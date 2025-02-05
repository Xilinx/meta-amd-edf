SUMMARY = "Package for building a installable toolchain for Basecamp SDK"
LICENSE = "MIT"

PR = "r0"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:versal-common = "${MACHINE}"
COMPATIBLE_MACHINE:versal2-common = "${MACHINE}"

inherit populate_sdk amd-qemu-xilinx-sdk-tools amd-misc-sdk-tools

# add these items to the "cross" side of the SDK
TOOLCHAIN_TARGET_TASK:append = " \
    packagegroup-vitis-aiml-dev \
    kernel-devsrc \
    xrt-dev \
    "

# add these items to the "native" side of the SDK
# i.e. these tools are built to run on the build host
TOOLCHAIN_HOST_TASK:append = " \
	nativesdk-packagegroup-vitis-aiml \
	nativesdk-packagegroup-basecamp-tools \
	"
