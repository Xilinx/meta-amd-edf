SUMMARY = "Generate capsule update metadata"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "u-boot-mkfwumdata-native"

inherit deploy image-artifact-names
IMAGE_NAME_SUFFIX = ""

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:versal-vek280-sdt-seg-ospi = "${MACHINE}"
COMPATIBLE_MACHINE:versal2-vek385-sdt-seg = "${MACHINE}"

INHIBIT_DEFAULT_DEPS = "1"

LOC_GUID ?= "588aced7-2cce-ed11-81cd-d324e93ac223"
IMAGE_TYPE_GUID_0 ?= "e86660de-5602-ad4f-8238-e406e274c4cf"
IMG_0_GUID_0 ?= "48054af6-ce2c-11ed-8f66-7bc4531cfe6b"
IMG_0_GUID_1 ?= "4b819c3e-ce2c-11ed-bec8-23de4c6d2cf2"

do_configure() {
    echo -e -n $'\x04' > ${WORKDIR}/${PN}-vendor.txt
    dd if=/dev/zero of=${WORKDIR}/${PN}-vendor.txt seek=1 bs=1 count=3
}

do_compile() {
    mkfwumdata -a 0 -b 2 -i 1 -v 2 ${LOC_GUID},${IMAGE_TYPE_GUID_0},${IMG_0_GUID_0},${IMG_0_GUID_1} ${PN}.bin -V ${WORKDIR}/${PN}-vendor.txt
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -Dm 0644 ${PN}.bin ${DEPLOYDIR}/${IMAGE_NAME}.bin
    ln -sf ${IMAGE_NAME}.bin ${DEPLOYDIR}/${IMAGE_LINK_NAME}.bin
}

addtask do_deploy after do_compile
