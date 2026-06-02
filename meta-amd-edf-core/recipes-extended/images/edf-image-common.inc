DESCRIPTION = "Common packages for the AMD Embedded Development Framework distro and images"
LICENSE = "MIT"

# By default wic is not enable in IMAGE_FSTYPES so enable WIC image type support.
IMAGE_FSTYPES += "wic wic.xz wic.bmap"

#### Disable Busybox
SKIP_RECIPE[busybox] = "Busybox disabled"

# Install license files into the image by default
IMAGE_FEATURES .= "${@' lic-pkgs' if oe.data.typed_value('LICENSE_CREATE_PACKAGE', d) else ''}"

# Default we use the fpga-overlay to select dfx-mgr
AMD-EDF_FPGA_OVERLAY_INSTALL = "${@bb.utils.contains('MACHINE_FEATURES', 'fpga-overlay', 'dfx-mgr', '', d)}"

# Eliminate architectures where this is not supported
AMD-EDF_FPGA_OVERLAY_INSTALL:riscv32 = ""
AMD-EDF_FPGA_OVERLAY_INSTALL:riscv64 = ""

# Minimal set packages for EDF images
AMD-EDF_IMAGE_COMMON_INSTALL = "\
    ${AMD-EDF_FPGA_OVERLAY_INSTALL} \
    libgpiod \
    libgpiod-tools \
    devmem2 \
    kernel-modules \
    mtd-utils \
    ${@bb.utils.contains('COMBINED_FEATURES', 'efi', 'fwupd', '', d)} \
    i2c-tools \
    pciutils \
    bmaptool \
    usbutils \
    fpga-manager-script \
    git \
    "

WIFI_FIRMWARE_INSTALL = " \
    linux-firmware-ath9k \
    linux-firmware-bcm43143 \
    linux-firmware-bcm43242a \
    linux-firmware-bcm43569 \
    linux-firmware-bcm4373 \
    linux-firmware-carl9170 \
    linux-firmware-mt7601u \
    linux-firmware-mt7610 \
    linux-firmware-mt7662 \
    linux-firmware-mt7663 \
    linux-firmware-mt7921 \
    linux-firmware-mt7925 \
    linux-firmware-ralink \
    linux-firmware-rtl8188 \
    linux-firmware-rtl8192cu \
    linux-firmware-rtl8192eu \
    linux-firmware-rtl8723 \
    linux-firmware-rtl8821 \
    linux-firmware-rtl8822 \
    linux-firmware-rtw88 \
    linux-firmware-rtw89 \
    "

# Full set of packages for EDF images
AMD-EDF_IMAGE_FULL_INSTALL = " \
    tree \
    yavta \
    ${AMD-EDF_IMAGE_COMMON_INSTALL} \
    btop \
    can-utils \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'container-host-config', '', d)} \
    dosfstools \
    dtc \
    ethtool \
    hexedit \
    htop \
    iperf3 \
    libiio-tests \
    linuxptp \
    lmsensors-sensors \
    net-tools \
    netperf \
    phytool \
    python3-gpiod \
    python3-frugy \
    python3-pip \
    systemd-conf-edf \
    tcf-agent \
    ufs-utils \
    usb-modeswitch \
    usb-modeswitch-data \
    util-linux-taskset \
    vim \
    packagegroup-base-wifi \
    ${@bb.utils.contains('COMBINED_FEATURES', 'wifi', '${WIFI_FIRMWARE_INSTALL}', '', d)} \
    "

# COMMON is for anything in that family with no board specific items
# w/o COMMON is for _JUST_ that type
AMD_RISCV32_COMMON_INSTALL = "linux-xlnx-udev-rules"
AMD_RISCV32_INSTALL = ""

AMD_RISCV64_COMMON_INSTALL = "linux-xlnx-udev-rules"
AMD_RISCV64_INSTALL = ""

AMD_CORTEXA9_COMMON_INSTALL = " \
    linux-xlnx-udev-rules \
    packagegroup-core-sdk packagegroup-core-standalone-sdk-target \
    "

AMD_CORTEXA9_INSTALL = " \
    zc702-pl-bram-gpio-fw \
    zc706-pl-bram-gpio-fw \
"

AMD_CORTEXA53_COMMON_INSTALL = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'docker-moby-contrib', '', d)} \
    libdrm \
    libdrm-tests \
    linux-xlnx-udev-rules \
    packagegroup-xilinx-audio \
    packagegroup-xilinx-gstreamer \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'packagegroup-oci packagegroup-docker', '', d)} \
    packagegroup-opencv \
    systemtap \
    v4l-utils \
    xrt \
    packagegroup-core-sdk packagegroup-core-standalone-sdk-target \
    ${@bb.utils.contains('COMBINED_FEATURES', 'efi', 'fwupd-quirks', '', d)} \
    python3-image-mgmt \
    ${@bb.utils.contains('DISTRO_FEATURES', 'kvm', 'kernel-module-vhost', '', d)} \
    "

AMD_CORTEXA53_INSTALL = "\
    zcu111-pl-bram-gpio-fw \
    zcu208-pl-bram-gpio-fw \
    "

AMD_CORTEXA53_MALI_COMMON_INSTALL = "\
    ${AMD_CORTEXA53_COMMON_INSTALL} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'vcu', 'vcu-ctrlsw', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'libmali', ' libmali-xlnx', '', d)} \
    "

AMD_CORTEXA53_MALI_INSTALL = "\
    zcu102-pl-bram-gpio-fw \
    zcu104-pl-bram-gpio-fw \
    zcu106-pl-bram-gpio-fw \
    "

AMD_CORTEXA72_COMMON_INSTALL = "\
    ${AMD_CORTEXA53_COMMON_INSTALL} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'vdu', 'vdu-ctrlsw', '', d)} \
    vpk180-pl-bram-gpio-fw \
    "

AMD_CORTEXA72_INSTALL = "\
    vek280-pl-bram-gpio-fw \
    vck190-pl-bram-gpio-fw \
    vmk180-pl-bram-gpio-fw \
    vpk120-pl-bram-gpio-fw \
    vrk160-pl-bram-gpio-fw \
    vrk165-pl-bram-gpio-fw \
    "

AMD_CORTEXA78_COMMON_INSTALL = "\
    ${AMD_CORTEXA53_COMMON_INSTALL} \
    "

AMD_CORTEXA78_INSTALL = ""

AMD_CORTEXA78_MALI_COMMON_INSTALL = "\
    ${AMD_CORTEXA78_COMMON_INSTALL} \
    isp-media-server \
    isp-firmware \
    kernel-module-isp \
    ${@bb.utils.contains('MACHINE_FEATURES', 'vcu2', 'vcu2-ctrlsw', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'libmali', ' libmali-xlnx', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'optee', ' optee-client', '', d)} \
    "

AMD_CORTEXA78_MALI_INSTALL = "\
    vek385-pl-bram-gpio-fw \
    vek385-revb-pl-bram-gpio-fw \
    vek386-pl-bram-gpio-fw \
    "

# Enable weston for mali family variants
IMAGE_FEATURES += "${@ 'weston' \
    if ( bb.utils.contains('DISTRO_FEATURES', 'wayland', True, False, d) and \
       ( bb.utils.contains('MACHINE_FEATURES', 'mali400', True, False, d) or \
         bb.utils.contains('MACHINE_FEATURES', 'malig78ae', True, False, d))) \
    else '' }"

AMD-EDF_IMAGE_COMMON_INSTALL:append:amd-rv32imac-zicbom-zba-zbb-zbs-common = " ${AMD_RISCV32_COMMON_INSTALL} ${AMD_RISCV32_INSTALL}"
AMD-EDF_IMAGE_COMMON_INSTALL:append:amd-rv64imafdc-zicbom-zba-zbb-zbs-common = " ${AMD_RISCV64_COMMON_INSTALL} ${AMD_RISCV64_INSTALL}"
AMD-EDF_IMAGE_COMMON_INSTALL:append:amd-cortexa9thf-neon-common = " ${AMD_CORTEXA9_COMMON_INSTALL} ${AMD_CORTEXA9_INSTALL}"
AMD-EDF_IMAGE_COMMON_INSTALL:append:amd-cortexa53-common = " ${AMD_CORTEXA53_COMMON_INSTALL} ${AMD_CORTEXA53_INSTALL}"
AMD-EDF_IMAGE_COMMON_INSTALL:append:amd-cortexa53-mali-common = " ${AMD_CORTEXA53_MALI_COMMON_INSTALL} ${AMD_CORTEXA53_MALI_INSTALL}"
AMD-EDF_IMAGE_COMMON_INSTALL:append:amd-cortexa72-common = " ${AMD_CORTEXA72_COMMON_INSTALL} ${AMD_CORTEXA72_INSTALL}"
AMD-EDF_IMAGE_COMMON_INSTALL:append:amd-cortexa78-common = " ${AMD_CORTEXA78_COMMON_INSTALL} ${AMD_CORTEXA78_INSTALL}"
AMD-EDF_IMAGE_COMMON_INSTALL:append:amd-cortexa78-mali-common = " ${AMD_CORTEXA78_MALI_COMMON_INSTALL} ${AMD_CORTEXA78_MALI_INSTALL}"

# Platform-specific packages (Xen/OpenAMP) - used by edf-platform-disk-image
AMD_CORTEXA53_PLATFORM_INSTALL = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'openamp', 'packagegroup-openamp', '', d)} \
    "

AMD_CORTEXA72_OPENAMP_FW_EXAMPLES = "\
    vck190-openamp-fw-examples \
    vek280-openamp-fw-examples \
    vmk180-openamp-fw-examples \
    vpk120-openamp-fw-examples \
    vrk160-openamp-fw-examples \
    vrk165-openamp-fw-examples \
    versal-vm-p-m1369-00-reva-x-prc-01-openamp-fw-examples \
    "

AMD_CORTEXA72_PLATFORM_INSTALL = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen-zephyr-domu-image', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'openamp', '${AMD_CORTEXA72_OPENAMP_FW_EXAMPLES}', '', d)} \
    "

AMD_CORTEXA78_MALI_OPENAMP_FW_EXAMPLES = "\
    vek385-openamp-fw-examples \
    vek385-revb-openamp-fw-examples \
    "

AMD_CORTEXA78_PLATFORM_INSTALL = ""

AMD_CORTEXA78_MALI_PLATFORM_INSTALL = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen-zephyr-domu-image', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'openamp', '${AMD_CORTEXA78_MALI_OPENAMP_FW_EXAMPLES}', '', d)} \
    "

AMD-EDF_PLATFORM_INSTALL = ""
AMD-EDF_PLATFORM_INSTALL:amd-cortexa53-common = "${AMD_CORTEXA53_PLATFORM_INSTALL}"
AMD-EDF_PLATFORM_INSTALL:amd-cortexa53-mali-common = "${AMD_CORTEXA53_PLATFORM_INSTALL}"
AMD-EDF_PLATFORM_INSTALL:amd-cortexa72-common = "${AMD_CORTEXA53_PLATFORM_INSTALL} ${AMD_CORTEXA72_PLATFORM_INSTALL}"
AMD-EDF_PLATFORM_INSTALL:amd-cortexa78-common = "${AMD_CORTEXA53_PLATFORM_INSTALL} ${AMD_CORTEXA78_PLATFORM_INSTALL} "
AMD-EDF_PLATFORM_INSTALL:amd-cortexa78-mali-common = "${AMD_CORTEXA53_PLATFORM_INSTALL} ${AMD_CORTEXA78_MALI_PLATFORM_INSTALL}"
