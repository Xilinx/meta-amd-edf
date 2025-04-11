FILESEXTRAPATHS:prepend:zynqmp := "${THISDIR}/zynqmp:"
FILESEXTRAPATHS:prepend:versal := "${THISDIR}/versal:"
FILESEXTRAPATHS:prepend:versal-2ve-2vm := "${THISDIR}/versal-2ve-2vm:"

SRC_URI:append:zynqmp = " file://basecamp-env.cfg file://xilinx_basecamp.h"
SRC_URI:append:versal = " file://basecamp-env.cfg file://xilinx_basecamp.h"
SRC_URI:append:versal-2ve-2vm = " file://basecamp-env.cfg file://amd_basecamp.h"

# Generate U-Boot environment binary image

DEPENDS += "u-boot-tools-xlnx-native"

do_unpack:append:zynqmp() {
    bb.build.exec_func('do_sys_config', d)
}

do_unpack:append:versal() {
    bb.build.exec_func('do_sys_config', d)
}

do_unpack:append:versal-2ve-2vm() {
    bb.build.exec_func('do_versal_2ve_2vm_sys_config', d)
}

do_sys_config() {
    cp ${WORKDIR}/xilinx_basecamp.h ${S}/include/configs/xilinx_basecamp.h
}

do_versal_2ve_2vm_sys_config() {
    cp ${WORKDIR}/amd_basecamp.h ${S}/include/configs/amd_basecamp.h
}

do_compile:append() {
    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
        uboot-mkenvimage -s 0x4200 -o ${WORKDIR}/uboot-initial-env.bin ${B}/${config}/u-boot-initial-env
    fi
}

do_deploy:append() {
    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
        install -D -m 644 ${WORKDIR}/uboot-initial-env.bin ${DEPLOYDIR}/${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR}.bin
        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR}.bin ${DEPLOYDIR}/${UBOOT_INITIAL_ENV}-${MACHINE}.bin
        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR}.bin ${DEPLOYDIR}/${UBOOT_INITIAL_ENV}.bin
    fi
}
