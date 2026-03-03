SUMMARY = "Linux-only Kria image for AMD EDF"
DESCRIPTION = "AMD EDF Linux image for Kria without OpenAMP"

require recipes-extended/images/edf-disk-image.inc

require edf-disk-image-kria-common.inc

# EFI boot files - Linux-only (no Xen entries)
EDF_LINUX_EFI_BOOT_FILES = ""
EDF_LINUX_EFI_BOOT_FILES:aarch64 = " \
    loader/loader.conf;loader/loader.conf \
    loader/edf-linux.conf;loader/entries/edf-linux.conf \
    "

IMAGE_EFI_BOOT_FILES += "${EDF_LINUX_EFI_BOOT_FILES}"
