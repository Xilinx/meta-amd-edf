SUMMARY = "VEK280 Capsule files"
DESCRIPTION = "VEK280 Capsule files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "https://petalinux.xilinx.com/sswreleases/rel-v2025.2/edf_files/2025.2/09240355/external/uefi-capsule/versal-vek280-sdt-seg_uefi-capsule_09240355.tar.gz"
SRC_URI[sha256sum] = "06f7271e5824ee8bfc7874fb15d2ed2c938f3ef61a0584b115f270433f3671ac"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:amd-cortexa78-mali-common = "${MACHINE}"

FW_DIR = "versal-vek280-sdt-seg_uefi-capsule"

S = "${WORKDIR}"

do_install() {
        install -d ${D}/${nonarch_base_libdir}/firmware/xilinx/${PN}
        install -Dm 0644 ${S}/${FW_DIR}/uefi-capsule*.bin ${D}/${nonarch_base_libdir}/firmware/xilinx/${PN}/
        install -Dm 0644 ${S}/${FW_DIR}/uefi-capsule*.cab ${D}/${nonarch_base_libdir}/firmware/xilinx/${PN}/
}

FILES:${PN} += "${nonarch_base_libdir}/firmware/xilinx/${PN}"
