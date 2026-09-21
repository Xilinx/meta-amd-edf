FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"

SRC_URI += " \
    file://edf-linux-boot.cmd \
    "

# Default EDF boot script is set to edf-linux-boot.cmd
DEFAULT_UBOOT_XLNX_BOOT_SCR ?= "edf-linux-boot.cmd"

# EDF disk images use a 3-partition layout: /efi (1), storage (2), rootfs (3).
ROOT_PARTNUM = "3"
