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

# WIC bootloader dependencies
EDF_BOOTLOADER_DEPS = "${@bb.utils.contains('COMBINED_FEATURES', 'efi', \
    'systemd-boot virtual-systemd-bootconf', '${UBOOT_BOOT_SCRIPT}', d)}"
WKS_FILE_DEPENDS_BOOTLOADERS:aarch64 = "${EDF_BOOTLOADER_DEPS}"

# WKS file selection - EFI for aarch64, default for others
WKS_FILES = "edf-disk-single-rootfs.wks"
WKS_FILES:aarch64 = "${@bb.utils.contains('COMBINED_FEATURES', 'efi', \
    'edf-disk-single-rootfs-efi.wks', 'edf-disk-single-rootfs.wks', d)}"

# Distro boot: deploy boot script and optional boot.bin to the boot partition
EDF_DISTROBOOT_ESP_FILES ?= "${@'boot.bin ' if d.getVar('SOC_ON_DISK_BOOT_BIN') == '1' else ''}boot.scr"
IMAGE_BOOT_FILES_label-esp = "${@'' if bb.utils.contains('COMBINED_FEATURES', 'efi', True, False, d) else '${EDF_DISTROBOOT_ESP_FILES}'}"
WICVARS:append = "${@'' if bb.utils.contains('COMBINED_FEATURES', 'efi', True, False, d) else ' IMAGE_BOOT_FILES_label-esp'}"
WICUFSVARS:append = "${@'' if bb.utils.contains('COMBINED_FEATURES', 'efi', True, False, d) else ' IMAGE_BOOT_FILES_label-esp'}"

# Configure QEMU boot
QB_KERNEL_ROOT:riscv32 = "/dev/vda3"
QB_KERNEL_ROOT:riscv64 = "/dev/vda3"
QB_KERNEL_ROOT:zynq = "/dev/mmcblk0p3"
