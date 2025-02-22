SUMMARY = "U-boot script for basecamp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit deploy

DEPENDS = "u-boot-mkimage-native"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE ?= "^$"
COMPATIBLE_MACHINE:zynqmp = "zynqmp"
COMPATIBLE_MACHINE:versal = "versal"
COMPATIBLE_MACHINE:versal2 = "versal2"

SRC_URI = " \
    file://basecamp-linux-boot.cmd \
    file://basecamp-linux-ufs-boot.cmd \
    "

include basecamp-xen-boot-env.inc

DEVICETREE_LOAD_ADDRESS_DEFAULT ??= ""
DEVICETREE_LOAD_ADDRESS_DEFAULT:zynqmp ??= "0x100000"
DEVICETREE_LOAD_ADDRESS_DEFAULT:versal ??= "0x1000"
DEVICETREE_LOAD_ADDRESS_DEFAULT:versal2 ??= "0x1000"
DEVICETREE_LOAD_ADDRESS ??= "${DEVICETREE_LOAD_ADDRESS_DEFAULT}"

KERNEL_LOAD_ADDRESS_DEFAULT ??= ""
KERNEL_LOAD_ADDRESS_DEFAULT:zynqmp ??= "0x200000"
KERNEL_LOAD_ADDRESS_DEFAULT:versal ??= "0x200000"
KERNEL_LOAD_ADDRESS_DEFAULT:versal2 ??= "0x200000"
KERNEL_LOAD_ADDRESS ??= "${KERNEL_LOAD_ADDRESS_DEFAULT}"

# UFS Storage config variable
# Default SCSI device set to 0.
SCSI_BOOTDEV ?= "0"
# Default to boot partition number where boot.scr, Image files are stored in UFS device.
SCSI_BOOTPARTNUM ?= "2"
# Default to booting with the linux rootfs device being partition 3 for UFS device.
SCSI_ROOTPARTNUM ?= "3"
# Default SCSI device node name set to a
SCSI_NODE_NAME ?= "a"
# UFS ROOT Device.
KERNEL_ROOT_SCSI ?= "root=/dev/\sd${SCSI_NODE_NAME}${SCSI_ROOTPARTNUM} rw rootwait"

do_compile() {
    # For basecamp-linux-boot.cmd
    sed -e 's/@@KERNEL_LOAD_ADDRESS@@/${KERNEL_LOAD_ADDRESS}/' \
        -e 's/@@DEVICETREE_LOAD_ADDRESS@@/${DEVICETREE_LOAD_ADDRESS}/' \
        "${WORKDIR}/basecamp-linux-boot.cmd" > "${WORKDIR}/linux-boot.cmd"

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

    # For basecamp-linux-ufs-boot.cmd
    sed -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@KERNEL_LOAD_ADDRESS@@/${KERNEL_LOAD_ADDRESS}/' \
        -e 's/@@DEVICETREE_LOAD_ADDRESS@@/${DEVICETREE_LOAD_ADDRESS}/' \
        -e 's/@@SCSI_BOOTDEV@@/${SCSI_BOOTDEV}/' \
        -e 's/@@SCSI_NODE_NAME@@/${SCSI_NODE_NAME}/' \
        -e 's/@@SCSI_BOOTPARTNUM@@/${SCSI_BOOTPARTNUM}/' \
        -e 's/@@SCSI_ROOTPARTNUM@@/${SCSI_ROOTPARTNUM}/' \
        -e 's:@@KERNEL_ROOT_SCSI@@:${KERNEL_ROOT_SCSI}:' \
        "${WORKDIR}/basecamp-linux-ufs-boot.cmd" > "${WORKDIR}/boot-ufs.cmd"

	mkimage -A arm -T script -C none -n "Linux Boot script" -d "${WORKDIR}/linux-boot.cmd" boot.scr
	mkimage -A arm -T script -C none -n "Xen Boot script" -d "${WORKDIR}/xen-boot.cmd" xen_boot.scr
	mkimage -A arm -T script -C none -n "Linux UFS Boot script" -d "${WORKDIR}/boot-ufs.cmd" boot_ufs.scr
}

do_install() {
	install -d ${D}/boot
	install -m 0644 boot.scr ${D}/boot
	install -m 0644 xen_boot.scr ${D}/boot
	install -m 0644 boot_ufs.scr ${D}/boot
}

FILES:${PN} = "/boot/*"

do_deploy() {
	install -d ${DEPLOYDIR}
	install -m 0644 boot.scr ${DEPLOYDIR}
	install -m 0644 xen_boot.scr ${DEPLOYDIR}
	install -m 0644 boot_ufs.scr ${DEPLOYDIR}
}
addtask do_deploy after do_compile before do_build
