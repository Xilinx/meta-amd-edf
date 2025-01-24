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
    ${AARCH64_COMMON_INSTALL} \
    ${BASECAMP_IMAGE_COMMON_INSTALL} \
    can-utils \
    container-host-config \
    docker-moby-contrib \
    dosfstools \
    dtc \
    e2fsprogs-mke2fs \
    htop \
    iperf3 \
    linux-xlnx-udev-rules \
    packagegroup-docker \
    packagegroup-oci \
    systemtap \
    tcf-agent \
    u-boot-tools-xlnx \
    vim \
    xrt \
    zip \
    "

OPENAMP_COMMON_INSTALL = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'openamp', ' packagegroup-openamp', '', d)} \
    "

AARCH64_COMMON_INSTALL = "\
    libdrm \
    libdrm-tests \
    packagegroup-xilinx-audio \
    packagegroup-xilinx-gstreamer \
    v4l-utils \
    yavta \
"

ZYNQMP_COMMON_INSTALL = "\
    "

ZYNQMP_MALI_COMMON_INSTALL = "\
    libmali-xlnx \
    "
# FIXME Temporarily remove x11 and matchbox
# packagegroup-xilinx-matchbox
# packagegroup-xilinx-x11

VERSAL_COMMON_INSTALL = "\
    ${OPENAMP_COMMON_INSTALL} \
    "

VERSAL2_COMMON_INSTALL = "\
    isp-firmware \
    kernel-module-isp \
    "

BASECAMP_IMAGE_COMMON_INSTALL:append:zynqmp-common = " ${ZYNQMP_COMMON_INSTALL}"
BASECAMP_IMAGE_COMMON_INSTALL:append:zynqmp-mali-common = " ${ZYNQMP_MALI_COMMON_INSTALL}"
BASECAMP_IMAGE_COMMON_INSTALL:append:versal-common = " ${VERSAL_COMMON_INSTALL}"
BASECAMP_IMAGE_COMMON_INSTALL:append:versal2-common = " ${VERSAL2_COMMON_INSTALL}"
