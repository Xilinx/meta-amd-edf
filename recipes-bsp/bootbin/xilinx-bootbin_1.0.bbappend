APP = "freertos-hello-world"
FHW_DEPENDS = ""
FHW_MCDEPENDS = "mc::${MACHINE}-cortexr5-1-freertos:${APP}:do_deploy"
# For multiconfig apps depends does not work and default for xilinx-bootbin depends got added
DEPENDS:remove = "${APP}"
do_compile[mcdepends] += "${FHW_MCDEPENDS}"
FHW_DEPLOY_DIR = "${TMPDIR}-${MACHINE}-cortexr5-1-freertos/deploy/images/${MACHINE}"
BIF_PARTITION_ATTR:append = " ${APP}"
BIF_PARTITION_ATTR[freertos-hello-world] = "destination_cpu=r52-1"
BIF_PARTITION_IMAGE[freertos-hello-world] = "${FHW_DEPLOY_DIR}/${APP}-${MACHINE}-${MACHINE}-cortexr5-1-freertos.elf"
BIF_PARTITION_ID[freertos-hello-world] = "0x1c000000"

BOOTBIN_DEPENDS ?= ""
BOOTBIN_DEPENDS:basecamp ?= "bootbin-version-header:do_deploy bootbin-version-string:do_deploy"
do_configure[depends] += "${BOOTBIN_DEPENDS}"

BIF_OPTIONAL_DATA:append:basecamp = "${DEPLOY_DIR_IMAGE}/bootbin-version-string-${MACHINE}.txt, id=0x21;"
BIF_OPTIONAL_DATA:append:basecamp = "${DEPLOY_DIR_IMAGE}/bootbin-version-header-${MACHINE}.txt, id=0x22;"
