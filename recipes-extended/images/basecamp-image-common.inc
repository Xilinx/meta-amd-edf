DESCRIPTION = "An image containing the set of packages for basecamp distro."
LICENSE = "MIT"

# By default wic is not enable in IMAGE_FSTYPES so enable WIC image type support.
IMAGE_FSTYPES += "wic wic.xz wic.bmap"

IMAGE_BOOT_FILES:remove = "boot.bin"

#### Disable Busybox
SKIP_RECIPE[busybox] = "Busybox disabled"

# splash feature is not used for AMD adaptive socs, hence remove it.
IMAGE_FEATURES:remove = "splash"

# Use CORE_IMAGE_EXTRA_INSTALL variable from core-image.bbclass
CORE_IMAGE_EXTRA_INSTALL:append = "\
    ${@bb.utils.contains('MACHINE_FEATURES', 'fpga-overlay', ' dfx-mgr', '', d)} \
    "

# Install xlnx-versal-vek280-revb packages
XLNX_VERSAL_VEK280_REVB_INSTALL = "\
    vek280-sdt-seg-cfg \
    "

IMAGE_INSTALL:append:xlnx-versal-vek280-revb-ospi = " ${XLNX_VERSAL_VEK280_REVB_INSTALL}"
