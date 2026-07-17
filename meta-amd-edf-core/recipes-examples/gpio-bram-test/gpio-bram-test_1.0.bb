SUMMARY = "GPIO BRAM example PL payload test for AMD EDF platforms"
DESCRIPTION = "Python test utility that validates the GPIO-BRAM example \
PL design by performing BRAM MMIO validation and GPIO line testing \
using python-periphery APIs."
SECTION = "examples"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://gpio_bram_test.py;beginline=1;endline=3;md5=1490da732217e397edf28cd8d6079e04"

SRC_URI = "file://gpio_bram_test.py"

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/gpio_bram_test.py ${D}${bindir}/gpio_bram_test.py
}

RDEPENDS:${PN} += " \
    python3-core \
    python3-periphery \
    "
