require ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'edf-virt.inc', '', d)}

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

EDF_DISTRO_INCLUDE = ""
EDF_DISTRO_INCLUDE:amd-edf = "linux-xlnx-edf.inc"

require ${EDF_DISTRO_INCLUDE}

# Audit is required or systemd can fail to start the journal
KERNEL_FEATURES:append = " cgl/features/audit/audit.cfg"

pkg_postinst:${KERNEL_PACKAGE_NAME}-image:append () {
    # Only run on the live target so we don't depend on /efi during rootfs builds.
    if [ -n "$D" ]; then
        exit 0
    fi

    boot_dir="/boot"
    efi_dir="/efi"

    if [ ! -d "$boot_dir" ]; then
        echo "${PN}: /boot missing; skipping ESP sync."
        exit 0
    fi

    if [ ! -d "$efi_dir" ]; then
        echo "${PN}: /efi missing; skipping ESP sync."
        exit 0
    fi

    if ! grep -qs " ${efi_dir} " /proc/mounts; then
        echo "${PN}: /efi not mounted; skipping ESP sync."
        exit 0
    fi

    # Copy the configured kernel images into the ESP.
    for img_type in ${KERNEL_IMAGETYPES}; do
        img_path="${boot_dir}/${img_type}"
        [ -e "$img_path" ] || continue
        dest_path="${efi_dir}/$(basename "$img_path")"
        install -m 0644 "$img_path" "$dest_path"
    done
}
