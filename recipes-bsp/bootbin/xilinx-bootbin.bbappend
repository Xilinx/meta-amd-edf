# Add bootbin-version-header to the manifest for zynq/zynqmp
# Note += is correct here, as we're appending to the value before the
# override is processed.  If the implementation changes in the base
# .bb, then this may need to change to match.
MANIFEST_AGGREGATE_COMPONENTS:zynq += "bootbin-version-header"
MANIFEST_AGGREGATE_COMPONENTS:zynqmp += "bootbin-version-header"

MANIFEST_AGGREGATE_DEPENDS:zynq += "bootbin-version-header"
MANIFEST_AGGREGATE_DEPENDS:zynqmp += "bootbin-version-header"

# Add bootbin-rollback-counter to the manifest for versal variants
MANIFEST_AGGREGATE_COMPONENTS:versal += "bootbin-rollback-counter"
MANIFEST_AGGREGATE_COMPONENTS:versal-net += "bootbin-rollback-counter"
MANIFEST_AGGREGATE_COMPONENTS:versal-2ve-2vm += "bootbin-rollback-counter"

MANIFEST_AGGREGATE_DEPENDS:versal += "bootbin-rollback-counter"
MANIFEST_AGGREGATE_DEPENDS:versal-net += "bootbin-rollback-counter"
MANIFEST_AGGREGATE_DEPENDS:versal-2ve-2vm += "bootbin-rollback-counter"

BOOTBIN_DEPENDS ?= ""
BOOTBIN_DEPENDS:append:amd-edf:zynq = " bootbin-version-header:do_deploy"
BOOTBIN_DEPENDS:append:amd-edf:zynqmp = " bootbin-version-header:do_deploy"
BOOTBIN_DEPENDS:append:amd-edf:versal = " bootbin-rollback-counter:do_deploy bootbin-version-string:do_deploy"
BOOTBIN_DEPENDS:append:amd-edf:versal-net = " bootbin-rollback-counter:do_deploy bootbin-version-string:do_deploy"
BOOTBIN_DEPENDS:append:amd-edf:versal-2ve-2vm = " bootbin-rollback-counter:do_deploy bootbin-version-string:do_deploy base-pdi-unique-id:do_deploy"
do_configure[depends] += "${BOOTBIN_DEPENDS}"

# User Defined Field Boot Header for version tracking on zynq/zynqmp
# For Kria machines, meta-kria sets BIF_UDFBH_ATTR:kria which takes precedence
BIF_UDFBH_ATTR:amd-edf:zynq = "bootbin-version-header"
BIF_UDFBH_ATTR:amd-edf:zynqmp = "bootbin-version-header"
BIF_PARTITION_ATTR[bootbin-version-header] = "udf_bh"
# Use variable indirection so meta-kria can override the path for Kria machines
BIF_VERSION_HEADER_IMAGE ?= "${DEPLOY_DIR_IMAGE}/bootbin-version-header-${MACHINE}.txt"
BIF_PARTITION_IMAGE[bootbin-version-header] = "${BIF_VERSION_HEADER_IMAGE}"

# Versal BIF optional data:
#   id=0x21 = human-readable version string (text)
#   id=0x22 = rollback counter (binary)
#   id=0x23 = PDI unique ID
#   id=0x24 = component manifest JSON (see shared-manifest-aggregate.bbclass)
BIF_OPTIONAL_DATA:append:amd-edf:versal = "${DEPLOY_DIR_IMAGE}/bootbin-version-string-${MACHINE}.txt, id=0x21;"
BIF_OPTIONAL_DATA:append:amd-edf:versal = "${DEPLOY_DIR_IMAGE}/bootbin-rollback-counter-${MACHINE}.bin, id=0x22;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-net = "${DEPLOY_DIR_IMAGE}/bootbin-version-string-${MACHINE}.txt, id=0x21;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-net = "${DEPLOY_DIR_IMAGE}/bootbin-rollback-counter-${MACHINE}.bin, id=0x22;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-2ve-2vm = "${DEPLOY_DIR_IMAGE}/bootbin-version-string-${MACHINE}.txt, id=0x21;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-2ve-2vm = "${DEPLOY_DIR_IMAGE}/bootbin-rollback-counter-${MACHINE}.bin, id=0x22;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-2ve-2vm = "${DEPLOY_DIR_IMAGE}/base-pdi-unique-id-${MACHINE}.txt, id=0x23;"

# Embed the component manifest JSON in BOOT.BIN as optional data (Versal only)
# id=0x24; max size per entry is 128KB, manifest is typically < 1KB
BIF_OPTIONAL_DATA:append:amd-edf:versal = "${MANIFEST_AGGREGATE_OUTPUT}, id=0x24;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-net = "${MANIFEST_AGGREGATE_OUTPUT}, id=0x24;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-2ve-2vm = "${MANIFEST_AGGREGATE_OUTPUT}, id=0x24;"

# Avoid EDF specific circular dependencies
EXTRA_IMAGEDEPENDS:remove = "edf-qspi"
EXTRA_IMAGEDEPENDS:remove = "edf-ospi"
