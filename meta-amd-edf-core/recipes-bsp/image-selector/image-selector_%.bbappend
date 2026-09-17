IMGSEL_VERSION ?= "${@d.getVar('PV').split('+')[0]}"
IMGSEL_DESCRIPTION ?= "Image Selector (${MACHINE})"
IMGSEL_TOOLS_VERSION ?= "${@(d.getVar('XILINX_RELEASE_VERSION') or '').replace('v', '')}"

# Metadata embedded into the image-selector BIN as BIF optional data
#   id=0x21 description
#   id=0x22 component version
#   id=0x23 tool version
#   id=0x24 build id (absent when no id is supplied)
# Own multiconfig, not the amd-edf distro, so the id comes from here.
include conf/distro/include/amd-edf-bootbin-id.conf

BIF_OPTIONAL_DATA_VAL[0x21] = "${IMGSEL_DESCRIPTION}"
BIF_OPTIONAL_DATA_VAL[0x22] = "${IMGSEL_VERSION}"
BIF_OPTIONAL_DATA_VAL[0x23] = "${IMGSEL_TOOLS_VERSION}"
BIF_OPTIONAL_DATA_VAL[0x24] = "${BOOTBIN_VER_BUILD}"
