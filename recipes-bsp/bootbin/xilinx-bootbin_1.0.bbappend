BOOTBIN_DEPENDS ?= ""
BOOTBIN_DEPENDS:append:basecamp = " bootbin-version-header:do_deploy"
BOOTBIN_DEPENDS:append:versal = " bootbin-version-string:do_deploy"
BOOTBIN_DEPENDS:append:versal2 = " bootbin-version-string:do_deploy"
do_configure[depends] += "${BOOTBIN_DEPENDS}"

BIF_OPTIONAL_DATA:append:basecamp = "${DEPLOY_DIR_IMAGE}/bootbin-version-header-${MACHINE}.bin, id=0x22;"
BIF_OPTIONAL_DATA:append:versal = "${DEPLOY_DIR_IMAGE}/bootbin-version-string-${MACHINE}.txt, id=0x21;"
BIF_OPTIONAL_DATA:append:versal2 = "${DEPLOY_DIR_IMAGE}/bootbin-version-string-${MACHINE}.txt, id=0x21;"
