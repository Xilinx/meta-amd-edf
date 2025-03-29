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
    kernel-modules \
    mtd-utils \
    fwupd \
    i2c-tools \
    pciutils \
    bmaptool \
    usbutils \
    fpga-manager-script \
    "

# Full set of packages for Basecamp images
BASECAMP_IMAGE_FULL_INSTALL = " \
    tree \
    libdrm \
    libdrm-tests \
    packagegroup-xilinx-audio \
    packagegroup-xilinx-gstreamer \
    v4l-utils \
    yavta \
    ${BASECAMP_IMAGE_COMMON_INSTALL} \
    can-utils \
    container-host-config \
    docker-moby-contrib \
    dosfstools \
    dtc \
    ethtool \
    hexedit \
    htop \
    iperf3 \
    linux-xlnx-udev-rules \
    linuxptp \
    net-tools \
    packagegroup-docker \
    packagegroup-oci \
    phytool \
    systemtap \
    tcf-agent \
    ufs-utils \
    util-linux-taskset \
    vim \
    xrt \
    "

ZYNQMP_COMMON_INSTALL = "\
    zcu111-pl-bram-gpio-fw \
    "

ZYNQMP_MALI_COMMON_INSTALL = "\
    vcu-ctrlsw \
    ${@bb.utils.contains('DISTRO_FEATURES', 'libmali', ' libmali-xlnx', '', d)} \
    "

VERSAL_COMMON_INSTALL = "\
    vdu-ctrlsw \
    "

VERSAL2_COMMON_INSTALL = "\
    isp-firmware \
    kernel-module-isp \
    vcu2-ctrlsw \
    ${@bb.utils.contains('DISTRO_FEATURES', 'libmali', ' libmali-xlnx', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', ' optee-client', '', d)} \
    "

BASECAMP_IMAGE_COMMON_INSTALL:append:zynqmp-common = " ${ZYNQMP_COMMON_INSTALL}"
BASECAMP_IMAGE_COMMON_INSTALL:append:zynqmp-mali-common = " ${ZYNQMP_MALI_COMMON_INSTALL}"
BASECAMP_IMAGE_COMMON_INSTALL:append:versal-common = " ${VERSAL_COMMON_INSTALL}"
BASECAMP_IMAGE_COMMON_INSTALL:append:versal2-common = " ${VERSAL2_COMMON_INSTALL}"
