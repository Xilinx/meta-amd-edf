SUMMARY = "EDF reference image that pulls in every available EDF \
packagegroup and demo for kitchen-sink validation builds."
DESCRIPTION = "AMD Embedded Development Framework image everything"
LICENSE = "MIT"

inherit core-image
require edf-image-common.inc

# We don't actually need to produce a specific image, we just want to run
# through all of the dependencies.
IMAGE_FSTYPES = ""

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
    "

# The following does not current work on risc-v due to RUST
IMAGE_FEATURES:append:arm = " \
    tools-testapps \
    "

IMAGE_FEATURES:append:aarch64 = " \
    tools-testapps \
    "

VITISAI_DEPENDENCIES = "googletest protobuf-c boost json-c libunwind"

AMD-EDF_IMAGE_FULL_INSTALL += " \
    packagegroup-base \
    packagegroup-core-boot \
    packagegroup-opencv \
    tcpdump \
    wireshark \
    packagegroup-networking-stack \
    python3-multiprocessing \
    python3-numpy \
    python3-shell \
    python3-threading \
    python3-threading \
    python3-pyserial \
    python3-h5py \
    util-linux \
    cpufrequtils \
    smartmontools \
    e2fsprogs \
    packagegroup-lmsensors \
    stress-ng \
    packagegroup-xilinx-benchmarks \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'packagegroup-xen', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'packagegroup-core-x11 xeyes xclock', '', d)} \
    bridge-utils \
    nfs-utils \
    nfs-utils-client \
    ${CORE_IMAGE_EXTRA_INSTALL} \
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
    kernel-devsrc \
    lopper \
    memtester \
    libiio \
    libiio-iiod \
    ${@bb.utils.contains('COMBINED_FEATURES', 'efi', 'efibootmgr', '', d)} \
    libubootenv \
    libubootenv-bin \
    mmc-utils \
    udev-extraconf \
    ser2net \
    picocom \
    "

AMD_RISCV32_FULL_INSTALL += " \
    "

AMD_RISCV64_FULL_INSTALL += " \
    "

AMD_CORTEXA9_FULL_INSTALL += " \
    valgrind \
    dmidecode \
    "

AMD_CORTEXA53_FULL_INSTALL += " \
    ${@bb.utils.contains('BBFILE_COLLECTIONS', 'jupyter-layer', bb.utils.contains('DISTRO_FEATURES', 'openamp', 'openamp-demo-notebooks', '', d), '', d)} \
    kernel-module-dp \
    kernel-module-hdmi \
    kernel-module-hdmi21 \
    ${VITISAI_DEPENDENCIES} \
    packagegroup-xilinx-qt \
    packagegroup-xilinx-ros \
    packagegroup-vitis-aiml \
    valgrind \
    ${@bb.utils.contains('BBFILE_COLLECTIONS', 'jupyter-layer', 'packagegroup-xilinx-jupyter', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'packagegroup-xilinx-multimedia', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'packagegroup-self-hosted', '', d)} \
    packagegroup-tsn \
    dmidecode \
    image-update \
    "

# TODO: Temporarily disable gstreamer for wrynose release integration
#    ${@bb.utils.contains('MACHINE_FEATURES', 'vcu', ' gstreamer-vcu-examples gstreamer-vcu-notebooks', '', d)}
AMD_CORTEXA53_MALI_FULL_INSTALL += " \
    ${AMD_CORTEXA53_FULL_INSTALL} \
    glmark2 \
    ${@bb.utils.contains('DISTRO_FEATURES', 'libmali', '', 'kmscube', d)} \
    packagegroup-amd-edf-gui \
    packagegroup-kria \
    image-update \
    "

# TODO: gstreamer1.0-omx is not available
# ${@bb.utils.contains('MACHINE_FEATURES', 'vdu', ' gstreamer-vdu-examples gstreamer-vdu-notebooks', '', d)}
AMD_CORTEXA72_FULL_INSTALL += " \
    ${@bb.utils.contains('BBFILE_COLLECTIONS', 'jupyter-layer', 'pm-notebooks', '', d)} \
    ${@bb.utils.contains('BBFILE_COLLECTIONS', 'jupyter-layer', bb.utils.contains('DISTRO_FEATURES', 'openamp', 'openamp-demo-notebooks', '', d), '', d)} \
    kernel-module-dp \
    kernel-module-hdmi \
    kernel-module-hdmi21 \
    ${VITISAI_DEPENDENCIES} \
    packagegroup-xilinx-qt \
    packagegroup-xilinx-ros \
    packagegroup-vitis-aiml \
    valgrind \
    ${@bb.utils.contains('BBFILE_COLLECTIONS', 'jupyter-layer', 'packagegroup-xilinx-jupyter', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'packagegroup-xilinx-multimedia', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'packagegroup-self-hosted', '', d)} \
    packagegroup-tsn \
    image-update \
    "

AMD_CORTEXA78_FULL_INSTALL += " \
    ${VITISAI_DEPENDENCIES} \
    packagegroup-xilinx-qt \
    packagegroup-xilinx-ros \
    packagegroup-vitis-aiml \
    valgrind \
    ${@bb.utils.contains('BBFILE_COLLECTIONS', 'jupyter-layer', 'packagegroup-xilinx-jupyter', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'packagegroup-xilinx-multimedia', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'packagegroup-self-hosted', '', d)} \
    packagegroup-tsn \
    "

AMD_CORTEXA78_MALI_FULL_INSTALL += " \
    ${AMD_CORTEXA78_FULL_INSTALL} \
    kernel-module-hdmi21 \
    ${@bb.utils.contains('MACHINE_FEATURES', 'optee', ' optee-os optee-examples optee-test', '', d)} \
    ${VITISAI_DEPENDENCIES} \
    glmark2 \
    kmscube \
    packagegroup-amd-edf-gui \
    "

AMD-EDF_IMAGE_FULL_INSTALL:append:amd-rv32imac-zicbom-zba-zbb-zbs-common = " ${AMD_RISCV32_FULL_INSTALL}"
AMD-EDF_IMAGE_FULL_INSTALL:append:amd-rv64imafdc-zicbom-zba-zbb-zbs-common = " ${AMD_RISCV64_FULL_INSTALL}"
AMD-EDF_IMAGE_FULL_INSTALL:append:amd-cortexa9thf-neon-common = " ${AMD_CORTEXA9_FULL_INSTALL}"
AMD-EDF_IMAGE_FULL_INSTALL:append:amd-cortexa53-common = " ${AMD_CORTEXA53_FULL_INSTALL}"
AMD-EDF_IMAGE_FULL_INSTALL:append:amd-cortexa53-mali-common = " ${AMD_CORTEXA53_MALI_FULL_INSTALL}"
AMD-EDF_IMAGE_FULL_INSTALL:append:amd-cortexa72-common = " ${AMD_CORTEXA72_FULL_INSTALL}"
AMD-EDF_IMAGE_FULL_INSTALL:append:amd-cortexa78-common = " ${AMD_CORTEXA78_FULL_INSTALL}"
AMD-EDF_IMAGE_FULL_INSTALL:append:amd-cortexa78-mali-common = " ${AMD_CORTEXA78_MALI_FULL_INSTALL}"

# Pull DPDK into the feed when meta-dpdk is present; xxv-pmd lands with its recipe.
AMD-EDF_IMAGE_FULL_INSTALL:append:aarch64 = " ${@bb.utils.contains('BBFILE_COLLECTIONS', 'dpdk', 'dpdk dpdk-examples dpdk-tools', '', d)}"

IMAGE_INSTALL = " ${AMD-EDF_IMAGE_COMMON_INSTALL} ${AMD-EDF_IMAGE_FULL_INSTALL} ${AMD-EDF_PLATFORM_INSTALL}"


IMAGE_LINGUAS = " "

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

# We want to download the ESW sources, but only if a particular version is enabled
DEPENDS:append = " ${@'embeddedsw-source-' + d.getVar('XILINX_RELEASE_VERSION').replace('v', '') if d.getVar('XILINX_RELEASE_VERSION') else ''}"

# Extra dependencies
EXTRA_DEPENDS = ""

EXTRA_DEPENDS:append:aarch64 = " \
    trusted-firmware-a:do_fetch \
"

EXTRA_DEPENDS:append:riscv64 = " \
    opensbi:do_fetch \
"

do_rootfs[depends] += "${EXTRA_DEPENDS}"

do_rootfs[prefuncs] += "edf_check_rootfs"
