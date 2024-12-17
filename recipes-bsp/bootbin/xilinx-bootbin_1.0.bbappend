# Include FreeRTOS MC app for specific machine only.
include ${@'versal-vek280-sdt-seg-ospi-freertos.inc' if d.getVar('MACHINE') == 'versal-vek280-sdt-seg-ospi' else ''}

BOOTBIN_DEPENDS ?= ""
BOOTBIN_DEPENDS:basecamp ?= "bootbin-version-header:do_deploy bootbin-version-string:do_deploy"
do_configure[depends] += "${BOOTBIN_DEPENDS}"

BIF_OPTIONAL_DATA:append:basecamp = "${DEPLOY_DIR_IMAGE}/bootbin-version-string-${MACHINE}.txt, id=0x21;"
BIF_OPTIONAL_DATA:append:basecamp = "${DEPLOY_DIR_IMAGE}/bootbin-version-header-${MACHINE}.txt, id=0x22;"
