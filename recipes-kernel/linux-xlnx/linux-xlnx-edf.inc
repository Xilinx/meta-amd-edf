FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Limit USB gadget to aarch64 only to avoid zynq USB host :CHIPIDEA=m conflict with 'usbgadget'
AMD_FPGA_USBGADGET = ""
AMD_FPGA_USBGADGET:aarch64 = "${@bb.utils.contains('COMBINED_FEATURES', 'usbgadget', \
    ' features/usb/usb-gadgets.scc features/usb-gadgets-extra/usb-gadgets-extra.scc', '', d)}"

KERNEL_FEATURES:append = " ${AMD_FPGA_USBGADGET}"

# Audit is required or systemd can fail to start the journal
KERNEL_FEATURES:append = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', \
    ' cgl/features/audit/audit.scc', '', d)}"

# Virtualization and container support: eBPF, network bridging, Kubernetes
KERNEL_FEATURES:append = "${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', \
    ' features/bpf/bpf.scc cfg/net/bridge.scc cfg/kubernetes.scc', '', d)}"

# Various kernel debug features
EDF_KERNEL_DEBUG_FEATURES = " \
    cfg/debug/printk/debug-dynamic-debug.scc \
    features/kfence/kfence.scc \
    cfg/debug/irq/debug-shirq.scc \
    cfg/debug/misc/debug-panic-oops.scc \
    cfg/debug/lock_hang/debug-wq-watchdog.scc \
    cfg/debug/tracer/debug-kernel-func.scc \
    features/coresight/debug-coresight.scc \
    features/coresight/etm-debug.scc \
    features/debug/debug-info-dwarf5.scc \
    features/debug/lockup.scc \
    features/debug/rcu-debug.scc \
    features/debug/sanitizer.scc \
    features/drm/drm-debug.scc \
    features/tracing/debug-tracing.scc \
    "
KERNEL_FEATURES:append = "${@bb.utils.contains('ENABLE_KERNEL_DEBUG', '1', \
    ' ${EDF_KERNEL_DEBUG_FEATURES}', '', d)}"

# USB wifi drivers (upstream scc + edf-kmeta iwd crypto) and local supplement
SRC_URI += "${@bb.utils.contains('COMBINED_FEATURES', 'wifi', \
    ' file://wifi-usb-extra.cfg', '', d)}"
KERNEL_FEATURES:append = "${@bb.utils.contains('COMBINED_FEATURES', 'wifi', \
    ' features/iwd/iwd-crypto.scc features/wifi/wifi-usb.scc', '', d)}"

pkg_postinst:${KERNEL_PACKAGE_NAME}-image:append () {
    # Only run on the live target so we don't depend on /efi during rootfs builds.
    if [ -n "$D" ]; then
        exit 0
    fi

    boot_dir="/boot"
    efi_dir="/efi"

    if [ ! -d "$boot_dir" ]; then
        echo "${PN}: /boot missing; skipping ESP sync."
        exit 0
    fi

    if [ ! -d "$efi_dir" ]; then
        echo "${PN}: /efi missing; skipping ESP sync."
        exit 0
    fi

    if ! grep -qs " ${efi_dir} " /proc/mounts; then
        echo "${PN}: /efi not mounted; skipping ESP sync."
        exit 0
    fi

    # Copy the configured kernel images into the ESP.
    for img_type in ${KERNEL_IMAGETYPES}; do
        img_path="${boot_dir}/${img_type}"
        [ -e "$img_path" ] || continue
        dest_path="${efi_dir}/$(basename "$img_path")"
        install -m 0644 "$img_path" "$dest_path"
    done
}
