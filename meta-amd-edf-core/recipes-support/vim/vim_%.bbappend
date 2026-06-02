# AMD EDF - Enhanced vim configuration

FILESEXTRAPATHS:prepend:amd-edf := "${THISDIR}/files:"

SRC_URI:append:amd-edf = " file://vimrc.edf"

do_install:append:amd-edf () {
    # Append EDF configuration to the default vimrc
    cat ${WORKDIR}/vimrc.edf >> ${D}/${datadir}/${BPN}/vimrc
}
