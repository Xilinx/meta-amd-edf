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
require recipes-core/images/edf-systemd-service.inc

do_rootfs[prefuncs] += "edf_check_rootfs"

# Reset the IMAGE_FSTYPES to only those we support
# cpio is NOT supported, this image will be too large for a ramdisk!
IMAGE_FSTYPES = "tar.gz wic wic.xz wic.bmap${@' wic.qemu-sd' if bb.data.inherits_class('image-types-xilinx-qemu', d) else ''}"

# Add the UFS (4k) ones when required
IMAGE_FSTYPES:append:versal-2ve-2vm = " wic.ufs wic.ufs.xz wic.ufs.bmap"

IMGCLASSES:append:versal-2ve-2vm = " image_types_ufs"

IMAGE_EFI_BOOT_FILES ?= ""
IMAGE_EFI_BOOT_FILES:versal ?= " \
    loader/loader.conf;loader/loader.conf \
    loader/edf-linux.conf;loader/entries/edf-linux.conf \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', "xen.cfg xen.efi loader/edf-xen.conf;loader/entries/edf-xen.conf", '', d)} \
    "
IMAGE_EFI_BOOT_FILES:versal-net ?= " \
    loader/loader.conf;loader/loader.conf \
    loader/edf-linux.conf;loader/entries/edf-linux.conf \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', "xen.cfg xen.efi loader/edf-xen.conf;loader/entries/edf-xen.conf", '', d)} \
    "
IMAGE_EFI_BOOT_FILES:versal-2ve-2vm ?= " \
    loader/loader.conf;loader/loader.conf \
    loader/edf-linux.conf;loader/entries/edf-linux.conf \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', "xen.cfg xen.efi loader/edf-xen.conf;loader/entries/edf-xen.conf", '', d)} \
    "

ADDN_IMAGE_RDEPENDS = ""
ADDN_IMAGE_RDEPENDS:versal = "virtual-systemd-bootconf:do_deploy"
ADDN_IMAGE_RDEPENDS:versal-net = "virtual-systemd-bootconf:do_deploy"
ADDN_IMAGE_RDEPENDS:versal-2ve-2vm = "virtual-systemd-bootconf:do_deploy"

do_rootfs[rdepends] += "${ADDN_IMAGE_RDEPENDS}"

WKS_FILES = "edf-disk-single-rootfs.wks"
WKS_FILES:versal = "edf-disk-single-rootfs-efi.wks"
WKS_FILES:versal-net = "edf-disk-single-rootfs-efi.wks"
WKS_FILES:versal-2ve-2vm = "edf-disk-single-rootfs-efi.wks"

# Configure QEMU boot
QB_KERNEL_ROOT:riscv32 = "/dev/vda3"
QB_KERNEL_ROOT:riscv64 = "/dev/vda3"
QB_KERNEL_ROOT:zynq = "/dev/mmcblk0p3"
