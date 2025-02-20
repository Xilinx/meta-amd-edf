IMGSEL_VERSION ?= "1.0"
IMGSEL_VERSION_STRING ?= "basecamp-${MACHINE}-imgsel-v${IMGSEL_VERSION}"
IMGSEL_VERSION_FILE ?= "${WORKDIR}/${PN}-version.txt"

do_configure:append () {
	if [ "${SOC_FAMILY}" != "zynqmp" ]; then
		echo "${IMGSEL_VERSION_STRING}" > ${IMGSEL_VERSION_FILE}
	fi
}

IMGSEL_BIF_OPTIONAL_DATA:versal ?= "${IMGSEL_VERSION_FILE},id=0x21;"
