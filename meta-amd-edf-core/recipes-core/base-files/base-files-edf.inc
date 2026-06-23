FILESEXTRAPATHS:prepend  := "${THISDIR}/files:"

SRC_URI += "file://amd-edf.sh"
SRC_URI += "file://locale.conf"

dirs755:append = " ${sysconfdir}/profile.d"

do_install:append () {
        install -m 0755 ${UNPACKDIR}/amd-edf.sh ${D}${sysconfdir}/profile.d/amd-edf.sh
        # Default system locale (C.UTF-8) so UTF-8 terminal apps render correctly
        install -m 0644 ${UNPACKDIR}/locale.conf ${D}${sysconfdir}/locale.conf
}

# Mark locale.conf as a config file so a base-files upgrade does not clobber a
# user-edited locale (e.g. set via localectl).
CONFFILES:${PN} += "${sysconfdir}/locale.conf"
