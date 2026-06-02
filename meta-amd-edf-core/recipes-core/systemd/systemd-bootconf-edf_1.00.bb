SUMMARY = "systemd-boot configuration files for booting with UEFI"
DESCRIPTION = "systemd-boot configuration files to be deployed to the ESP for use with EDF distro"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PROVIDES += "virtual-systemd-bootconf"
RPROVIDES:${PN} += "virtual-systemd-bootconf"

inherit deploy

# Use aarch64 override for machine compatibility
COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:aarch64 = "${MACHINE}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

INHIBIT_DEFAULT_DEPS = "1"

do_patch[noexec] = "1"
do_configure[noexec] = "1"

SRC_URI = " \
    file://loader.conf \
    file://edf-linux.conf \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'file://edf-xen.conf', '', d)} \
    "

S = "${WORKDIR}"

KERNEL_CMDLINE_EXTRA ?= "earlycon uio_pdrv_genirq.of_id=generic-uio"

do_compile() {
    # Substitute the kernel cmdline extra placeholder
    sed 's#@@KERNEL_CMDLINE_EXTRA@@#${KERNEL_CMDLINE_EXTRA}#g' ${S}/edf-linux.conf > ${WORKDIR}/edf-linux.conf.out
}

do_install() {
    install -d ${D}/boot/loader/entries
    install -m 0644 ${S}/loader.conf ${D}/boot/loader/
    install -m 0644 ${WORKDIR}/edf-linux.conf.out ${D}/boot/loader/entries/edf-linux.conf
    if [ -e "${S}/edf-xen.conf" ]; then
        install -m 0644 ${S}/edf-xen.conf ${D}/boot/loader/entries/
    fi
}

FILES:${PN} = "/boot/loader/loader.conf /boot/loader/entries/*.conf"

do_deploy() {
    install -d ${DEPLOYDIR}/loader
    install -m 0644 ${S}/loader.conf ${DEPLOYDIR}/loader
    install -m 0644 ${WORKDIR}/edf-linux.conf.out ${DEPLOYDIR}/loader/edf-linux.conf
    if [ -e "${S}/edf-xen.conf" ]; then
        install -m 0644 ${S}/edf-xen.conf ${DEPLOYDIR}/loader
    fi
}

pkg_postinst:${PN} () {
    # Skip during rootfs creation so the copy runs only on the live target.
    if [ -n "$D" ]; then
        echo "systemd-bootconf-edf: skipping postinst during image build."
        exit 0
    fi

    loader_dir="/boot/loader"
    efi_dir="/efi"

    if [ ! -d "$loader_dir" ]; then
        echo "systemd-bootconf-edf: /boot/loader missing; skipping ESP sync."
        exit 0
    fi

    if [ ! -d "$efi_dir" ]; then
        echo "systemd-bootconf-edf: /efi not mounted; skipping ESP sync."
        exit 0
    fi

    if ! grep -qs " ${efi_dir} " /proc/mounts; then
        echo "systemd-bootconf-edf: /efi not mounted; skipping ESP sync."
        exit 0
    fi

    dest_loader="${efi_dir}/loader"

    rootfs_uuid=""
    if [ -r /proc/cmdline ]; then
        rootfs_uuid="$(sed -n 's/.*root=PARTUUID=\([^ ]*\).*/\1/p' /proc/cmdline)"
    fi

    install -d "${dest_loader}/entries"

    if [ -f "${loader_dir}/loader.conf" ]; then
        install -m 0644 "${loader_dir}/loader.conf" "${dest_loader}/loader.conf"
    fi

    for entry in "${loader_dir}"/entries/*.conf; do
        [ -f "$entry" ] || continue
        dest_entry="${dest_loader}/entries/$(basename "$entry")"

        uuid="${rootfs_uuid}"
        if [ -z "$uuid" ] && [ -f "$dest_entry" ]; then
            uuid="$(sed -n 's/.*root=PARTUUID=\([^ ]*\).*/\1/p' "$dest_entry")"
        fi

        if [ -n "$uuid" ]; then
            sed "s#@@ROOTFS_UUID@@#${uuid}#g" "$entry" > "$dest_entry"
            chmod 0644 "$dest_entry"
        else
            echo "systemd-bootconf-edf: PARTUUID unavailable; keeping existing $(basename "$dest_entry")."
        fi
    done
}

addtask do_deploy after do_compile
