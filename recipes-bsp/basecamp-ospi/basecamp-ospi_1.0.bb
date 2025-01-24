SUMMARY = "OSPI image for Basecamp"
DESCRIPTION = "OSPI image for Basecamp with image selector, image recovery, capsule metadata and boot.bin"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "virtual/boot-bin capsule-mdata virtual/imgsel virtual/imgrcry"

inherit deploy image-artifact-names
IMAGE_NAME_SUFFIX = ""

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:versal-vek280-sdt-seg-ospi = "${MACHINE}"

IMAGE_SELECTOR_OFFSET ?= "0x0"
IMAGE_SELECTOR_BACKUP_OFFSET ?= "0x6_0000"
IMAGE_SELECTOR_SCRATCHPAD_OFFSET ?= "0xC_0000"
IMAGE_RECOVERY_OFFSET ?= "0xE_0000"
IMAGE_RECOVERY_META_OFFSET ?= "0x14E_0000"
CAPSULE_METADATA_OFFSET ?= "0x150_0000"
CAPSULE_METADATA_BACKUP_OFFSET ?= "0x152_0000"
UBOOT_ENV_OFFSET ?= "0x154_0000"
UBOOT_ENV_BACKUP_OFFSET ?= "0x156_0000"
IMAGE_A_OFFSET ?= "0x158_0000"
#UBOOT_ENV_A_OFFSET ?= "0x878_0000"
#UBOOT_ENV_A_BACKUP_OFFSET ?= "0x87A_0000"
IMAGE_B_OFFSET ?= "0x87C_0000"
#UBOOT_ENV_B_OFFSET ?= "0x796_0000"
#UBOOT_ENV_B_BACKUP_OFFSET ?= "0x79E_0000"
USER_SCRATCHPAD_OFFSET ?= "0xF9E_000"
OSPI_SIZE ?= "0x1000_0000"

# The file comes from the imgrcvry deployment recipe with a specific name
IMGRCRY_IMAGE_NAME ??= "image-recovery-${MACHINE}"
IMGRCVRY_BIN_FILE = "${DEPLOY_DIR_IMAGE}/${IMGRCRY_IMAGE_NAME}.bin"

do_compile[depends] += " \
    virtual/boot-bin:do_deploy \
    capsule-mdata:do_deploy \
    virtual/imgsel:do_deploy \
    imgrcvry-deploy:do_deploy \
    "

python do_compile() {

    import io

    image_selector_offset = int(d.getVar("IMAGE_SELECTOR_OFFSET") or '0', 0)
    image_selector_backup_offset = int(d.getVar("IMAGE_SELECTOR_BACKUP_OFFSET") or '0', 0)
    image_recovery_offset = int(d.getVar("IMAGE_RECOVERY_OFFSET") or '0', 0)
    image_recovery_meta_offset = int(d.getVar("IMAGE_RECOVERY_META_OFFSET") or '0', 0)
    capsule_metadata_offset = int(d.getVar("CAPSULE_METADATA_OFFSET") or '0', 0)
    capsule_metadata_backup_offset = int(d.getVar("CAPSULE_METADATA_OFFSET") or '0', 0)
    uboot_env_offset = int(d.getVar("UBOOT_ENV_A_OFFSET") or '0', 0)
    uboot_env_backup_offset = int(d.getVar("UBOOT_ENV_B_OFFSET") or '0', 0)
    image_a_offset = int(d.getVar("IMAGE_A_OFFSET") or '0', 0)
    image_b_offset = int(d.getVar("IMAGE_B_OFFSET") or '0', 0)
    ospi_size = int(d.getVar("OSPI_SIZE") or '0', 0)

    ospi_data = io.BytesIO()
    ospi_data.write(b'\xFF' * ospi_size)

    # Image selector

    try:
        with open(d.getVar("DEPLOY_DIR_IMAGE")+"/image-selector-"+d.getVar("MACHINE")+".bin", "rb") as f:
            imgsel_data = f.read(-1)
    except OSError as err:
        bb.fatal("Unable to open image selector file: " + str(err))

    ospi_data.seek(image_selector_offset)
    ospi_data.write(imgsel_data)
    ospi_data.seek(image_selector_backup_offset)
    ospi_data.write(imgsel_data)

    # Image recovery

    try:
        with open(d.getVar("IMGRCVRY_BIN_FILE"), "rb") as f:
            imgrcvry_data = f.read(-1)
    except OSError as err:
        bb.fatal("Unable to open image recovery file: " + str(err))

    ospi_data.seek(image_recovery_offset)
    ospi_data.write(imgrcvry_data)

    # System ready IR - Capsule Metadata

    try:
        with open(d.getVar("DEPLOY_DIR_IMAGE")+"/capsule-mdata-"+d.getVar("MACHINE")+".bin", "rb") as f:
            capsule_mdata = f.read(-1)
    except OSError as err:
        bb.fatal("Unable to open capsule metadata file: " + str(err))

    ospi_data.seek(capsule_metadata_offset)
    ospi_data.write(capsule_mdata)
    ospi_data.seek(capsule_metadata_backup_offset)
    ospi_data.write(capsule_mdata)

    # UBoot env

    try:
        with open(d.getVar("DEPLOY_DIR_IMAGE")+"/u-boot-xlnx-initial-env.bin", "rb") as f:
            uboot_env = f.read(-1)
    except OSError as err:
        bb.fatal("Unable to open UBoot env file: " + str(err))

    ospi_data.seek(uboot_env_offset)
    ospi_data.write(uboot_env)
    ospi_data.seek(uboot_env_backup_offset)
    ospi_data.write(uboot_env)

    # Image A/B

    try:
        with open(d.getVar("DEPLOY_DIR_IMAGE")+"/boot.bin", "rb") as f:
            bootbin = f.read(-1)
    except OSError as err:
        bb.fatal("Unable to open boot.bin file: " + str(err))

    #FIXME add a size check here - 116736KB max on 2GB

    ospi_data.seek(image_a_offset)
    ospi_data.write(bootbin)
    ospi_data.seek(image_b_offset)
    ospi_data.write(bootbin)

    # Write image

    with open(d.getVar("B") + "/" + d.getVar("IMAGE_NAME") + ".bin", "wb") as b:
        b.write(ospi_data.getbuffer())

}

do_deploy () {
    install -Dm 644 ${B}/${IMAGE_NAME}.bin ${DEPLOYDIR}/${IMAGE_NAME}.bin
    ln -s ${IMAGE_NAME}.bin ${DEPLOYDIR}/${IMAGE_LINK_NAME}.bin
}

addtask deploy after do_compile
