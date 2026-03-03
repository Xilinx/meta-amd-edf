SUMMARY = "Full platform image for AMD EDF with Xen and OpenAMP"
DESCRIPTION = "AMD EDF Platform image extending Linux base with Xen/OpenAMP"

require edf-disk-image.inc

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:aarch64 = ".*"

IMAGE_INSTALL += "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'packagegroup-xen', '', d)} \
    ${AMD-EDF_PLATFORM_INSTALL} \
    "

# EFI boot files - with Xen entries when enabled
EDF_LINUX_EFI_BOOT_FILES = ""
EDF_LINUX_EFI_BOOT_FILES:aarch64 = " \
    loader/loader.conf;loader/loader.conf \
    loader/edf-linux.conf;loader/entries/edf-linux.conf \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen.cfg xen.efi loader/edf-xen.conf;loader/entries/edf-xen.conf', '', d)} \
    "

IMAGE_EFI_BOOT_FILES += "${EDF_LINUX_EFI_BOOT_FILES}"
