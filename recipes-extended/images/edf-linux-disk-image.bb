SUMMARY = "A full featured console image for AMD Embedded Development Framework"
DESCRIPTION = "AMD Embedded Development Framework Linux image compositing rootfs and other binaries to a single wic image"

LICENSE ?= "MIT"

require edf-image-common.inc

IMAGE_FEATURES += "ssh-server-openssh package-management"

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    ${AMD-EDF_IMAGE_FULL_INSTALL} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'packagegroup-xen', '', d)} \
    "

inherit core-image
inherit amd-edf-wic-efi

do_rootfs[prefuncs] += "edf_check_rootfs"

# Reset the IMAGE_FSTYPES to only those we support
# cpio is NOT supported, this image will be too large for a ramdisk!
IMAGE_FSTYPES = "tar.gz wic wic.xz wic.bmap${@' wic.qemu-sd' if bb.data.inherits_class('image-types-xilinx-qemu', d) else ''}"

# Add the UFS (4k) ones when required for versal-2ve-2vm
IMAGE_FSTYPES:append:versal-2ve-2vm = " wic.ufs wic.ufs.xz wic.ufs.bmap"
IMGCLASSES:append:versal-2ve-2vm = " image_types_ufs"

# EFI boot files for all aarch64 platforms
IMAGE_EFI_BOOT_FILES ?= ""
IMAGE_EFI_BOOT_FILES:aarch64 ?= " \
    loader/loader.conf;loader/loader.conf \
    loader/edf-linux.conf;loader/entries/edf-linux.conf \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', "xen.cfg xen.efi loader/edf-xen.conf;loader/entries/edf-xen.conf", '', d)} \
    "

# systemd-boot configuration dependency for aarch64
ADDN_IMAGE_RDEPENDS = ""
ADDN_IMAGE_RDEPENDS:aarch64 = "virtual-systemd-bootconf:do_deploy"

do_rootfs[rdepends] += "${ADDN_IMAGE_RDEPENDS}"

# WKS file selection - EFI for aarch64, default for others
WKS_FILES = "edf-disk-single-rootfs.wks"
WKS_FILES:aarch64 = "edf-disk-single-rootfs-efi.wks"

# Configure QEMU boot
QB_KERNEL_ROOT:riscv32 = "/dev/vda3"
QB_KERNEL_ROOT:riscv64 = "/dev/vda3"
QB_KERNEL_ROOT:zynq = "/dev/mmcblk0p3"
