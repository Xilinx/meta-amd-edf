FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "${@bb.utils.contains('COMBINED_FEATURES', 'usbgadget', \
    ' file://usb-gadgets-extra.cfg', '', d)}"
KERNEL_FEATURES += "${@bb.utils.contains('COMBINED_FEATURES', 'usbgadget', \
    ' features/usb/usb-gadgets.scc', '', d)}"

# Audit is required or systemd can fail to start the journal
KERNEL_FEATURES:append = " cgl/features/audit/audit.cfg"

# Virtualization and container support: eBPF, network bridging, Kubernetes
KERNEL_FEATURES:append = "${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', \
    ' features/bpf/bpf.scc cfg/net/bridge.scc cfg/kubernetes.scc', '', d)}"

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
