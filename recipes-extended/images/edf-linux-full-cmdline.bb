SUMMARY = "EDF Linux full command line image"
DESCRIPTION = "Full command-line (no graphical session) reference \
image for the AMD Embedded Development Framework, including the \
standard EDF package set plus the on-target developer tooling."

require recipes-extended/images/core-image-full-cmdline.bb

# By default wic* and wic*.bmap are generated, for this target image recipe we
# only need cpio.gz.u-boot to fit into RAM.
IMAGE_FSTYPES = "cpio.gz.u-boot"

# Remove splash from the features as it is not needed for a command line image
# and adds a few more packages that are not needed.
IMAGE_FEATURES:remove = " splash"

# By default we use the fpga-overlay to select fpga-manager-script and dfx-mgr
# packages for FPGA overlay support.
# This is needed for AMD-EDF platforms that support FPGA overlay. For other
# platforms, this will not add any additional packages.
AMD-EDF_FPGA_OVERLAY_INSTALL = " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fpga-overlay', 'fpga-manager-script dfx-mgr', '', d)} \
    "

# Eliminate architectures where this is not supported
AMD-EDF_FPGA_OVERLAY_INSTALL:riscv32 = ""
AMD-EDF_FPGA_OVERLAY_INSTALL:riscv64 = ""

AMD-EDF_AARCH64_FULL_CMDLINE_COMMON_INSTALL ?= ""
AMD-EDF_AARCH64_FULL_CMDLINE_COMMON_INSTALL:append:aarch64 = "\
    dfu-util \
    ufs-utils \
    "

# Minimal set of packages for EDF Linux full command line image.
AMD-EDF_IMAGE_FULL_CMDLINE_COMMON_INSTALL = "\
    ${AMD-EDF_FPGA_OVERLAY_INSTALL} \
    bmaptool \
    can-utils \
    devmem2 \
    dhcpcd \
    dtc \
    hdparm \
    hexedit \
    i2c-tools \
    iperf3 \
    iozone3 \
    kernel-modules \
    libgpiod \
    libgpiod-tools \
    linuxptp \
    mtd-utils \
    net-tools \
    nvme-cli \
    pciutils \
    rsync \
    tcf-agent \
    tree \
    usbutils \
    "

IMAGE_INSTALL:append = " \
    ${AMD-EDF_IMAGE_FULL_CMDLINE_COMMON_INSTALL} \
    ${AMD-EDF_AARCH64_FULL_CMDLINE_COMMON_INSTALL} \
    "
