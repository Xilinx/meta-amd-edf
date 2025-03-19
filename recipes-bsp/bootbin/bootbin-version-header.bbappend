COMPATIBLE_MACHINE:zynqmp = "${MACHINE}"
COMPATIBLE_MACHINE:versal = "${MACHINE}"
COMPATIBLE_MACHINE:versal2 = "${MACHINE}"

BOOTBIN_VER_MAX_LEN:versal ?= "50"
BOOTBIN_VER_MAX_LEN:versal2 ?= "60"

BASECAMP_BOOTBIN_VER ?= "1"
BASECAMP_BOOTBIN_VER_FILE ?= "bootbin-version-header.bin"

python do_configure:prepend:basecamp() {
    version = d.getVar("BASECAMP_BOOTBIN_VER")
}

python do_configure:append:basecamp() {
    basecamp_version = d.getVar('BASECAMP_BOOTBIN_VER')
    if d.getVar('SOC_FAMILY') == 'zynqmp':
       basecamp_version = d.getVar('MACHINE') + '-v' + d.getVar('BASECAMP_BOOTBIN_VER')
       basecamp_ver_f = basecamp_version.encode("utf-8").hex()
    else:
       basecamp_ver_f = int(basecamp_version).to_bytes(4, 'little').decode('utf-8')

    with open(d.expand("${B}/${BASECAMP_BOOTBIN_VER_FILE}"), "w") as f:
       f.write(basecamp_ver_f)
}

do_deploy:append:basecamp() {
     install -m 0644 ${B}/${BASECAMP_BOOTBIN_VER_FILE} ${DEPLOYDIR}/${IMAGE_NAME}.bin
     ln -s ${IMAGE_NAME}.bin ${DEPLOYDIR}/${IMAGE_LINK_NAME}.bin
}
