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
    smartmontools \
    e2fsprogs \
    packagegroup-lmsensors \
    packagegroup-xilinx-benchmarks \
    packagegroup-self-hosted \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'packagegroup-self-hosted', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'packagegroup-core-x11', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'packagegroup-xilinx-multimedia', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'packagegroup-core-weston', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'packagegroup-xen', '', d)} \
    bridge-utils \
    nfs-utils \
    nfs-utils-client \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    fpga-manager-script \
    meson \
    u-boot-tools \
    u-boot-tools-xlnx \
    ${@'libdfx' if 'xilinx-tools' in d.getVar('BBFILE_COLLECTIONS').split() else ''} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization vmsep', ' packagegroup-container docker-compose', '', d)} \
    ltp \
    ttf-bitstream-vera \
    packagegroup-core-full-cmdline \
    python3-pybind11 \
    python3-graphviz \
    bootgen \
    wolfssl \
    ${VITISAI_DEPENDENCIES} \
    kernel-devsrc \
    lopper \
    packagegroup-xilinx-jupyter \
    packagegroup-xilinx-ros \
    packagegroup-tsn \
    valgrind \
    packagegroup-xilinx-qt \
    packagegroup-vitis-aiml \
    kernel-module-hdmi21 \
    "
#    kernel-module-hdmi

ZYNQMP_COMMON_INSTALL += " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'vcu', ' gstreamer-vcu-examples gstreamer-vcu-notebooks', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'openamp', ' openamp-demo-notebooks', '', d)} \
    kernel-module-dp \
    "

ZYNQMP_MALI_COMMON_INSTALL += " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'vcu', ' gstreamer-vcu-examples gstreamer-vcu-notebooks', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'openamp', ' openamp-demo-notebooks', '', d)} \
    kernel-module-dp \
    "

VERSAL_COMMON_INSTALL += " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'vdu', ' gstreamer-vdu-examples gstreamer-vdu-notebooks', '', d)} \
    pm-notebooks \
    ${@bb.utils.contains('DISTRO_FEATURES', 'openamp', ' openamp-demo-notebooks', '', d)} \
    kernel-module-dp \
    "

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


