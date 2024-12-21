DESCRIPTION = "Common packages for the Basecamp distro and images"
LICENSE = "MIT"

# By default wic is not enable in IMAGE_FSTYPES so enable WIC image type support.
IMAGE_FSTYPES += "wic wic.xz wic.bmap"

#### Disable Busybox
SKIP_RECIPE[busybox] = "Busybox disabled"

# Minimal set packages for Basecamp images
BASECAMP_IMAGE_COMMON_INSTALL = "\
    ${@bb.utils.contains('MACHINE_FEATURES', 'fpga-overlay', ' dfx-mgr', '', d)} \
    libgpiod \
    libgpiod-tools \
    devmem2 \
    tree \
    kernel-modules \
    mtd-utils \
    fwupd \
    "

# Full set of packages for Basecamp images
BASECAMP_IMAGE_FULL_INSTALL = " \
    ${BASECAMP_IMAGE_COMMON_INSTALL} \
    container-host-config \
    docker-moby-contrib \
    packagegroup-docker \
    packagegroup-oci \
    vim \
    xrt \
    "

# Install versal-common fw recipes.
VERSAL_COMMON_INSTALL = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'openamp', ' packagegroup-openamp', '', d)} \
    "

# Install versal2-common fw recipes.
VERSAL2_COMMON_INSTALL = "\
    isp-firmware \
    kernel-module-isp \
    "

BASECAMP_IMAGE_COMMON_INSTALL:append:versal-common = " ${VERSAL_COMMON_INSTALL}"
BASECAMP_IMAGE_COMMON_INSTALL:append:versal2-common = " ${VERSAL2_COMMON_INSTALL}"
