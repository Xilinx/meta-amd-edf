SUMMARY = "Linux-only console image for AMD Embedded Development Framework"
DESCRIPTION = "AMD EDF Linux image without Xen or OpenAMP"

require edf-disk-image.inc

# EFI boot files - Linux-only (no Xen entries)
EDF_LINUX_EFI_BOOT_FILES = ""
EDF_LINUX_EFI_BOOT_FILES:aarch64 = " \
    loader/loader.conf;loader/loader.conf \
    loader/edf-linux.conf;loader/entries/edf-linux.conf \
    "

IMAGE_EFI_BOOT_FILES += "${EDF_LINUX_EFI_BOOT_FILES}"
