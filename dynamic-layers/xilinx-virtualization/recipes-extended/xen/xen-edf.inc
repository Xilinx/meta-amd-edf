FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "\
    file://zynqmp-xen-efi-config \
    file://versal-xen-efi-config \
    file://versal-net-xen-efi-config \
    file://versal-2ve-2vm-xen-efi-config \
    "

# Any machine or soc specific override requires a different package_arch
PACKAGE_ARCH:zynqmp = "${MACHINE_ARCH}"
PACKAGE_ARCH:versal = "${MACHINE_ARCH}"
PACKAGE_ARCH:versal-net = "${MACHINE_ARCH}"
PACKAGE_ARCH:versal-2ve-2vm = "${MACHINE_ARCH}"

do_install:append:zynqmp() {
    install -m 0644 ${WORKDIR}/zynqmp-xen-efi-config ${D}/boot/xen.cfg
}

do_install:append:versal() {
    install -m 0644 ${WORKDIR}/versal-xen-efi-config ${D}/boot/xen.cfg
}

do_install:append:versal-net() {
    install -m 0644 ${WORKDIR}/versal-net-xen-efi-config ${D}/boot/xen.cfg
}

do_install:append:versal-2ve-2vm() {
    install -m 0644 ${WORKDIR}/versal-2ve-2vm-xen-efi-config ${D}/boot/xen.cfg
}

FILES:${PN}-efi += "/boot/xen.cfg"

do_deploy:append:zynqmp() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${WORKDIR}/zynqmp-xen-efi-config ${DEPLOYDIR}/xen.cfg
}

do_deploy:append:versal() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${WORKDIR}/versal-xen-efi-config ${DEPLOYDIR}/xen.cfg
}

do_deploy:append:versal-net() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${WORKDIR}/versal-net-xen-efi-config ${DEPLOYDIR}/xen.cfg
}

do_deploy:append:versal-2ve-2vm() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${WORKDIR}/versal-2ve-2vm-xen-efi-config ${DEPLOYDIR}/xen.cfg
}

pkg_postinst:${PN}-efi:append () {
    # Only run on the live target so we don't depend on /efi during rootfs builds.
    if [ -n "$D" ]; then
        exit 0
    fi

    boot_dir="/boot"
    efi_dir="/efi"

    if [ ! -d "$boot_dir" ]; then
        echo "${PN}-efi: /boot missing; skipping ESP sync."
        exit 0
    fi

    if [ ! -d "$efi_dir" ]; then
        echo "${PN}-efi: /efi missing; skipping ESP sync."
        exit 0
    fi

    if ! grep -qs " ${efi_dir} " /proc/mounts; then
        echo "${PN}-efi: /efi not mounted; skipping ESP sync."
        exit 0
    fi

    rootfs_uuid=""
    if [ -r /proc/cmdline ]; then
        rootfs_uuid="$(sed -n 's/.*root=PARTUUID=\([^ ]*\).*/\1/p' /proc/cmdline)"
    fi

    cfg_src="${boot_dir}/xen.cfg"
    if [ -f "$cfg_src" ]; then
        cfg_dest="${efi_dir}/xen.cfg"

        # Fall back to getting the root PARTUUID from the original config file
        uuid="${rootfs_uuid}"
        if [ -z "$uuid" ] && [ -f "$cfg_dest" ]; then
            uuid="$(sed -n 's/.*root=PARTUUID=\([^ ]*\).*/\1/p' "$cfg_dest")"
        fi

        if [ -n "$uuid" ]; then
            sed "s#@@ROOTFS_UUID@@#${uuid}#g" "$cfg_src" > "$cfg_dest"
            chmod 0644 "$cfg_dest"
        else
            echo "${PN}-efi: PARTUUID unavailable; keeping existing xen.cfg."
        fi
    else
        echo "${PN}-efi: xen.cfg not found in ${boot_dir}; skipping ESP copy."
    fi

    # xen.efi ships from the package payload in /usr/lib64/efi/
    efi_src="/usr/lib64/efi/xen.efi"
    if [ -f "$efi_src" ]; then
        install -m 0644 "$efi_src" "${efi_dir}/xen.efi"
    else
        echo "${PN}-efi: xen.efi not found at ${efi_src}; skipping ESP copy."
    fi
}
