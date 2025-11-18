inherit amd-edf-wic-efi
require recipes-core/images/edf-systemd-service.inc

COMPATIBLE_MACHINE:amd-cortexa53-mali-common = "${MACHINE}"

# Do not build an initramfs when targeting the shared amd-cortexa53-mali-common machine
INITRAMFS_IMAGE:amd-cortexa53-mali-common = ""
INITRAMFS_IMAGE_BUNDLE:amd-cortexa53-mali-common = ""

UBOOT_BOOT_SCRIPT:amd-cortexa53-mali-common = ""
UBOOT_BOOT_SCRIPT_SOC_DEFAULT:amd-cortexa53-mali-common = ""
EXTRA_IMAGEDEPENDS:remove:amd-cortexa53-mali-common = "virtual/bootloader u-boot-xlnx-uenv u-boot-edf-scr"

IMAGE_FSTYPES:append:amd-cortexa53-mali-common = " wic wic.xz"
IMAGE_BOOT_FILES:remove:amd-cortexa53-mali-common = "boot.scr"
IMAGE_INSTALL:append:amd-cortexa53-mali-common = " systemd-conf-edf"

WKS_FILE:amd-cortexa53-mali-common = "edf-disk-single-rootfs-efi.wks"
WKS_FILES:amd-cortexa53-mali-common = "${WKS_FILE}"

ADDN_IMAGE_RDEPENDS ??= ""
ADDN_IMAGE_RDEPENDS:amd-cortexa53-mali-common = "virtual-systemd-bootconf:do_deploy"
do_rootfs[rdepends] += "${ADDN_IMAGE_RDEPENDS}"

IMAGE_EFI_BOOT_FILES:amd-cortexa53-mali-common = " \
    loader/loader.conf;loader/loader.conf \
    loader/edf-linux.conf;loader/entries/edf-linux.conf \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', "loader/edf-xen.conf;loader/entries/edf-xen.conf", '', d)} \
"

WKS_FILE_DEPENDS:append:amd-cortexa53-mali-common = " ${EFI_PROVIDER}"
