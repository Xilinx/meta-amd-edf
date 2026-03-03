LICENSE ?= "MIT"

require edf-image-common.inc

IMAGE_FEATURES += "ssh-server-openssh package-management"

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    ${AMD-EDF_IMAGE_FULL_INSTALL} \
    "

inherit core-image
inherit amd-edf-wic-efi

do_rootfs[prefuncs] += "edf_check_rootfs"

# Reset the IMAGE_FSTYPES to only those we support
# cpio is NOT supported, this image will be too large for a ramdisk!
IMAGE_FSTYPES = "tar.gz wic wic.xz wic.bmap${@' wic.qemu-sd' if bb.data.inherits_class('image-types-xilinx-qemu', d) else ''}"

# Microblaze-v doesn't typically have SD
IMAGE_FSTYPES:microblaze-v = "tar.gz"

# Add the UFS (4k) ones when required for versal-net and versal-2ve-2vm
IMAGE_FSTYPES:append:versal-net = " wic.ufs wic.ufs.xz wic.ufs.bmap"
IMGCLASSES:append:versal-net = " image_types_ufs"

IMAGE_FSTYPES:append:versal-2ve-2vm = " wic.ufs wic.ufs.xz wic.ufs.bmap"
IMGCLASSES:append:versal-2ve-2vm = " image_types_ufs"

# WIC bootloader dependencies for aarch64 EFI boot
WKS_FILE_DEPENDS_BOOTLOADERS:aarch64 = "systemd-boot virtual-systemd-bootconf"

# WKS file selection - EFI for aarch64, default for others
WKS_FILES = "edf-disk-single-rootfs.wks"
WKS_FILES:aarch64 = "edf-disk-single-rootfs-efi.wks"

# Configure QEMU boot
QB_KERNEL_ROOT:riscv32 = "/dev/vda3"
QB_KERNEL_ROOT:riscv64 = "/dev/vda3"
QB_KERNEL_ROOT:zynq = "/dev/mmcblk0p3"
