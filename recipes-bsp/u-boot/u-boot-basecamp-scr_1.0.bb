SUMMARY = "U-boot script for basecamp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit deploy

DEPENDS = "u-boot-mkimage-native"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE ?= "^$"
COMPATIBLE_MACHINE:zynqmp = "zynqmp"
COMPATIBLE_MACHINE:versal = "versal"
COMPATIBLE_MACHINE:versal-2ve-2vm = "versal-2ve-2vm"

SRC_URI = " \
    file://basecamp-linux-mmc-boot.cmd \
    "

SRC_URI:append:versal-2ve-2vm = " \
    file://basecamp-linux-ufs-boot.cmd \
    "

include basecamp-xen-boot-env.inc

do_compile() {

	mkimage -A arm -T script -C none -n "Linux Boot script" -d "${WORKDIR}/basecamp-linux-mmc-boot.cmd" boot.scr

    # For basecamp-xen-boot.cmd
    sed -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@XEN_IMAGETYPE@@/${XEN_IMAGETYPE}/' \
        -e 's/@@RAMDISK_IMAGETYPE@@/${RAMDISK_IMAGETYPE}/' \
        -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        -e 's/@@DOM0_MEM@@/${DOM0_MEM}/' \
        -e 's/@@DOM0_MAX_VCPUS@@/${DOM0_MAX_VCPUS}/' \
        -e 's/@@XEN_LOAD_ADDRESS@@/${XEN_LOAD_ADDRESS}/' \
        -e 's/@@RAMDISK_LOAD_ADDRESS@@/${RAMDISK_LOAD_ADDRESS}/' \
        -e 's:@@XEN_KERNEL_LOAD_ADDRESS@@:${XEN_KERNEL_LOAD_ADDRESS}:' \
        -e 's:@@XEN_DEVICETREE_LOAD_ADDRESS@@:${XEN_DEVICETREE_LOAD_ADDRESS}:' \
        -e 's:@@XEN_RAMDISK_LOAD_ADDRESS@@:${XEN_RAMDISK_LOAD_ADDRESS}:' \
        -e 's:@@XEN_CMDLINE_APPEND@@:${XEN_CMDLINE_APPEND}:' \
        -e 's:@@XEN_SERIAL_CONSOLES@@:${XEN_SERIAL_CONSOLES}:' \
        -e 's/@@BOOTPARTNUM@@/${BOOTPARTNUM}/' \
        -e 's/@@ROOTPARTNUM@@/${ROOTPARTNUM}/' \
        -e 's:@@KERNEL_ROOT_SD@@:${KERNEL_ROOT_SD}:' \
        -e 's:@@KERNEL_ROOT_RAMDISK@@:${KERNEL_ROOT_RAMDISK}:' \
        -e 's/@@SDBOOTDEV@@/${SDBOOTDEV}/' \
        "${WORKDIR}/basecamp-xen-boot.cmd" > "${WORKDIR}/xen-boot.cmd"

	mkimage -A arm -T script -C none -n "Xen Boot script" -d "${WORKDIR}/xen-boot.cmd" xen_boot.scr
}

do_compile:append:versal-2ve-2vm() {
    mkimage -A arm -T script -C none -n "Linux UFS Boot script" -d "${WORKDIR}/basecamp-linux-ufs-boot.cmd" boot_ufs.scr
}

do_install() {
	install -d ${D}/boot
	install -m 0644 boot.scr ${D}/boot
	install -m 0644 xen_boot.scr ${D}/boot
}

do_install:append:versal-2ve-2vm() {
	install -m 0644 boot_ufs.scr ${D}/boot
}

FILES:${PN} = "/boot/*"

do_deploy() {
	install -d ${DEPLOYDIR}
	install -m 0644 boot.scr ${DEPLOYDIR}
	install -m 0644 xen_boot.scr ${DEPLOYDIR}
}

do_deploy:append:versal-2ve-2vm() {
    install -m 0644 boot_ufs.scr ${DEPLOYDIR}
}

addtask do_deploy after do_compile before do_build
