# Adjust the variables for edf xen boot

SRC_URI += "file://edf-xen-boot.cmd"

inherit xen-launch-config

# Image Load Address for Xen Dom0 boot
XEN_KERNEL_LOAD_ADDRESS ??= "0xE00000"
XEN_LOAD_ADDRESS ??= "0xBA00000"
XEN_DEVICETREE_LOAD_ADDRESS ??= "0xC000000"
XEN_RAMDISK_LOAD_ADDRESS ??= "0x2600000"

# If dom0-ramdisk is used for Xen boot then set RAMDISK image to cpio.gz
KERNEL_IMAGETYPE ?= "Image"
XEN_IMAGETYPE ?= "xen"
RAMDISK_IMAGETYPE ?= "edf-xen-image-full-cmdline-${MACHINE}.rootfs.cpio.gz"

# Set SDBOOTDEV
SDBOOTDEV ?= "0"

# Default to boot partition number where xen and xen_boot.scr files are stored in SD/eMMC partition
BOOTPARTNUM ?= "2"

# Default to booting with the xen rootfs device being partition 4 for SD/eMMC
ROOTPARTNUM ?= "4"

# Set Kernel root filesystem parameter for SD/eMMC boot
# Bootdev will automatically be set to 'sda' or 'mmcblkXp'
KERNEL_ROOT_SD ?= "root=/dev/\${bootdev}${ROOTPARTNUM} ro rootwait"

# Set Kernel root filesystem parameter for using Xen RAMDISK boot
KERNEL_ROOT_RAMDISK ?= "root=/dev/ram0 rw"

# Set the amount of memory for dom0 depending on total available memory size(DDR).
DOM0_MEM ?= "1500M"

# Specify which UART console Xen should use. You can sepecify the devicetree
# alias or full path to a node in the devicetree
# XEN_SERIAL_CONSOLES ?= "serial0" or XEN_SERIAL_CONSOLES ?= "/axi/serial@ff000000"
XEN_SERIAL_CONSOLES ?= "serial0"

# Specify additional Xen paramaters which will be appended to xen-bootargs
# This can alos be used for debugging purposes.
# Examples: XEN_CMDLINE_APPEND ?= "sched=credit loglvl=all guest_loglvl=debug"
XEN_CMDLINE_APPEND ?= ""

# Specify the max number of vcpus for dom0
# Example usage: DOM0_MAX_VCPUS = "2" or DOM0_MAX_VCPUS = "2-4"
DOM0_MAX_VCPUS ?= "1"

# Add Xen EDF variables as addendum.
EDF_XEN_SCRIPT_SED_ADDENDUM +=   "-e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@XEN_IMAGETYPE@@/${XEN_IMAGETYPE}/' \
        -e 's/@@RAMDISK_IMAGETYPE@@/${RAMDISK_IMAGETYPE}/' \
        -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        -e 's/@@DOM0_MEM@@/${DOM0_MEM}/' \
        -e 's/@@DOM0_MAX_VCPUS@@/${DOM0_MAX_VCPUS}/' \
        -e 's/@@XEN_LOAD_ADDRESS@@/${XEN_LOAD_ADDRESS}/' \
        -e 's/@@RAMDISK_LOAD_ADDRESS@@/${RAMDISK_LOAD_ADDRESS}/' \
        -e 's:@@XEN_KERNEL_LOAD_ADDRESS@@:${XEN_KERNEL_LOAD_ADDRESS}:' \
        -e 's:@@XEN_DEVICETREE_LOAD_ADDRESS@@:${XEN_DEVICETREE_LOAD_ADDRESS}:' \
        -e 's:@@XEN_RAMDISK_LOAD_ADDRESS@@:${XEN_RAMDISK_LOAD_ADDRESS}:' \
        -e 's:@@XEN_CMDLINE_APPEND@@:${XEN_CMDLINE_APPEND}:' \
        -e 's:@@XEN_SERIAL_CONSOLES@@:${XEN_SERIAL_CONSOLES}:' \
        -e 's/@@BOOTPARTNUM@@/${BOOTPARTNUM}/' \
        -e 's/@@ROOTPARTNUM@@/${ROOTPARTNUM}/' \
        -e 's:@@KERNEL_ROOT_SD@@:${KERNEL_ROOT_SD}:' \
        -e 's:@@KERNEL_ROOT_RAMDISK@@:${KERNEL_ROOT_RAMDISK}:' \
        -e 's/@@SDBOOTDEV@@/${SDBOOTDEV}/' \
        -e "s:@@XEN_DOM0_KERNEL_NODE_UADDR@@:$(echo ${XEN_KERNEL_LOAD_ADDRESS} | sed 's/0x//g'):" \
        -e "s:@@XEN_DOM0_RAMDISK_NODE_UADDR@@:$(echo ${XEN_RAMDISK_LOAD_ADDRESS} | sed 's/0x//g'):" \
"
