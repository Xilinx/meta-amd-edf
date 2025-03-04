DESCRIPTION = "wic Image Manipulator"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4f0802e55821766fdb26000965ec2bdf"

SRC_URI = "git://git.yoctoproject.org/${BPN};protocol=https;branch=no-create"
SRCREV = "985a5ff29ccc0c312907d77f9ca98d0890e50541"
S = "${WORKDIR}/git"

inherit python_poetry_core

BBCLASSEXTEND = "native nativesdk"
