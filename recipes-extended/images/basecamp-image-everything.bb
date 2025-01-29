DESCRIPTION = "Basecamp image everything"
LICENSE = "MIT"

inherit core-image
require basecamp-image-common.inc

IMAGE_FEATURES = " \
    ssh-server-openssh \
    hwcodecs \
    dev-pkgs \
    package-management \
    ptest-pkgs \
    splash \
    tools-sdk \
    tools-debug \
    tools-profile \
    tools-testapps \
    "

VITISAI_DEPENDENCIES = "opencv googletest protobuf-c boost json-c libunwind"

BASECAMP_IMAGE_FULL_INSTALL += " \
    packagegroup-base \
    packagegroup-core-boot \
    packagegroup-opencv \
    libdrm \
    libdrm-tests \
    tcpdump \
    wireshark \
    packagegroup-networking-stack \
    python3-pip\
    python3-multiprocessing \
    python3-numpy \
    python3-shell \
    python3-threading \
    python3-threading \
    python3-pyserial \
    python3-h5py \
    util-linux \
    cpufrequtils \
    usbutils \
    i2c-tools \
    smartmontools \
    e2fsprogs \
    v4l-utils \
    lmsensors-sensors \
    lmsensors-libsensors \
    lmsensors-sensorsdetect \
    packagegroup-xilinx-benchmarks \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'packagegroup-self-hosted', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'packagegroup-core-x11', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization vmsep', 'packagegroup-container', '', d)} \
    packagegroup-xilinx-audio \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'packagegroup-core-weston', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'packagegroup-xen', '', d)} \
    tcf-agent \
    mtd-utils \
    bridge-utils \
    can-utils \
    pciutils \
    kernel-modules \
    nfs-utils \
    nfs-utils-client \
    linux-xlnx-udev-rules \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    fpga-manager-script \
    htop \
    iperf3 \
    meson \
    u-boot-tools \
    ${@'libdfx' if 'xilinx-tools' in d.getVar('BBFILE_COLLECTIONS').split() else ''} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization vmsep', ' docker-compose', '', d)} \
    ltp \
    tree \
    ttf-bitstream-vera \
    packagegroup-core-full-cmdline \
    python3-pybind11 \
    python3-graphviz \
    bootgen \
    wolfssl \
    ${VITISAI_DEPENDENCIES} \
    kernel-devsrc \
    packagegroup-xilinx-gstreamer \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'packagegroup-xilinx-matchbox', '', d)} \
    xrt \
    lopper \
    packagegroup-xilinx-jupyter \
    packagegroup-xilinx-ros \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'packagegroup-xilinx-multimedia', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'vdu', ' gstreamer-vdu-examples gstreamer-vdu-notebooks', '', d)} \
    valgrind \
    packagegroup-xilinx-qt \
    packagegroup-vitis-aiml \
    "

VERSAL_COMMON_INSTALL += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'openamp', ' openamp-demo-notebooks', '', d)} \
    pm-notebooks \
    "

#    kernel-module-hdmi

BASECAMP_IMAGE_COMMON_INSTALL:append:versal-common = " ${VERSAL_COMMON_INSTALL}"

IMAGE_INSTALL = " ${BASECAMP_IMAGE_FULL_INSTALL}"

IMAGE_LINGUAS = " "

IMAGE_FSTYPES:remove = "cpio.gz cpio cpio.bz2"

SDK_RDEPENDS:append:task-populate-sdk-ext = " nativesdk-packagegroup-sdk-host packagegroup-cross-canadian-${MACHINE}"

DEPENDS:append = " \
    cpio-native \
    wic-tools \
    protobuf-native \
    libeigen-native \
    python3-setuptools-native \
    unfs3-native \
    libeigen \
"

SUPPORTED_MACHINES = " \
    zynqmp-common \
    zynqmp-mali-common \
    versal-common \
    versal2-common \
"

python() {
    machine = d.getVar("MACHINE")
    supported = d.getVar("SUPPORTED_MACHINES").split()

    if machine not in supported:
        bb.warn("This image is not supported on %s, only machine(s) %s are supported." % (machine, supported))
}


