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

# FIXME removed: hellopm, pm-notebooks, xrt (requires ocl-icd)
#${@bb.utils.contains('MACHINE_FEATURES', 'vdu', ' gstreamer-vdu-examples gstreamer-vdu-notebooks', '', d)}
IMAGE_INSTALL:append = " \
    packagegroup-core-boot \
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
    kernel-module-hdmi \
    ${@bb.utils.contains('DISTRO_FEATURES', 'openamp', ' openamp-demo-notebooks', '', d)} \
    "

#FIXME some of the following packagegroups will need to be readded when available:
#petalinux-base 
#petalinux-opencv 
#petalinux-display-debug 
#petalinux-networking-debug 
#petalinux-networking-stack 
#petalinux-python-modules 
#petalinux-utils 
#petalinux-v4lutils 
#petalinux-lmsensors 
#petalinux-benchmarks 
#petalinux-jupyter 
#${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'petalinux-self-hosted', '', d)} 
#${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'petalinux-x11', '', d)} 
#${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'petalinux-matchbox', '', d)} 
#petalinux-qt 
#petalinux-qt-extended 
#${@bb.utils.contains('DISTRO_FEATURES', 'virtualization vmsep', 'petalinux-ocicontainers', '', d)} 
##${@bb.utils.contains('DISTRO_FEATURES', 'openamp', 'petalinux-openamp', '', d)}
#petalinux-gstreamer 
#petalinux-audio 
#petalinux-mraa 
#${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'petalinux-multimedia', '', d)} 
#${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'petalinux-weston', '', d)} 
#${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'petalinux-xen', '', d)} 
#aws-greengrass-v2 
#petalinux-ros 

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
