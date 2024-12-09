DESCRIPTION = "Common packages for the Basecamp distro and images"
LICENSE = "MIT"

# By default wic is not enable in IMAGE_FSTYPES so enable WIC image type support.
IMAGE_FSTYPES += "wic wic.xz wic.bmap"

#### Disable Busybox
SKIP_RECIPE[busybox] = "Busybox disabled"

# Use CORE_IMAGE_EXTRA_INSTALL variable from core-image.bbclass
BASECAMP_IMAGE_COMMON_INSTALL = "\
    ${@bb.utils.contains('MACHINE_FEATURES', 'fpga-overlay', ' dfx-mgr', '', d)} \
    libgpiod \
    libgpiod-tools \
    devmem2 \
    tree \
    kernel-modules \
    mtd-utils \
    container-host-config \
    docker-moby-contrib \
    packagegroup-docker \
    packagegroup-oci \
    packagegroup-openamp \
    vim \
    xrt \
    fwupd \
    "

# Install versal-common fw recipes.
VERSAL_COMMON_INSTALL = "\
    vek280-pl-bram-uart-gpio-fw \
    "
# Install versal2-common fw recipes.
VERSAL2_COMMON_INSTALL = "\
    isp-firmware \
    kernel-module-isp \
    "

BASECAMP_IMAGE_COMMON_INSTALL:append:versal-common = " ${VERSAL_COMMON_INSTALL}"
BASECAMP_IMAGE_COMMON_INSTALL:append:versal2-common = " ${VERSAL2_COMMON_INSTALL}"
