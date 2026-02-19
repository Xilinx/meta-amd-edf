require recipes-extended/images/edf-linux-disk-image.bb

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:amd-cortexa53-mali-common = "${MACHINE}"

IMAGE_FEATURES += "splash hwcodecs"

IMAGE_INSTALL += " \
    nfs-utils-client \
    nfs-utils \
    packagegroup-kria \
    u-boot-tools \
    udev-extraconf \
    wireless-regdb-static \
    ${@bb.utils.contains('DISTRO_FEATURES', 'openamp', 'k24-openamp-fw-examples k26-openamp-fw-examples', '', d)} \
"
