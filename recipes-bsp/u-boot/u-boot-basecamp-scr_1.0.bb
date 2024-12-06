SUMMARY = "U-boot script for basecamp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit deploy

DEPENDS = "u-boot-mkimage-native"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE ?= "^$"
COMPATIBLE_MACHINE:versal = "versal"
COMPATIBLE_MACHINE:versal2 = "versal2"

SRC_URI = " \
	file://basecamp-linux-boot.cmd \
	"

do_compile() {
	mkimage -A arm -T script -C none -n "Boot script" -d "${WORKDIR}/basecamp-linux-boot.cmd" boot.scr
}

do_install() {
	install -d ${D}/boot
	install -m 0644 boot.scr ${D}/boot
}
FILES:${PN} = "/boot/*"

do_deploy() {
	install -d ${DEPLOYDIR}
	install -m 0644 boot.scr ${DEPLOYDIR}
}
addtask do_deploy after do_compile before do_build
