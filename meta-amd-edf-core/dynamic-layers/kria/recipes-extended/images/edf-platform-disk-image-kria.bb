SUMMARY = "Platform Kria image for AMD EDF with OpenAMP"
DESCRIPTION = "AMD EDF Platform image for Kria with OpenAMP"

require recipes-extended/images/edf-disk-image.inc

require edf-disk-image-kria-common.inc

IMAGE_INSTALL += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'packagegroup-xen', '', d)} \
    ${AMD-EDF_PLATFORM_INSTALL} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'openamp', 'k24-openamp-fw-examples k26-openamp-fw-examples', '', d)} \
    "

# EFI boot files - with Xen entries when enabled
EDF_LINUX_EFI_BOOT_FILES:aarch64 = " \
    loader/loader.conf;loader/loader.conf \
    loader/edf-linux.conf;loader/entries/edf-linux.conf \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen.cfg xen.efi loader/edf-xen.conf;loader/entries/edf-xen.conf', '', d)} \
    "

IMAGE_EFI_BOOT_FILES += "${EDF_LINUX_EFI_BOOT_FILES}"
