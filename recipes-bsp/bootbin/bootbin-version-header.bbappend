COMPATIBLE_MACHINE:versal = "${MACHINE}"

BOOTBIN_VER_MAX_LEN:versal ?= "50"


BASECAMP_BOOTBIN_VER ?= "1"
BASECAMP_BOOTBIN_VER_FILE ?= "bootbin-version-header.bin"

python do_configure:append:basecamp() {
    basecamp_version = d.getVar('BASECAMP_BOOTBIN_VER')
    with open(d.expand("${B}/${BASECAMP_BOOTBIN_VER_FILE}"), "w") as f:
       f.write(int(basecamp_version).to_bytes(4, 'little').decode('utf-8'))
}

do_deploy:append:basecamp() {
     install -m 0644 ${B}/${BASECAMP_BOOTBIN_VER_FILE} ${DEPLOYDIR}/${IMAGE_NAME}.bin
     ln -s ${IMAGE_NAME}.bin ${DEPLOYDIR}/${IMAGE_LINK_NAME}.bin
}
