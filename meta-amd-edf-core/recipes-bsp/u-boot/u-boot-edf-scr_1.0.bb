SUMMARY = "U-boot script for AMD Embedded Development Framework"
DESCRIPTION = "Builds the U-Boot boot.scr boot script used by the AMD \
Embedded Development Framework (EDF) reference distribution to load \
the kernel, device tree and initramfs at boot."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit deploy

DEPENDS = "u-boot-mkimage-native"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE ?= "^$"
COMPATIBLE_MACHINE:zynq = "zynq"
COMPATIBLE_MACHINE:zynqmp = "zynqmp"
COMPATIBLE_MACHINE:versal = "versal"
COMPATIBLE_MACHINE:versal-net = "versal-net"
COMPATIBLE_MACHINE:versal-2ve-2vm = "versal-2ve-2vm"
COMPATIBLE_MACHINE:microblaze-v = "microblaze-v"

SRC_URI_MBV ?= ""
SRC_URI_MBV:microblaze-v = "file://edf-linux-boot.cmd.mbv64"
SRC_URI = " \
    file://edf-linux-boot.cmd \
    ${SRC_URI_MBV} \
    "

KERNEL_BOOTCMD:zynq ?= "bootm"
KERNEL_BOOTCMD:microblaze-v ?= "booti"
KERNEL_BOOTCMD:aarch64 ?= "booti"

# KERNEL_IMAGETYPE resolves to uImage on zynq (arm), Image on aarch64
KERNEL_IMAGE ?= "${KERNEL_IMAGETYPE}"

EDF_ROOT_PARTNUM ?= "3"

do_compile() {
    sed -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        -e 's/@@KERNEL_IMAGE@@/${KERNEL_IMAGE}/' \
        -e 's/@@ROOT_PARTNUM@@/${EDF_ROOT_PARTNUM}/' \
        "${WORKDIR}/edf-linux-boot.cmd" > "${WORKDIR}/boot.cmd"

    mkimage -A arm -T script -C none -n "EDF Boot script" \
        -d "${WORKDIR}/boot.cmd" boot.scr
}

do_compile:microblaze-v () {
    sed -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        "${WORKDIR}/edf-linux-boot.cmd.mbv64" > "${WORKDIR}/boot.cmd"

    mkimage -A riscv -T script -C none -n "EDF Boot script" \
        -d "${WORKDIR}/boot.cmd" boot.scr
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
