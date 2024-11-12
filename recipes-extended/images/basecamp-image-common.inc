DESCRIPTION = "An image containing the set of packages for basecamp distro."
LICENSE = "MIT"

# By default wic is not enable in IMAGE_FSTYPES so enable WIC image type support.
IMAGE_FSTYPES += "wic wic.xz wic.bmap"

IMAGE_BOOT_FILES:remove = "boot.bin"

#### Disable Busybox
SKIP_RECIPE[busybox] = "Busybox disabled"

# splash feature is not used for AMD adaptive socs, hence remove it.
IMAGE_FEATURES:remove = "splash"

## FIXME add CONTAINER_PROFILE

# Use CORE_IMAGE_EXTRA_INSTALL variable from core-image.bbclass
CORE_IMAGE_EXTRA_INSTALL:append = "\
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
    vim \
    "

# Install versal-common fw recipes.
VERSAL_COMMON_INSTALL = "\
    vek280-pl-bram-uart-gpio-fw \
    "

IMAGE_INSTALL:append:versal-common = " ${VERSAL_COMMON_INSTALL}"
