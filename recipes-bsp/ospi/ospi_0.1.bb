DESCRIPTION = "OSPI image for Basecamp - VEK280"
SUMMARY = "Temporary OSPI image layout"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "virtual/boot-bin virtual/bootloader"

inherit deploy image-artifact-names

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:xlnx-versal-vek280-revb-ospi = "${MACHINE}"

BOOTBIN_OFFSET ?= "0x0"
UBOOT_ENV_OFFSET ?= "0x7_F40_000"
OSPI_SIZE ?= "0x10_000_000"

# By default IMAGE_NAME_SUFFIX=".rootfs" set in image-artifact-name.bbclass, Due
# to this IMAGE_NAME will have .rootfs(ospi-xlnx-versal-vek280-revb-ospi.rootfs-20240605023046)
# suffix. OSPI bin file is not part of rootfs, hence set this to null to remove
# .rootfs extension.
IMAGE_NAME_SUFFIX=""

do_compile[depends] += "virtual/boot-bin:do_deploy virtual/bootloader:do_deploy"

python do_compile() {

    import io

    bootbin_offset = int(d.getVar("BOOTBIN_OFFSET") or '0', 0)
    uboot_env_offset = int(d.getVar("UBOOT_ENV_OFFSET") or '0', 0)
    ospi_size = int(d.getVar("OSPI_SIZE") or '0', 0)

    ospi_data = io.BytesIO()
    ospi_data.write(b'\x00' * ospi_size)

    # BOOT.BIN at 0x00000000
    try:
        with open(d.getVar("DEPLOY_DIR_IMAGE")+"/boot.bin", "rb") as bbin:
            bootbin = bbin.read(-1)
    except OSError as err:
        bb.fatal("Unable to open boot.bin file: " + str(err))

    ospi_data.seek(bootbin_offset)
    ospi_data.write(bootbin)

    # UBoot env
    try:
        with open(d.getVar("DEPLOY_DIR_IMAGE")+"/u-boot-xlnx-initial-env.bin", "rb") as env:
            uboot_env = env.read(-1)
    except OSError as err:
        bb.fatal("Unable to open boot.bin file: " + str(err))

    ospi_data.seek(uboot_env_offset)
    ospi_data.write(uboot_env)

    # Write image
    with open(d.getVar("B") + "/" + d.getVar("IMAGE_NAME") + ".bin", "wb") as b:
        b.write(ospi_data.getbuffer())

}

do_deploy () {
    install -Dm 644 ${B}/${IMAGE_NAME}.bin ${DEPLOYDIR}/${IMAGE_NAME}.bin
    ln -s ${IMAGE_NAME}.bin ${DEPLOYDIR}/${IMAGE_LINK_NAME}.bin
}

addtask deploy after do_compile
