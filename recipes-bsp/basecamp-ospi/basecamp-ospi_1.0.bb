SUMMARY = "OSPI image for Basecamp"
DESCRIPTION = "OSPI image for Basecamp with image selector, image recovery, capsule metadata and boot.bin"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit amd-basecamp-spi-image

COMPATIBLE_MACHINE:versal-vek280-sdt-seg-ospi = "${MACHINE}"
COMPATIBLE_MACHINE:versal2-vek385-sdt-seg = "${MACHINE}"
