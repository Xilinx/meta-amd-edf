IMGSEL_VERSION ?= "${@d.getVar('PV').split('+')[0]}"
IMGSEL_DESCRIPTION ?= "Image Selector (${MACHINE})"
IMGSEL_TOOLS_VERSION ?= "${@(d.getVar('XILINX_RELEASE_VERSION') or '').replace('v', '')}"

# Metadata embedded into the image-selector BIN as BIF optional data
#   id=0x21 description
#   id=0x22 component version
#   id=0x23 tool version
BIF_OPTIONAL_DATA_VAL[0x21] = "${IMGSEL_DESCRIPTION}"
BIF_OPTIONAL_DATA_VAL[0x22] = "${IMGSEL_VERSION}"
BIF_OPTIONAL_DATA_VAL[0x23] = "${IMGSEL_TOOLS_VERSION}"
