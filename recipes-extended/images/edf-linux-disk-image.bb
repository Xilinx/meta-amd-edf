DESCRIPTION = "AMD Embedded Development Framework Linux image compositing rootfs and other binaries to a single wic image"

LICENSE ?= "MIT"
PACKAGES = ""

EXCLUDE_FROM_WORLD = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

INHIBIT_DEFAULT_DEPS = "1"

# Clear IMAGE_FEATURES, as they are implemented by the individual partition
# images and may require the core-image or other image classes
IMAGE_FEATURES = ""

# Note this recipe is NOT compatible with populate_sdk!  For an SDK, use one of the regulr image recipes.

inherit image

# By default wic is not enable in IMAGE_FSTYPES so enable WIC image type support.
IMAGE_FSTYPES = "wic wic.xz wic.bmap${@' wic.qemu-sd' if bb.data.inherits_class('image-types-xilinx-qemu', d) else ''}"

IMAGE_FSTYPES:append:versal-2ve-2vm = " wic.ufs wic.ufs.xz wic.ufs.bmap"

# Reset the IMGCLASSES
IMGCLASSES  = "rootfs_${IMAGE_PKGTYPE} image_types ${IMAGE_CLASSES}"
IMGCLASSES += "image_types_wic image-types-xilinx-qemu"

IMGCLASSES:append:versal-2ve-2vm = " image_types_ufs"

# Clear everything else
#TOOLCHAIN_TARGET_TASK = ""
#TOOLCHAIN_TARGET_TASK_ATTEMPTONLY = ""
#POPULATE_SDK_POST_TARGET_COMMAND = ""

ROOTFS_PART_TYPE = "B921B045-1DF0-41C3-AF44-4C6F280D3FAE"

# ESP partition UUID is fixed - uboot will not detect the ESP partition if this is not set
ESP_PART_TYPE = "C12A7328-F81F-11D2-BA4B-00A0C93EC93B"

IMAGE_EFI_BOOT_FILES ?= ""
IMAGE_EFI_BOOT_FILES:versal ?= " \
    loader/loader.conf;loader/loader.conf \
    loader/edf-linux.conf;loader/entries/edf-linux.conf \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', "xen.cfg xen.efi loader/edf-xen.conf;loader/entries/edf-xen.conf", '', d)} \
    "
IMAGE_EFI_BOOT_FILES:versal-2ve-2vm ?= " \
    loader/loader.conf;loader/loader.conf \
    loader/edf-linux.conf;loader/entries/edf-linux.conf \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', "xen.cfg xen.efi loader/edf-xen.conf;loader/entries/edf-xen.conf", '', d)} \
    "

EDF_IMAGE_ROOTFS = "${DEPLOY_DIR_IMAGE}/edf-image-full-cmdline${IMAGE_MACHINE_SUFFIX}${IMAGE_NAME_SUFFIX}.tar.gz"
EDF_IMAGE_ROOTFS_DIR = "${WORKDIR}/rootfs-edf-image-full-cmdline"

# Generate a UUID for the rootfs and pass it to wic - we need to do it here because it also needs to
# be passed to the wic plugin which is installing the ESP config files for systemd-boot and Xen
do_rootfs_wicenv:prepend:versal () {
    import uuid
    d.setVar("ROOTFS_PART_UUID", str(uuid.uuid4()))
}
do_rootfs_wicufsenv:prepend:versal-2ve-2vm () {
    import uuid
    d.setVar("ROOTFS_PART_UUID", str(uuid.uuid4()))
}

WICVARS:append = "\
    WORKDIR \
    EDF_IMAGE_ROOTFS_DIR \
    ROOTFS_PART_UUID \
    ROOTFS_PART_TYPE \
    ESP_PART_TYPE \
    EFI_PROVIDER \
    "

WICUFSVARS:append = "\
    WORKDIR \
    EDF_IMAGE_ROOTFS_DIR \
    ROOTFS_PART_UUID \
    ROOTFS_PART_TYPE \
    ESP_PART_TYPE \
    EFI_PROVIDER \
    "

DEPENDS += " \
    edf-image-full-cmdline \
    "

WKS_FILES = "edf-disk-single-rootfs.wks"
WKS_FILES:versal = "edf-disk-single-rootfs-efi.wks"
WKS_FILES:versal-net = "edf-disk-single-rootfs-efi.wks"
WKS_FILES:versal-2ve-2vm = "edf-disk-single-rootfs-efi.wks"

# Configure QEMU boot
QB_KERNEL_ROOT:riscv32 = "/dev/vda3"
QB_KERNEL_ROOT:riscv64 = "/dev/vda3"
QB_KERNEL_ROOT:zynq = "/dev/mmcblk0p3"

do_rootfs[depends] += " \
    edf-image-full-cmdline:do_build \
    "

ADDN_IMAGE_RDEPENDS = ""
ADDN_IMAGE_RDEPENDS:versal = "virtual-systemd-bootconf:do_deploy"
ADDN_IMAGE_RDEPENDS:versal-net = "virtual-systemd-bootconf:do_deploy"
ADDN_IMAGE_RDEPENDS:versal-2ve-2vm = "virtual-systemd-bootconf:do_deploy"

do_rootfs[rdepends] += "${ADDN_IMAGE_RDEPENDS}"

do_rootfs[prefuncs] += "edf_check_rootfs"
fakeroot do_rootfs() {
    (
     mkdir -p ${EDF_IMAGE_ROOTFS_DIR}
     cd ${EDF_IMAGE_ROOTFS_DIR}
     tar xvpfSz ${EDF_IMAGE_ROOTFS}
    )
}

do_rootfs[cleandirs] += "${EDF_IMAGE_ROOTFS_DIR}"
