FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://basecamp-env-offset.cfg"

# Generate U-Boot environment binary image

DEPENDS += "u-boot-tools-xlnx-native"

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
