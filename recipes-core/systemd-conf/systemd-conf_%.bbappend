FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://timesyncd.conf"

do_install:append() {
	install -D -m0644 ${WORKDIR}/timesyncd.conf ${D}${systemd_unitdir}/timesyncd.conf.d/00-${PN}.conf
}

FILES:${PN} += "${systemd_unitdir}/timesyncd.conf.d/"
