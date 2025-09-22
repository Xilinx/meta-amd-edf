SUMMARY = "VEK385 Capsule files"
DESCRIPTION = "VEK385 Capsule files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "https://petalinux.xilinx.com/sswreleases/rel-v2025.2/edf_files/2025.2/09220403/external/uefi-capsule/versal-2ve-2vm-vek385-sdt-seg_uefi-capsule_09220403.tar.gz"
SRC_URI[sha256sum] = "2793e70435be1c9f1f8f42765865e72bb1401d6205797580a2754e41d5b52003"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:amd-cortexa78-mali-common = "${MACHINE}"

FW_DIR = "versal-2ve-2vm-vek385-sdt-seg_uefi-capsule"

S = "${WORKDIR}"

do_install() {
        install -d ${D}/${nonarch_base_libdir}/firmware/xilinx/${PN}
        install -Dm 0644 ${S}/${FW_DIR}/uefi-capsule*.bin ${D}/${nonarch_base_libdir}/firmware/xilinx/${PN}/
        install -Dm 0644 ${S}/${FW_DIR}/uefi-capsule*.cab ${D}/${nonarch_base_libdir}/firmware/xilinx/${PN}/
}

FILES:${PN} += "${nonarch_base_libdir}/firmware/xilinx/${PN}"
