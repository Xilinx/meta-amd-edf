BOOTBIN_DEPENDS ?= ""
BOOTBIN_DEPENDS:append:amd-edf = " bootbin-version-header:do_deploy"
BOOTBIN_DEPENDS:append:amd-edf:versal = " bootbin-version-string:do_deploy"
BOOTBIN_DEPENDS:append:amd-edf:versal-net = " bootbin-version-string:do_deploy"
BOOTBIN_DEPENDS:append:amd-edf:versal-2ve-2vm = " bootbin-version-string:do_deploy base-pdi-unique-id:do_deploy"
do_configure[depends] += "${BOOTBIN_DEPENDS}"

# User Defined Field Boot Header for version tracking on zynq/zynqmp
# For Kria machines, meta-kria sets BIF_UDFBH_ATTR:kria which takes precedence
BIF_UDFBH_ATTR:amd-edf:zynq = "bootbin-version-header"
BIF_UDFBH_ATTR:amd-edf:zynqmp = "bootbin-version-header"
BIF_PARTITION_ATTR[bootbin-version-header] = "udf_bh"
# Use variable indirection so meta-kria can override the path for Kria machines
BIF_VERSION_HEADER_IMAGE ?= "${DEPLOY_DIR_IMAGE}/bootbin-version-header-${MACHINE}.txt"
BIF_PARTITION_IMAGE[bootbin-version-header] = "${BIF_VERSION_HEADER_IMAGE}"

BIF_OPTIONAL_DATA:append:amd-edf = "${@'${DEPLOY_DIR_IMAGE}/bootbin-version-header-${MACHINE}.txt, id=0x22;' if d.getVar('SOC_FAMILY') not in [ 'zynq', 'zynqmp' ] else ''}"
BIF_OPTIONAL_DATA:append:amd-edf:versal = "${DEPLOY_DIR_IMAGE}/bootbin-version-string-${MACHINE}.txt, id=0x21;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-net = "${DEPLOY_DIR_IMAGE}/bootbin-version-string-${MACHINE}.txt, id=0x21;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-2ve-2vm = "${DEPLOY_DIR_IMAGE}/bootbin-version-string-${MACHINE}.txt, id=0x21;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-2ve-2vm = "${DEPLOY_DIR_IMAGE}/base-pdi-unique-id-${MACHINE}.txt, id=0x23;"

# For EDF we want zynq to boot using u-boot, not directly to the kernel
QB_DEFAULT_KERNEL:zynq = "u-boot.elf"
