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
BOOTBIN_DEPENDS:append:amd-edf:versal = " bootbin-rollback-counter:do_deploy bootbin-version-string:do_deploy"
BOOTBIN_DEPENDS:append:amd-edf:versal-net = " bootbin-rollback-counter:do_deploy bootbin-version-string:do_deploy"
BOOTBIN_DEPENDS:append:amd-edf:versal-2ve-2vm = " bootbin-rollback-counter:do_deploy bootbin-version-string:do_deploy"
# Image Store section needs the FPD CDO deployed before the BIF is written
BOOTBIN_DEPENDS:append:amd-edf:versal-2ve-2vm-vek385-revb-sdt-seg = " cdo-deploy:do_deploy"
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

# Versal BIF optional data:
#   id=0x21 = human-readable version string (text)
#   id=0x22 = rollback counter (binary)
#   id=0x24 = component manifest JSON (see shared-manifest-aggregate.bbclass)
BIF_OPTIONAL_DATA:append:amd-edf:versal = "${DEPLOY_DIR_IMAGE}/bootbin-version-string-${MACHINE}.txt, id=0x21;"
BIF_OPTIONAL_DATA:append:amd-edf:versal = "${DEPLOY_DIR_IMAGE}/bootbin-rollback-counter-${MACHINE}.bin, id=0x22;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-net = "${DEPLOY_DIR_IMAGE}/bootbin-version-string-${MACHINE}.txt, id=0x21;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-net = "${DEPLOY_DIR_IMAGE}/bootbin-rollback-counter-${MACHINE}.bin, id=0x22;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-2ve-2vm = "${DEPLOY_DIR_IMAGE}/bootbin-version-string-${MACHINE}.txt, id=0x21;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-2ve-2vm = "${DEPLOY_DIR_IMAGE}/bootbin-rollback-counter-${MACHINE}.bin, id=0x22;"

# Embed the component manifest JSON in BOOT.BIN as optional data (Versal only)
# id=0x24; max size per entry is 128KB, manifest is typically < 1KB
BIF_OPTIONAL_DATA:append:amd-edf:versal = "${MANIFEST_AGGREGATE_OUTPUT}, id=0x24;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-net = "${MANIFEST_AGGREGATE_OUTPUT}, id=0x24;"
BIF_OPTIONAL_DATA:append:amd-edf:versal-2ve-2vm = "${MANIFEST_AGGREGATE_OUTPUT}, id=0x24;"

# Image Store: package the FPD CDO into a named section so PLM can reload it on
# resume from RAM instead of re-reading the boot device.
BIF_SECTIONS:amd-edf:versal-2ve-2vm-vek385-revb-sdt-seg = "image_store_section the_ROM_image"
BIF_PARTITION_ATTR:append:amd-edf:versal-2ve-2vm-vek385-revb-sdt-seg = " image-store-ref"

# The FPD CDO is real content in BOOT.BIN, so track it like any other component
MANIFEST_AGGREGATE_COMPONENTS:append:amd-edf:versal-2ve-2vm-vek385-revb-sdt-seg = " fpd-cdo"
MANIFEST_COMPONENT_IMAGES:append:amd-edf:versal-2ve-2vm-vek385-revb-sdt-seg = " fpd-cdo=${DEPLOY_DIR_IMAGE}/CDO/fpd_data.cdo"

# Avoid EDF specific circular dependencies
EXTRA_IMAGEDEPENDS:remove = "edf-qspi"
EXTRA_IMAGEDEPENDS:remove = "edf-ospi"
