SUMMARY = "U-boot script for AMD Embedded Development Framework"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit deploy

DEPENDS = "u-boot-mkimage-native"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE ?= "^$"
COMPATIBLE_MACHINE:zynqmp = "zynqmp"
COMPATIBLE_MACHINE:versal = "versal"
COMPATIBLE_MACHINE:versal-net = "versal-net"
COMPATIBLE_MACHINE:versal-2ve-2vm = "versal-2ve-2vm"

SRC_URI = " \
    file://edf-linux-mmc-boot.cmd \
    "

SRC_URI:append:versal-2ve-2vm = " \
    file://edf-linux-ufs-boot.cmd \
    "

# Add Xen EDF variables as addendum.
EDF_XEN_SCRIPT_SED_ADDENDUM = ""
EDF_XEN_DOM0LESS_SCRIPT_SED_ADDENDUM = ""

include edf-xen-dom0less-boot-env.inc

do_compile() {

	mkimage -A arm -T script -C none -n "Linux Boot script" -d "${WORKDIR}/edf-linux-mmc-boot.cmd" boot.scr

    # For edf-xen-boot.cmd
    sed ${EDF_XEN_SCRIPT_SED_ADDENDUM} \
        "${WORKDIR}/edf-xen-boot.cmd" > "${WORKDIR}/xen-boot.cmd"

    # For edf-xen-dom0less-boot.cmd
    sed ${EDF_XEN_DOM0LESS_SCRIPT_SED_ADDENDUM} \
        "${WORKDIR}/edf-xen-dom0less-boot.cmd" > "${WORKDIR}/xen-dom0less-boot.cmd"

	mkimage -A arm -T script -C none -n "Xen Boot script" -d "${WORKDIR}/xen-boot.cmd" xen_boot.scr
	mkimage -A arm -T script -C none -n "Xen Dom0less Boot script" -d "${WORKDIR}/xen-dom0less-boot.cmd" xen_dom0less_boot.scr
}

do_compile:append:versal-2ve-2vm() {
    mkimage -A arm -T script -C none -n "Linux UFS Boot script" -d "${WORKDIR}/edf-linux-ufs-boot.cmd" boot_ufs.scr
}

do_install() {
	install -d ${D}/boot
	install -m 0644 boot.scr ${D}/boot
	install -m 0644 xen_boot.scr ${D}/boot
	install -m 0644 xen_dom0less_boot.scr ${D}/boot
}

do_install:append:versal-2ve-2vm() {
	install -m 0644 boot_ufs.scr ${D}/boot
}

FILES:${PN} = "/boot/*"

do_deploy() {
	install -d ${DEPLOYDIR}
	install -m 0644 boot.scr ${DEPLOYDIR}
	install -m 0644 xen_boot.scr ${DEPLOYDIR}
	install -m 0644 xen_dom0less_boot.scr ${DEPLOYDIR}
}

do_deploy:append:versal-2ve-2vm() {
    install -m 0644 boot_ufs.scr ${DEPLOYDIR}
}

addtask do_deploy after do_compile before do_build
