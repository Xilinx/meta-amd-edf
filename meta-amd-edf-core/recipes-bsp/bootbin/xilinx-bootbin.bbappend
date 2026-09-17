# Add bootbin-version-header to the manifest for zynq/zynqmp
# Note += is correct here, as we're appending to the value before the
# override is processed.  If the implementation changes in the base
# .bb, then this may need to change to match.
MANIFEST_AGGREGATE_COMPONENTS:zynq += "bootbin-version-header"
MANIFEST_AGGREGATE_COMPONENTS:zynqmp += "bootbin-version-header"

MANIFEST_AGGREGATE_DEPENDS:zynq += "bootbin-version-header"
MANIFEST_AGGREGATE_DEPENDS:zynqmp += "bootbin-version-header"

BOOTBIN_DEPENDS ?= ""
BOOTBIN_DEPENDS:append:amd-edf:zynq = " bootbin-version-header:do_deploy"
BOOTBIN_DEPENDS:append:amd-edf:zynqmp = " bootbin-version-header:do_deploy"
do_configure[depends] += "${BOOTBIN_DEPENDS}"
# The aggregate manifest hashes deploy-path BIF images (e.g. version-header), so
# it must run after those artifacts are deployed, not just before do_configure.
do_manifest_aggregate[depends] += "${BOOTBIN_DEPENDS}"

# User Defined Field Boot Header for version tracking on zynq/zynqmp
# For Kria machines, meta-kria sets BIF_UDFBH_ATTR:kria which takes precedence
BIF_UDFBH_ATTR:amd-edf:zynq = "bootbin-version-header"
BIF_UDFBH_ATTR:amd-edf:zynqmp = "bootbin-version-header"
BIF_PARTITION_ATTR[bootbin-version-header] = "udf_bh"
# Use variable indirection so meta-kria can override the path for Kria machines
BIF_VERSION_HEADER_IMAGE ?= "${DEPLOY_DIR_IMAGE}/bootbin-version-header-${MACHINE}.txt"
BIF_PARTITION_IMAGE[bootbin-version-header] = "${BIF_VERSION_HEADER_IMAGE}"

# Boot-image metadata embedded as BIF optional data
#   id=0x21 description
#   id=0x22 component version
#   id=0x23 tool version
#   id=0x24 build id (absent when no id is supplied)
#   id=0x25 = component manifest JSON (see shared-manifest-aggregate.bbclass).

BOOTBIN_VERSION ?= "1.2"
BOOTBIN_DESCRIPTION ?= "AMD EDF Boot Firmware (${MACHINE})"
BOOTBIN_TOOLS_VERSION ?= "${@(d.getVar('XILINX_RELEASE_VERSION') or '').replace('v', '')}"
# Build id of the automation build; empty for a local build.
BOOTBIN_VER_BUILD ??= ""

BIF_OPTIONAL_DATA_VAL[0x21] = "${BOOTBIN_DESCRIPTION}"
BIF_OPTIONAL_DATA_VAL[0x22] = "${BOOTBIN_VERSION}"
BIF_OPTIONAL_DATA_VAL[0x23] = "${BOOTBIN_TOOLS_VERSION}"
BIF_OPTIONAL_DATA_VAL[0x24] = "${BOOTBIN_VER_BUILD}"

# Embed the component manifest JSON in BOOT.BIN as optional data (Versal only)
# id=0x25; max size per entry is 128KB, manifest is typically < 1KB
BIF_OPTIONAL_DATA_FILE[0x25] = "${MANIFEST_AGGREGATE_OUTPUT}"

# Avoid EDF specific circular dependencies
EXTRA_IMAGEDEPENDS:remove = "edf-qspi"
EXTRA_IMAGEDEPENDS:remove = "edf-ospi"

do_deploy:append() {
    cat << 'EOF' > ${DEPLOYDIR}/${BOOTBIN_BASE_NAME}.version.txt
${BOOTBIN_DESCRIPTION}
${BOOTBIN_VERSION}
${BOOTBIN_TOOLS_VERSION}
${BOOTBIN_VER_BUILD}
EOF
    ln -sf ${BOOTBIN_BASE_NAME}.version.txt ${DEPLOYDIR}/${BOOTBIN_LINK_NAME}.version.txt
    ln -sf ${BOOTBIN_BASE_NAME}.version.txt ${DEPLOYDIR}/bootbin-version.txt
}
