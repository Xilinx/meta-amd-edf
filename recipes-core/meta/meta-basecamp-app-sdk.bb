SUMMARY = "Package for building a installable toolchain for Basecamp SDK"
LICENSE = "MIT"

PR = "r0"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:versal-common = "${MACHINE}"
COMPATIBLE_MACHINE:versal2-common = "${MACHINE}"

inherit populate_sdk amd-qemu-xilinx-sdk-tools amd-misc-sdk-tools

TOOLCHAIN_TARGET_TASK:append = " \
    kernel-devsrc \
    xrt-dev \
    "
