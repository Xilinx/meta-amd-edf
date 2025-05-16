# Adjust the variables for edf xen dom0less boot
# See https://xenbits.xen.org/docs/unstable/misc/arm/device-tree/booting.txt
# Include all the common dom0 configuration from edf-xen-boot-env.inc and add
# additional config for dom0less boot and domU guest.
include edf-xen-boot-env.inc

SRC_URI += "\
    file://edf-xen-dom0less-boot.cmd \
    "

# Image Load Address for Xen Dom0 boot
# Xen Dom0 kernel image is loaded at 0xE00000
# Xen Dom0 ramdisk image is loaded at 0x2A00000
# Xen DomU0 kernel image is loaded at 0xAA00000
# Xen DomU0 ramdisk image is loaded at 0xC600000
# Based on above image loadded address XEN_LOAD_ADDRESS needs to be adjusted.
XEN_LOAD_ADDRESS ?= "0xF800000"

# If domU0, Kernel and ramdisk is used for Xen dom0less boot
XEN_DOMU0_KERNEL_IMAGETYPE ?= "Image"
XEN_DOMU0_RAMDISK_IMAGETYPE ?= "core-image-minimal-${MACHINE}.rootfs.cpio.gz"

# Kernel and ramdisk load address for Xen DomU0 Linux Guest.
XEN_DOMU0_KERNEL_LOAD_ADDRESS ?= "0xAA00000"
XEN_DOMU0_RAMDISK_LOAD_ADDRESS ?= "0xC600000"

# Set the amount of memory for domU depending on total available memory size(DDR).
# Set 800M(0xC8000) for DomU0.
DOMU0_MEM ?= "0xC8000"

# Specify the max number of vcpus for domU
# Example usage: DOMU0_MAX_VCPUS = "2" or DOMU0_MAX_VCPUS = "2-4"
DOMU0_MAX_VCPUS ?= "1"

# Add xen variables as addendum.
EDF_XEN_DOM0LESS_SCRIPT_SED_ADDENDUM +=  "\
        ${EDF_XEN_SCRIPT_SED_ADDENDUM} \
        -e 's/@@XEN_DOMU0_KERNEL_IMAGETYPE@@/${XEN_DOMU0_KERNEL_IMAGETYPE}/' \
        -e 's/@@XEN_DOMU0_RAMDISK_IMAGETYPE@@/${XEN_DOMU0_RAMDISK_IMAGETYPE}/' \
        -e 's/@@DOMU0_MEM@@/${DOMU0_MEM}/' \
        -e 's/@@DOMU0_MAX_VCPUS@@/${DOMU0_MAX_VCPUS}/' \
        -e 's:@@XEN_DOMU0_KERNEL_LOAD_ADDRESS@@:${XEN_DOMU0_KERNEL_LOAD_ADDRESS}:' \
        -e 's:@@XEN_DOMU0_RAMDISK_LOAD_ADDRESS@@:${XEN_DOMU0_RAMDISK_LOAD_ADDRESS}:' \
        -e "s:@@XEN_DOMU0_KERNEL_NODE_UADDR@@:$(echo ${XEN_DOMU0_KERNEL_LOAD_ADDRESS} | sed 's/0x//g'):" \
        -e "s:@@XEN_DOMU0_RAMDISK_NODE_UADDR@@:$(echo ${XEN_DOMU0_RAMDISK_LOAD_ADDRESS} | sed 's/0x//g'):" \
"
