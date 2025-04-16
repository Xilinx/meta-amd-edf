DESCRIPTION = "Minimal AMD Embedded Development Framework disk image containing only edf-image-full-cmdline"
LICENSE ?= "MIT"
PACKAGES = ""

EXCLUDE_FROM_WORLD = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

INHIBIT_DEFAULT_DEPS = "1"

require edf-image-full-cmdline.bb

# For the minimal image we do not want OpenAMP, even if the distro feature is enabled
OPENAMP_COMMON_INSTALL = ""

IMGCLASSES += "image-types-xilinx-qemu"

WKS_FILES = "edf-disk-single-rootfs.wks"
