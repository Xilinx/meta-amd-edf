COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:amd-cortexa53-mali-common = "${MACHINE}"
COMPATIBLE_MACHINE:kria = "${MACHINE}"

IMAGE_FEATURES += "splash hwcodecs"

IMAGE_INSTALL += " \
    nfs-utils-client \
    nfs-utils \
    packagegroup-kria \
    u-boot-tools \
    udev-extraconf \
"
