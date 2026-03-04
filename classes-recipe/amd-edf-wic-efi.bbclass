#
# Common helpers for EDF images that produce EFI WIC artifacts.
#

# Ensure WIC gets the metadata it needs to populate the ESP.
WICVARS:append = " \
    WORKDIR \
    ROOTFS_PART_UUID \
    ROOTFS_PART_TYPE \
    ESP_PART_TYPE \
    EFI_PROVIDER \
    STORAGE_PART_TYPE \
    "

# Mirror the same metadata into the UFS-specific variable for Versal platforms.
WICUFSVARS:append = " \
    WORKDIR \
    ROOTFS_PART_UUID \
    ROOTFS_PART_TYPE \
    ESP_PART_TYPE \
    EFI_PROVIDER \
    STORAGE_PART_TYPE \
    "


python amd_edf_set_rootfs_uuid () {
    import uuid
    d.setVar("ROOTFS_PART_UUID", str(uuid.uuid4()))
}

# Prepend in an anonymous python section due to load / execution order
# do_rootfs_wicenv[prefuncs] += "amd_edf_set_rootfs_uuid" may get cleared
# when the image classes are loaded.
python () {
    d.appendVarFlag('do_rootfs_wicenv', 'prefuncs', ' amd_edf_set_rootfs_uuid')

    if d.getVarFlag('do_rootfs_wicufsenv', 'func'):
        d.appendVarFlag('do_rootfs_wicufsenv', 'prefuncs', ' amd_edf_set_rootfs_uuid')
}
