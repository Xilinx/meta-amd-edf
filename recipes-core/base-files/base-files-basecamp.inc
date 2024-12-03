FILESEXTRAPATHS:prepend  := "${THISDIR}/files:"

SRC_URI += "file://basecamp.sh"

dirs755:append = " ${sysconfdir}/profile.d"

do_install:append () {
        install -m 0755 ${WORKDIR}/basecamp.sh ${D}${sysconfdir}/profile.d/basecamp.sh
}

