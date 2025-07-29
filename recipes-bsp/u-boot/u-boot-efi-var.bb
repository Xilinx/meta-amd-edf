SUMMARY = "Generate u-boot EFI vars file"
DESCRIPTION = "ubootefi.var is stored in the ESP and persists the UEFI environment including boot settings and order"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

FILESEXTRAPATHS:prepend:amd-cortexa72-common := "${THISDIR}/amd-cortexa72-common:"
FILESEXTRAPATHS:prepend:amd-cortexa78-mali-common := "${THISDIR}/amd-cortexa78-mali-common:"

DEPENDS += "u-boot-efivars-native xxd-native python3-pyopenssl-native"

SRC_URI:append:amd-cortexa72-common = "file://Boot0032.txt"
SRC_URI:append:amd-cortexa78-mali-common = "file://Boot0032.txt file://Boot0033.txt"

inherit deploy python3native

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:amd-cortexa72-common = "${MACHINE}"
COMPATIBLE_MACHINE:amd-cortexa78-mali-common = "${MACHINE}"

INHIBIT_DEFAULT_DEPS = "1"

do_compile() {
    export CRYPTOGRAPHY_OPENSSL_NO_LEGACY=1
}

do_compile:append:amd-cortexa72-common() {
    xxd -r -p ${WORKDIR}/Boot0032.txt Boot0032.bin

    efivar.py set -i ubootefi.var -n Boot0032 -d Boot0032.bin -t file
    efivar.py set -i ubootefi.var -n BootOrder -d 50 -t u16
}

do_compile:append:amd-cortexa78-mali-common() {
    xxd -r -p ${WORKDIR}/Boot0032.txt Boot0032.bin
    xxd -r -p ${WORKDIR}/Boot0033.txt Boot0033.bin

    echo "32 00 33 00" > ${WORKDIR}/BootOrder.txt
    xxd -r -p ${WORKDIR}/BootOrder.txt BootOrder.bin

    efivar.py set -i ubootefi.var -n Boot0032 -d Boot0032.bin -t file
    efivar.py set -i ubootefi.var -n Boot0033 -d Boot0033.bin -t file
    efivar.py set -i ubootefi.var -n BootOrder -d BootOrder.bin -t file
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -Dm 0644 ubootefi.var ${DEPLOYDIR}/ubootefi-${MACHINE}.var
    ln -s ubootefi-${MACHINE}.var ${DEPLOYDIR}/ubootefi.var
}

addtask do_deploy after do_compile before do_build
