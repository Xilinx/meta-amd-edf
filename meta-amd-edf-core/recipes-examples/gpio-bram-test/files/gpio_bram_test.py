#!/usr/bin/env python3
# Copyright (C) 2026 Advanced Micro Devices, Inc.
# SPDX-License-Identifier: MIT

"""GPIO BRAM PL example test for AMD EDF reference platforms.

Validates the GPIO-BRAM example PL payload by:
  1. Performing a read/write/restore validation of the BRAM aperture via
     python-periphery MMIO.
  2. Toggling output GPIO lines via python-periphery GPIO API.
  3. Optionally validating input GPIO lines (for DIP switches and push
     buttons) by detecting state transitions during a timeout window.

Address and gpiochip selection are auto-discovered where possible and may
be overridden from the environment for platforms whose example payload
differs:
    GPIO_BRAM_ADDR          (default 0xB0000000)
    GPIO_BRAM_SIZE          (default 0x1000)
    GPIO_BRAM_CHIP          (default: auto-detected via /sys/class/gpio label;
                           selects the first PL AXI GPIO chip for output toggle)
    GPIO_BRAM_OUT_LINES     (default "0 1 2 3")
    GPIO_BRAM_LINES         (legacy alias for GPIO_BRAM_OUT_LINES)
    GPIO_BRAM_IN_LINES      (default ""; auto-detected across ALL PL chips if unset)
    GPIO_BRAM_INPUT_TIMEOUT (default 10 seconds)
    GPIO_BRAM_INPUT_POLL_MS (default 100 milliseconds)
    GPIO_BRAM_AUTODETECT_WINDOW (default 6 seconds)
    GPIO_BRAM_MAX_SCAN_LINES (default 32)
"""

import glob
import os
import re
import sys
import time

from periphery import GPIO, MMIO

BRAM_ADDR_ENV = os.environ.get("GPIO_BRAM_ADDR")
BRAM_ADDR = int(BRAM_ADDR_ENV, 0) if BRAM_ADDR_ENV else None
BRAM_SIZE = int(os.environ.get("GPIO_BRAM_SIZE", "0x1000"), 0)
GPIO_OUT_LINES = [
    int(x, 0)
    for x in os.environ.get(
        "GPIO_BRAM_OUT_LINES", os.environ.get("GPIO_BRAM_LINES", "0 1 2 3")
    ).split()
]
GPIO_IN_LINES = [int(x, 0) for x in os.environ.get("GPIO_BRAM_IN_LINES", "").split()]
GPIO_INPUT_TIMEOUT = float(os.environ.get("GPIO_BRAM_INPUT_TIMEOUT", "10"))
GPIO_INPUT_POLL_S = max(
    0.01, float(os.environ.get("GPIO_BRAM_INPUT_POLL_MS", "100")) / 1000.0
)
GPIO_AUTODETECT_WINDOW = float(os.environ.get("GPIO_BRAM_AUTODETECT_WINDOW", "6"))
GPIO_MAX_SCAN_LINES = int(os.environ.get("GPIO_BRAM_MAX_SCAN_LINES", "32"))
SKIP_MMIO = os.environ.get("GPIO_BRAM_SKIP_MMIO", "0") == "1"


HELP_TEXT = """Usage: gpio_bram_test.py [OPTIONS]

GPIO-BRAM test utility for AMD EDF platforms.

What this script does:
  1) MMIO BRAM validation
    - Auto-discovers BRAM range (or uses GPIO_BRAM_ADDR/GPIO_BRAM_SIZE)
    - Reads original value, writes test patterns, verifies readback,
      and restores original value

  2) GPIO output toggle test
    - Auto-discovers a PL gpiochip (or uses GPIO_BRAM_CHIP)
    - Toggles configured output lines HIGH/LOW

  3) GPIO input transition test
    - Uses GPIO_BRAM_IN_LINES if set; otherwise auto-detects changing lines
      across PL gpiochips
    - Waits for DIP/button transitions and prints per-line results

Options:
  -h, --help      Show this help message and exit.
  --all           Run all test blocks.
                  This is the default when no block option is provided.
  --mmio          Run only the MMIO BRAM validation block.
  --gpio-output   Run only the GPIO output toggle block (drive lines).
  --gpio-input    Run only the GPIO input transition block (monitor lines).

Examples:
  gpio_bram_test.py --gpio-output
  gpio_bram_test.py --mmio --gpio-input
"""


def _is_likely_non_pl_gpio(label):
    # Exclude PS-side and non-PL GPIO controllers by label pattern.
    # Labels starting with "pmc" are platform-management-controller GPIO (PS side).
    # Labels containing "zynqmp" are PS GPIO controllers on Zynq UltraScale+ devices.
    # Labels containing "pca" are I2C GPIO expanders (e.g. PCA9554/PCA9555 class).
    # All other address-based labels (e.g. "a4000000.gpio") are treated as PL AXI GPIO.
    lower_label = label.lower()
    return (
        "zynqmp" in lower_label
        or "pca" in lower_label
        or lower_label.startswith("pmc")
    )


def _read_text_file(path):
    try:
        with open(path, "r", encoding="utf-8") as file_handle:
            return file_handle.read().strip()
    except OSError:
        return ""


def _read_compatible_for_chip(chipdir):
    # Prefer sysfs of_node compatible when available.
    compat_path = "/sys/class/gpio/{}/device/of_node/compatible".format(chipdir)
    try:
        with open(compat_path, "rb") as file_handle:
            raw = file_handle.read()
            if raw:
                return (
                    raw.replace(b"\x00", b" ")
                    .decode("ascii", errors="ignore")
                    .strip()
                    .lower()
                )
    except OSError:
        pass
    return ""


def _is_pl_gpio_chip(label, compatible):
    lower_label = label.lower()
    lower_compat = compatible.lower()

    if lower_compat:
        # Strong signal from DT compatible for AXI GPIO IP.
        if "axi-gpio" in lower_compat or "xps-gpio" in lower_compat:
            return True
        # If compatible explicitly points to PS/non-PL blocks, exclude.
        if "zynqmp-gpio" in lower_compat or "pca" in lower_compat:
            return False

    # Fallback to label heuristics for systems where compatible is unavailable.
    return "gpio" in lower_label and not _is_likely_non_pl_gpio(lower_label)


def discover_all_pl_gpiochips_with_meta():
    """Return tuples of (chip path, label, compatible) for PL AXI GPIO chips.

    Scans /dev/gpiochip* directly so chips not exported via legacy sysfs
    (common on newer kernels with CONFIG_GPIO_SYSFS disabled) are also found.
    Falls back to opening the character device via periphery ioctl to read
    the label when the sysfs label file is absent.
    """
    chips = []
    for chip in sorted(glob.glob("/dev/gpiochip*")):
        chip_name = os.path.basename(chip)

        # Fast path: legacy sysfs label.
        label = _read_text_file("/sys/class/gpio/{}/label".format(chip_name)) or None

        # Fallback: read label via character device ioctl (periphery).
        if not label:
            try:
                gpio = GPIO(chip, 0, "in")
                label = gpio.chip_label
                gpio.close()
            except OSError:
                continue

        if not label:
            continue

        compatible = _read_compatible_for_chip(chip_name)

        if _is_pl_gpio_chip(label, compatible):
            chips.append((chip, label, compatible))

    return chips if chips else [("/dev/gpiochip1", "", "")]


def _parse_reg_first_range(reg_bytes):
    if not reg_bytes or len(reg_bytes) % 4 != 0:
        return None

    words = [
        int.from_bytes(reg_bytes[i : i + 4], "big")
        for i in range(0, len(reg_bytes), 4)
    ]

    if len(words) >= 4:
        addr = (words[0] << 32) | words[1]
        size = (words[2] << 32) | words[3]
        return (addr, size)

    if len(words) >= 2:
        return (words[0], words[1])

    return None


def discover_bram_range():
    if BRAM_ADDR is not None:
        return (BRAM_ADDR, BRAM_SIZE)

    # Prefer DT nodes that explicitly describe an AXI BRAM controller.
    dt_root = "/proc/device-tree"
    if os.path.isdir(dt_root):
        for compat_path in sorted(
            glob.glob(dt_root + "/**/compatible", recursive=True)
        ):
            try:
                with open(compat_path, "rb") as file_handle:
                    compat = (
                        file_handle.read()
                        .replace(b"\x00", b" ")
                        .decode("ascii", errors="ignore")
                        .lower()
                    )
            except OSError:
                continue

            if "axi-bram-ctrl" not in compat and "bram" not in compat:
                continue

            reg_path = os.path.join(os.path.dirname(compat_path), "reg")
            try:
                with open(reg_path, "rb") as file_handle:
                    parsed = _parse_reg_first_range(file_handle.read())
            except OSError:
                parsed = None

            if parsed and parsed[1] > 0:
                return parsed

    # Fallback to iomem labels if DT parsing did not find a BRAM node.
    try:
        with open("/proc/iomem", "r", encoding="utf-8") as file_handle:
            for line in file_handle:
                lower = line.lower()
                if "bram" not in lower:
                    continue
                match = re.match(
                    r"^\s*([0-9a-f]+)-([0-9a-f]+)\s*:\s*(.+)$", lower
                )
                if not match:
                    continue
                start = int(match.group(1), 16)
                end = int(match.group(2), 16)
                size = (end - start) + 1
                if size > 0:
                    return (start, size)
    except OSError:
        pass

    return None


def discover_gpiochip():
    """Return the first /dev/gpiochipN that looks like a PL AXI GPIO output chip."""
    override = os.environ.get("GPIO_BRAM_CHIP")
    if override:
        if not override.startswith("/dev/"):
            override = "/dev/" + override
        return override

    chips = discover_all_pl_gpiochips_with_meta()
    if chips:
        return chips[0][0]

    return "/dev/gpiochip1"


def discover_all_pl_gpiochips():
    """Return all /dev/gpiochipN devices that look like PL AXI GPIO chips.

    Unlike discover_gpiochip() which stops at the first match, this collects
    every chip so the input test can scan DIP switches and buttons that may
    be wired to a different AXI GPIO channel (e.g. gpiochip2, gpiochip3).
    """
    return [chip for chip, _, _ in discover_all_pl_gpiochips_with_meta()]


def _chip_line_count(chip):
    chip_name = os.path.basename(chip)
    # Try legacy sysfs ngpio first.
    try:
        with open(
            "/sys/class/gpio/{}/ngpio".format(chip_name),
            "r",
            encoding="utf-8",
        ) as file_handle:
            return int(file_handle.read().strip())
    except (OSError, ValueError):
        pass
    # Fallback: try the platform bus device path for line count.
    for path in glob.glob(
        "/sys/bus/platform/devices/*/gpio/{}/ngpio".format(chip_name)
    ):
        try:
            with open(path, "r", encoding="utf-8") as file_handle:
                return int(file_handle.read().strip())
        except (OSError, ValueError):
            continue
    return GPIO_MAX_SCAN_LINES


def autodetect_input_lines(chips, exclude_lines_on_first_chip):
    """Scan all PL GPIO chips for lines that change during the detection window.

    Args:
        chips: chip paths to scan (e.g. ["/dev/gpiochip1", "/dev/gpiochip2"])
        exclude_lines_on_first_chip: lines to skip on the first (output) chip

    Returns:
        list of (chip, line) tuples that showed a state transition
    """
    # Build a flat list of (chip, line) candidates across all PL chips.
    # On the first (output) chip exclude the already-driven output lines.
    candidates = []
    for idx, chip in enumerate(chips):
        line_count = _chip_line_count(chip)
        scan_count = min(line_count, GPIO_MAX_SCAN_LINES)
        skip = exclude_lines_on_first_chip if idx == 0 else set()
        for line in range(scan_count):
            if line not in skip:
                candidates.append((chip, line))

    handles = {}
    baseline = {}
    changed = set()

    for chip, line in candidates:
        try:
            gpio = GPIO(chip, line, "in")
            handles[(chip, line)] = gpio
            baseline[(chip, line)] = bool(gpio.read())
        except OSError:
            continue

    if not handles:
        return []

    chip_list_str = ", ".join(chips)
    print(
        "Auto-detecting input lines across PL chips [{}]. "
        "Actuate DIP/buttons now for {:.1f}s...".format(
            chip_list_str, GPIO_AUTODETECT_WINDOW
        )
    )

    deadline = time.time() + GPIO_AUTODETECT_WINDOW
    while time.time() < deadline:
        for key, gpio in handles.items():
            if key not in changed:
                current = bool(gpio.read())
                if current != baseline[key]:
                    changed.add(key)
        time.sleep(GPIO_INPUT_POLL_S)

    for gpio in handles.values():
        gpio.close()

    return sorted(changed)


def memory_test():
    if SKIP_MMIO:
        print("=== Memory Read/Write Validation (MMIO) ===")
        print("Skipping MMIO test because GPIO_BRAM_SKIP_MMIO=1\n")
        return

    bram_range = discover_bram_range()
    if not bram_range:
        print("=== Memory Read/Write Validation (MMIO) ===")
        print(
            "Could not auto-detect BRAM address/size. "
            "Skipping MMIO test to avoid invalid bus access. "
            "Set GPIO_BRAM_ADDR/GPIO_BRAM_SIZE to force it.\n"
        )
        return

    bram_addr, bram_size = bram_range
    # Keep a sane lower bound for the mapping to include offset 0x00 safely.
    bram_size = max(bram_size, 0x1000)

    print("=== Memory Read/Write Validation (MMIO) ===")
    print("Using BRAM range: 0x{:X} size 0x{:X}".format(bram_addr, bram_size))
    mmio = MMIO(bram_addr, bram_size)
    try:
        original_val = mmio.read32(0x00)
        print("Original value at 0x{:X}: 0x{:08X}".format(bram_addr, original_val))

        for test_val in (0xDEADBEEF, 0x12345678, 0x87654321):
            mmio.write32(0x00, test_val)
            print("Wrote test value: 0x{:08X}".format(test_val))
            readback_val = mmio.read32(0x00)
            print("Read back value:  0x{:08X}".format(readback_val))
            if readback_val != test_val:
                print("MISMATCH at 0x{:X}!".format(bram_addr))

        mmio.write32(0x00, original_val)
        print("Restored original value.")
    finally:
        mmio.close()
    print()


def gpio_toggle_test():
    chip = discover_gpiochip()
    print("=== GPIO Toggle Test ({}) ===".format(chip))
    for line in GPIO_OUT_LINES:
        try:
            gpio = GPIO(chip, line, "out")
        except OSError as exc:
            print(
                "GPIO {}: SKIP - could not open line as output: {} "
                "(line may be busy or in use by another process)\n".format(line, exc)
            )
            continue
        try:
            print("GPIO {}: drive HIGH".format(line))
            gpio.write(True)
            time.sleep(1)
            print("GPIO {}: logic state now {}".format(line, int(gpio.read())))
            print("GPIO {}: drive LOW".format(line))
            gpio.write(False)
            time.sleep(1)
            print("GPIO {}: logic state now {}".format(line, int(gpio.read())))
            print("GPIO {} output toggle test complete.\n".format(line))
        finally:
            gpio.close()


def gpio_input_test():
    all_pl_chip_meta = discover_all_pl_gpiochips_with_meta()
    all_pl_chips = [chip for chip, _, _ in all_pl_chip_meta]
    out_chip = discover_gpiochip()
    chip_list_str = ", ".join(all_pl_chips)
    print("=== GPIO Input Test ({}) ===".format(chip_list_str))
    input_specs = (
        [(out_chip, line) for line in GPIO_IN_LINES] if GPIO_IN_LINES else []
    )

    if not input_specs:
        detected = autodetect_input_lines(all_pl_chips, set(GPIO_OUT_LINES))
        if detected:
            print("Auto-detected input lines:")
            for chip, line in detected:
                print("  {} line {}".format(chip, line))
            input_specs = detected
        else:
            print(
                "No input from switches was detected during auto-detect. "
                "No GPIO input state change was observed across any PL chip ({}).\n"
                "Actuate a DIP switch or button and re-run, or set "
                "GPIO_BRAM_IN_LINES explicitly.".format(chip_list_str)
            )
            return

    print("\nFor each line, actuate the mapped DIP switch or push button before timeout.")
    print(
        "Timeout per line: {:.1f}s, poll interval: {:.3f}s".format(
            GPIO_INPUT_TIMEOUT, GPIO_INPUT_POLL_S
        )
    )
    print(
        "Observed input states will be recorded below. If a switch changes, "
        "the transition will be printed immediately.\n"
    )

    pass_count = 0
    for chip, line in input_specs:
        try:
            gpio = GPIO(chip, line, "in")
        except OSError as exc:
            print(
                "{} line {}: SKIP - could not open as input: {}\n".format(
                    chip, line, exc
                )
            )
            continue
        try:
            initial = bool(gpio.read())
            deadline = time.time() + GPIO_INPUT_TIMEOUT
            print("{} line {} initial state: {}".format(chip, line, int(initial)))
            print("{} line {}: waiting for a state change...".format(chip, line))

            transitioned = False
            last_val = initial
            while time.time() < deadline:
                current = bool(gpio.read())
                if current != initial:
                    transitioned = True
                    last_val = current
                    break
                time.sleep(GPIO_INPUT_POLL_S)

            if transitioned:
                pass_count += 1
                print(
                    "{} line {}: detected transition {} -> {}\n".format(
                        chip, line, int(initial), int(last_val)
                    )
                )
            else:
                print(
                    "{} line {} FAIL: no transition within timeout; "
                    "current state {}\n".format(chip, line, int(last_val))
                )
        finally:
            gpio.close()

    print(
        "Input test summary: {}/{} lines observed transitions.\n".format(
            pass_count, len(input_specs)
        )
    )


def _parse_selected_blocks(argv):
    selected = set()

    for arg in argv[1:]:
        if arg in ("-h", "--help"):
            print(HELP_TEXT)
            return None
        if arg == "--all":
            selected.update(("mmio", "gpio-out", "gpio-in"))
            continue
        if arg == "--mmio":
            selected.add("mmio")
            continue
        if arg == "--gpio-output":
            selected.add("gpio-out")
            continue
        if arg == "--gpio-input":
            selected.add("gpio-in")
            continue

        print("Unknown option: {}\n".format(arg))
        print(HELP_TEXT)
        sys.exit(2)

    if not selected:
        selected.update(("mmio", "gpio-out", "gpio-in"))

    return selected


def main():
    selected = _parse_selected_blocks(sys.argv)
    if selected is None:
        return

    if "mmio" in selected:
        memory_test()
    if "gpio-out" in selected:
        gpio_toggle_test()
    if "gpio-in" in selected:
        gpio_input_test()


if __name__ == "__main__":
    main()
