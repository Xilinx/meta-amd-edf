# SPDX-License-Identifier: MIT
#
# Copyright (C) 2025-2026, Advanced Micro Devices, Inc. All rights reserved.
#
# uefi-capsule.bb - Generate UEFI capsule update artifacts
#
# This recipe generates firmware update capsules compatible with:
#   - UEFI Capsule Update specification
#   - Arm SystemReady IR (IoT Ready) certification
#   - LVFS (Linux Vendor Firmware Service) / fwupd
#
# Artifacts produced:
#   - *-metadata.bin     : FWU metadata for U-Boot A/B bank switching
#   - *-capsule.bin      : UEFI capsule containing boot.bin firmware
#   - *-acceptance-capsule.bin : Capsule to accept/commit an update
#   - *-bootfw-firmware.cab    : Cabinet file for fwupd/LVFS submission
#
# =============================================================================
# GUID/UUID Reference
# =============================================================================
#
# This recipe uses multiple GUIDs for different purposes in the UEFI FWU
# (Firmware Update) specification. Here's what each one is for:
#
# PRODUCT_GUID (required, set by machine config)
# ----------------------------------------------
#   The unique identifier for this firmware/product. This GUID:
#   - Appears in the UEFI capsule header
#   - Is matched by fwupd to identify which device the capsule applies to
#   - Must match the GUID advertised in the device's ESRT (EFI System Resource Table)
#   - Should be unique per product/board variant
#   Example: "cb27e54d-8f3a-4c77-8a72-1c76d2d4e938"
#
# LOC_GUID (FWU metadata location identifier)
# -------------------------------------------
#   Identifies the storage location for FWU metadata within the flash layout.
#   This is a fixed GUID for the AMD EDF platform layout and typically does
#   not need to be changed unless the flash partitioning scheme changes.
#   Default: "588aced7-2cce-ed11-81cd-d324e93ac223"
#
# IMG_0_GUID_0 / IMG_0_GUID_1 (FWU bank image identifiers)
# --------------------------------------------------------
#   These identify the firmware images in Bank A (IMG_0_GUID_0) and
#   Bank B (IMG_0_GUID_1) for the A/B update scheme. They must match
#   the UUIDs defined in the device tree's fwu-mdata node:
#
#     fwu-bank0 { fwu-image0 { uuid = "0b931b7e-..."; } }  <- IMG_0_GUID_0
#     fwu-bank1 { fwu-image0 { uuid = "1243d800-..."; } }  <- IMG_0_GUID_1
#
#   These are typically constant across all boards using the same flash layout.
#
# =============================================================================

SUMMARY = "Generate UEFI capsule update artifacts"
DESCRIPTION = "Generates UEFI capsule update artifacts compatible with LVFS and fwupd"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "gcab-native appstream-native u-boot-mkeficapsule-native u-boot-mkfwumdata-native"

PV = "${VER_STRING}"

SRC_URI = "file://firmware.metainfo.xml.in"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy image-artifact-names nopackages

IMAGE_NAME_SUFFIX = ""

INHIBIT_DEFAULT_DEPS = "1"

# =============================================================================
# Product Identification (MUST be set by machine config or distro)
# =============================================================================

# Human-readable product name (e.g., "VEK280", "VEK385")
PRODUCT_NAME ?= ""

# Product homepage URL for LVFS metadata
PRODUCT_URL ?= ""

# Unique product GUID - see documentation above
# Generate a new one with: uuidgen
PRODUCT_GUID ?= ""

# =============================================================================
# LVFS Cabinet Metadata
# =============================================================================

UEFI_CAB_RELEASE_DESCRIPTION ?= "Boot firmware update."
UEFI_CAB_DEVELOPER_NAME ?= "AMD"
UEFI_CAB_FW_NAME ?= "AMD EDF ${PRODUCT_NAME} Boot Firmware"
UEFI_CAB_FW_SUMMARY ?= "Boot Firmware Update"
UEFI_CAB_FW_DESCRIPTION ?= "UEFI capsule update for ${PRODUCT_NAME} boot firmware."
UEFI_CAB_ID ?= "com.amd.edf.${@''.join(c.lower() for c in d.getVar('PRODUCT_NAME') if c.isalnum())}.bootfirmware"
UEFI_CAB_UPDATE_PROTOCOL ?= "org.uefi.capsule"

# Task vardeps for metainfo rendering.  These variables are consumed indirectly
# by the embedded Python template expansion (not as explicit ${VAR} references),
# so they must be listed to avoid stale sstate reuse when metadata changes.
UEFI_CAB_META_VARS = " \
    PRODUCT_NAME PRODUCT_GUID PRODUCT_URL \
    UEFI_CAB_ID UEFI_CAB_FW_NAME UEFI_CAB_FW_SUMMARY UEFI_CAB_FW_DESCRIPTION \
    UEFI_CAB_DEVELOPER_NAME UEFI_CAB_RELEASE_DESCRIPTION \
    VER_STRING VER_FORMAT UEFI_CAB_UPDATE_PROTOCOL \
"

# =============================================================================
# FWU Metadata GUIDs (see documentation above for details)
# =============================================================================

# Storage location GUID for FWU metadata partition
LOC_GUID ?= "588aced7-2cce-ed11-81cd-d324e93ac223"

# Image GUIDs for A/B banks - must match device tree fwu-mdata node
IMG_0_GUID_0 ?= "0b931b7e-b2f6-11ef-8565-eb65d140066b"
IMG_0_GUID_1 ?= "1243d800-b2f6-11ef-8f4f-8bddc3aa326d"

# =============================================================================
# GUID Byte-Order Conversion for FWU Metadata
# =============================================================================
#
# mkeficapsule stores GUIDs as little-endian, mkfwumdata as big-endian.
# Swap first 3 fields so ESRT matches capsule GUID.

python set_metadata_guid() {
    p = d.getVar('PRODUCT_GUID').split('-')
    swap = lambda s: ''.join([s[i:i+2] for i in range(0, len(s), 2)][::-1])
    d.setVar('METADATA_GUID', f"{swap(p[0])}-{swap(p[1])}-{swap(p[2])}-{p[3]}-{p[4]}")
}
do_compile[prefuncs] += "set_metadata_guid"

# =============================================================================
# Version Configuration (LVFS pair format)
# =============================================================================
#
# LVFS uses "pair" versioning: two 16-bit integers (major.minor) packed into
# a 32-bit value. Capsule versions must be monotonically increasing per
# PRODUCT_GUID, independent of the EDF release version.
#
# Override in local.conf or distro config:
#   VER_MAJOR:pn-uefi-capsule = "2"
#   VER_MINOR:pn-uefi-capsule = "1"

VER_MAJOR ?= "1"
VER_MINOR ?= "0"
VER_STRING ?= "${VER_MAJOR}.${VER_MINOR}"
VER_RAW_PAIR ?= "${@(int(d.getVar('VER_MAJOR')) << 16) | (int(d.getVar('VER_MINOR')) & 0xFFFF)}"
VER_FORMAT ?= "pair"

# Use SOURCE_DATE_EPOCH for reproducible builds, fall back to DATE
def get_lvfs_date(d):
    import time
    epoch = d.getVar('SOURCE_DATE_EPOCH')
    if epoch:
        return time.strftime('%Y-%m-%d', time.gmtime(int(epoch)))
    date = d.getVar('DATE')
    return '{}-{}-{}'.format(date[0:4], date[4:6], date[6:8])

LVFS_DATE = "${@get_lvfs_date(d)}"
LVFS_DATE[vardepsexclude] += "DATE SOURCE_DATE_EPOCH"

# =============================================================================
# Internal Variables
# =============================================================================

CAPSULE_VENDOR_BLOB = "${WORKDIR}/${PN}-vendor.txt"
CAPSULE_META_TEMPLATE = "${WORKDIR}/firmware.metainfo.xml.in"
CAPSULE_META_OUTPUT = "${WORKDIR}/firmware.metainfo.xml"

python do_configure() {
    import re

    # Validate required variables
    required = ["PRODUCT_GUID", "PRODUCT_NAME", "PRODUCT_URL"]
    missing = [var for var in required if not d.getVar(var)]
    if missing:
        bb.fatal("uefi-capsule: missing required metadata (%s). "
                 "These must be set in your machine config." % ", ".join(missing))

    # Validate GUID format (8-4-4-4-12 hex pattern with valid hex characters)
    guid = d.getVar('PRODUCT_GUID')
    guid_pattern = r'^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$'
    if not re.match(guid_pattern, guid):
        bb.fatal("uefi-capsule: PRODUCT_GUID '%s' is not a valid UUID format "
                 "(expected xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx with hex digits)" % guid)

    # Validate PRODUCT_URL is a valid URL (LVFS requires this)
    url = d.getVar('PRODUCT_URL')
    if not url.startswith(('http://', 'https://')):
        bb.fatal("uefi-capsule: PRODUCT_URL '%s' must be a valid HTTP/HTTPS URL" % url)

    # Warn about common LVFS issues
    name = d.getVar('PRODUCT_NAME')
    if len(name) < 2:
        bb.warn("uefi-capsule: PRODUCT_NAME '%s' is very short - LVFS may reject it" % name)

    # Validate version numbers are within range for LVFS pair format (16-bit each)
    ver_minor = int(d.getVar('VER_MINOR'))
    ver_major = int(d.getVar('VER_MAJOR'))
    if ver_minor > 65535 or ver_major > 65535:
        bb.fatal("uefi-capsule: VER_MAJOR and VER_MINOR must be <= 65535 for LVFS pair format")

    # The capsule tools expect a four-byte vendor blob
    with open(d.getVar('CAPSULE_VENDOR_BLOB'), 'wb') as vendor:
        vendor.write(b'\x04\x00\x00\x00')
}

do_compile[depends] += "virtual/boot-bin:do_deploy"
do_compile() {
    # Generate FWU metadata blob for U-Boot A/B bank management
    # Arguments: -a <active_bank> -b <num_banks> -i <num_images> -v <metadata_format_version>
    # Note: -v 2 is the FWU metadata FORMAT version (v1 or v2), not firmware version
    # GUIDs: <location>,<image_type>,<bank0_image>,<bank1_image>
    mkfwumdata -a 0 -b 2 -i 1 -v 2 \
        ${LOC_GUID},${METADATA_GUID},${IMG_0_GUID_0},${IMG_0_GUID_1} \
        "${WORKDIR}/${PN}-metadata.bin" -V "${CAPSULE_VENDOR_BLOB}"

    # Generate UEFI capsule from boot.bin
    # -o: OEM flags, -g: GUID, --index: image index, -v: firmware version (VER_RAW_PAIR)
    mkeficapsule -o 0x8000 -g ${PRODUCT_GUID} --index 1 -v ${VER_RAW_PAIR} \
        "${DEPLOY_DIR_IMAGE}/boot.bin" "${WORKDIR}/firmware.bin"

    # Generate acceptance capsule (used to commit an update after successful boot)
    mkeficapsule -A -g ${PRODUCT_GUID} "${WORKDIR}/${PN}-bootfw-acceptance-capsule.bin"
}

python do_generate_metainfo() {
    """Generate AppStream metainfo XML from template.

    Reads the template, computes firmware checksum, expands variables,
    and validates the result against AppStream specification.
    """
    import hashlib
    import subprocess
    from pathlib import Path

    firmware = Path(d.getVar("WORKDIR")) / "firmware.bin"
    checksum = hashlib.sha256(firmware.read_bytes()).hexdigest()
    d.setVar("FW_CHECKSUM", checksum)

    template_path = Path(d.getVar("CAPSULE_META_TEMPLATE"))
    output_path = Path(d.getVar("CAPSULE_META_OUTPUT"))
    output_path.write_text(d.expand(template_path.read_text(encoding="utf-8")), encoding="utf-8")

    # Validate metainfo against AppStream spec before packaging
    result = subprocess.run(
        ["appstreamcli", "validate", "--pedantic", str(output_path)],
        capture_output=True, text=True
    )
    if result.returncode != 0:
        output = result.stdout.strip() or result.stderr.strip()
        bb.warn("AppStream validation issues (cabinet may not pass LVFS checks):\n%s" % output)
}
do_generate_metainfo[vardeps] += "${UEFI_CAB_META_VARS}"
do_generate_metainfo[dirs] = "${WORKDIR}"
addtask do_generate_metainfo after do_compile before do_create_cabinet

do_create_cabinet() {
    # Package firmware and metainfo into LVFS-compatible cabinet
    gcab --create --nopath \
        "${WORKDIR}/${PN}-bootfw-firmware.cab" \
        "${WORKDIR}/firmware.bin" \
        "${CAPSULE_META_OUTPUT}"

    bbnote "Cabinet contents:"
    gcab --list "${WORKDIR}/${PN}-bootfw-firmware.cab"
}
do_create_cabinet[dirs] = "${WORKDIR}"
addtask do_create_cabinet after do_generate_metainfo before do_deploy

do_deploy() {
    install -d ${DEPLOYDIR}

    # FWU metadata (written to SPI flash by edf-ospi/edf-qspi recipes)
    install -Dm 0644 ${WORKDIR}/${PN}-metadata.bin ${DEPLOYDIR}/${IMAGE_NAME}-metadata.bin
    ln -sf ${IMAGE_NAME}-metadata.bin ${DEPLOYDIR}/${IMAGE_LINK_NAME}-metadata.bin

    # UEFI capsule (can be placed on ESP for capsule-on-disk update)
    install -Dm 0644 ${WORKDIR}/firmware.bin ${DEPLOYDIR}/${IMAGE_NAME}-capsule.bin
    ln -sf ${IMAGE_NAME}-capsule.bin ${DEPLOYDIR}/${IMAGE_LINK_NAME}-capsule.bin

    # Acceptance capsule (used to accept/commit firmware update)
    install -Dm 0644 ${WORKDIR}/${PN}-bootfw-acceptance-capsule.bin ${DEPLOYDIR}/${IMAGE_NAME}-acceptance-capsule.bin
    ln -sf ${IMAGE_NAME}-acceptance-capsule.bin ${DEPLOYDIR}/${IMAGE_LINK_NAME}-acceptance-capsule.bin

    # Cabinet file for fwupd/LVFS
    install -Dm 0644 ${WORKDIR}/${PN}-bootfw-firmware.cab ${DEPLOYDIR}/${IMAGE_NAME}-bootfw-firmware.cab
    ln -sf ${IMAGE_NAME}-bootfw-firmware.cab ${DEPLOYDIR}/${IMAGE_LINK_NAME}-bootfw-firmware.cab

    # User-friendly cabinet name for LVFS submission
    ln -sf ${IMAGE_NAME}-bootfw-firmware.cab ${DEPLOYDIR}/amd-edf-${@d.getVar('PRODUCT_NAME').lower()}-v${VER_STRING}.cab
}

addtask do_deploy after do_create_cabinet
