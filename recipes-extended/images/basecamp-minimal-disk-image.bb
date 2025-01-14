DESCRIPTION = "Minimal Basecamp disk image containing only basecamp-image-full-cmdline"
LICENSE ?= "MIT"
PACKAGES = ""

EXCLUDE_FROM_WORLD = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

INHIBIT_DEFAULT_DEPS = "1"

require basecamp-image-full-cmdline.bb

# By default wic is not enable in IMAGE_FSTYPES so enable WIC image type support.
IMAGE_FSTYPES = "wic wic.xz wic.bmap wic.qemu-sd"

IMGCLASSES += "image-types-xilinx-qemu"

WKS_FILES = "basecamp-disk-single-rootfs.wks"
