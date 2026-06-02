SUMMARY = "EDF default systemd configuration files"
DESCRIPTION = "Drop-in systemd configuration overrides (units, \
presets, tmpfiles and sysctl snippets) that adjust the default systemd \
behaviour for AMD Embedded Development Framework (EDF) target images."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI = " \
    file://25-rootfs.conf \
    file://timesyncd.conf \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/repart.d/
    install -m 0644 ${S}/25-rootfs.conf ${D}${sysconfdir}/repart.d/25-rootfs.conf
    install -D -m 0644 ${S}/timesyncd.conf ${D}${systemd_unitdir}/timesyncd.conf.d/00-xilinx.conf
}

FILES:${PN} += "${systemd_unitdir}/timesyncd.conf.d/00-xilinx.conf"

RDEPENDS:${PN} += "systemd"
