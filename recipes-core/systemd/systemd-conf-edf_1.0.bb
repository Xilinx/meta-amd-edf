SUMMARY = "EDF default systemd configuration files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI = "file://25-rootfs.conf"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/repart.d/
    install -m 0644 ${S}/25-rootfs.conf ${D}${sysconfdir}/repart.d/25-rootfs.conf
}

RDEPENDS:${PN} += "systemd"
