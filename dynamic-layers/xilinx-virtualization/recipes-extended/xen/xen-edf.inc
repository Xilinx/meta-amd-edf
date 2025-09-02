FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://xen-efi-config"

do_install:append() {
    install -m 0644 ${WORKDIR}/xen-efi-config ${D}/boot/xen.cfg
}

FILES:${PN}-efi += "/boot/xen.cfg"

do_deploy:append() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${WORKDIR}/xen-efi-config ${DEPLOYDIR}/xen.cfg
}
