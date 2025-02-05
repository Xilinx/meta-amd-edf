DESCRIPTION = "wic Image Manipulator"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ee23c52855c222cba72583d301d2338"

SRC_URI = "git://github.com/Xilinx/${BPN};protocol=https;branch=master"
SRCREV = "bd6f23ac4438a4409a1d953fbd7477be6b5b1b21"
S = "${WORKDIR}/git"

inherit python_poetry_core

BBCLASSEXTEND = "native nativesdk"
