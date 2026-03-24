#
# Copyright (C) 2026, Advanced Micro Devices, Inc.  All rights reserved.
#
# SPDX-License-Identifier: MIT
#

SUMMARY = "OSPI image for AMD Embedded Development Framework"
DESCRIPTION = "OSPI image with image selector, image recovery, update metadata and boot.bin"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit amd-spi-image deploy image-artifact-names

COMPATIBLE_MACHINE = "^$"
PACKAGE_ARCH = "${MACHINE_ARCH}"

# OSPI Memory Map (256MB default)
# All offsets can be overridden per-machine in machineyaml or bbappends
SPI_IMAGE_SIZE ?= "0x1000_0000"
SPI_FLASH_TYPE = "OSPI"
SPI_ERASE_BLOCK_SIZE = "0x20000"
SPI_IMGSEL_OFFSET           ?= "0x0 0x6_0000"
SPI_IMGSEL_SCRATCH_OFFSET   ?= "0xC_0000"
SPI_RECOVERY_OFFSET         ?= "0xE_0000"
SPI_RECOVERY_SCRATCH_OFFSET ?= "0x14E_0000"
SPI_UPDATE_META_OFFSET      ?= "0x150_0000 0x152_0000"
SPI_UBOOT_ENV_OFFSET        ?= "0x154_0000 0x156_0000"
SPI_BOOTBIN_OFFSET          ?= "0x15C_0000 0x880_0000"
SPI_USER_SCRATCH_OFFSET     ?= "0xFA4_0000"
SPI_MANIFEST_OFFSET         ?= "0xFFE_0000"

SPI_COMPONENTS ?= "imgsel imgsel_scratch recovery recovery_scratch update_meta uboot_env bootbin user_scratch manifest"

# Image selector - primary + backup
SPI_OFFSET[imgsel] = "${SPI_IMGSEL_OFFSET}"
SPI_SOURCE[imgsel] = "image-selector-${MACHINE}.bin"

# Image selector scratchpad (reserved)
SPI_OFFSET[imgsel_scratch] = "${SPI_IMGSEL_SCRATCH_OFFSET}"
SPI_SOURCE[imgsel_scratch] = "@empty"

# Image recovery
SPI_OFFSET[recovery] = "${SPI_RECOVERY_OFFSET}"
SPI_SOURCE[recovery] = "${IMGRCRY_IMAGE_NAME}.bin"
IMGRCRY_IMAGE_NAME ??= "image-recovery-${MACHINE}"

# Image recovery scratchpad (reserved)
SPI_OFFSET[recovery_scratch] = "${SPI_RECOVERY_SCRATCH_OFFSET}"
SPI_SOURCE[recovery_scratch] = "@empty"

# SystemReady-DT update metadata - primary + backup
SPI_OFFSET[update_meta] = "${SPI_UPDATE_META_OFFSET}"
SPI_UPDATE_META_SOURCE ?= "${@'uefi-capsule-' + d.getVar('MACHINE') + '-metadata.bin' if d.getVar('PRODUCT_GUID') else '@empty'}"
SPI_SOURCE[update_meta] = "${SPI_UPDATE_META_SOURCE}"

# U-Boot environment - primary + backup (reserved)
SPI_OFFSET[uboot_env] = "${SPI_UBOOT_ENV_OFFSET}"
SPI_SOURCE[uboot_env] = "@empty"

# Boot.bin - A/B slots
SPI_OFFSET[bootbin] = "${SPI_BOOTBIN_OFFSET}"
SPI_SOURCE[bootbin] = "boot.bin"

# User scratchpad (reserved)
SPI_OFFSET[user_scratch] = "${SPI_USER_SCRATCH_OFFSET}"
SPI_SOURCE[user_scratch] = "@empty"

# OSPI manifest (last sector, 128KB erase sector size)
SPI_OFFSET[manifest] = "${SPI_MANIFEST_OFFSET}"
SPI_SOURCE[manifest] = "@manifest"

SPI_CAPSULE_DEPENDS = "${@'uefi-capsule' if d.getVar('PRODUCT_GUID') else ''}"
SPI_DEPLOY_DEPENDS ?= "virtual/imgsel virtual/imgrcry ${SPI_CAPSULE_DEPENDS} virtual/boot-bin"
do_compile[vardeps] += "SPI_IMGSEL_OFFSET SPI_IMGSEL_SCRATCH_OFFSET \
    SPI_RECOVERY_OFFSET SPI_RECOVERY_SCRATCH_OFFSET \
    SPI_UPDATE_META_OFFSET SPI_UBOOT_ENV_OFFSET \
    SPI_BOOTBIN_OFFSET SPI_USER_SCRATCH_OFFSET SPI_MANIFEST_OFFSET \
    IMGRCRY_IMAGE_NAME"

do_configure[noexec] = "1"
do_install[noexec] = "1"

# QEMU boot support
QEMU_FLASH_FILE = "${IMAGE_NAME}"
SPI_BOOTBIN_A_OFFSET = "${@d.getVar('SPI_BOOTBIN_OFFSET').split()[0]}"
QB_OPT_APPEND += "-boot multiboot=${@int(int(d.getVar('SPI_BOOTBIN_A_OFFSET').replace('_',''), 0) / 32 / 1024)}"
