DESCRIPTION = "Target Disk image for compositing basecamp target images rootfs and other binaries to a single wic image"
LICENSE ?= "MIT"
PACKAGES = ""

EXCLUDE_FROM_WORLD = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

INHIBIT_DEFAULT_DEPS = "1"

# Note this recipe is NOT compatible with populate_sdk!  For an SDK, use one of the regulr image recipes.

inherit image

# By default wic is not enable in IMAGE_FSTYPES so enable WIC image type support.
IMAGE_FSTYPES = "wic wic.xz wic.bmap wic.qemu-sd"

# Reset the IMGCLASSES
IMGCLASSES  = "rootfs_${IMAGE_PKGTYPE} image_types ${IMAGE_CLASSES}"
IMGCLASSES += "image_types_wic"

# Clear everything else
#TOOLCHAIN_TARGET_TASK = ""
#TOOLCHAIN_TARGET_TASK_ATTEMPTONLY = ""
#POPULATE_SDK_POST_TARGET_COMMAND = ""

BC_IMAGE_ROOTFS = "${DEPLOY_DIR_IMAGE}/basecamp-image-full-cmdline${IMAGE_MACHINE_SUFFIX}${IMAGE_NAME_SUFFIX}.tar.gz"
BC_IMAGE_ROOTFS_DIR = "${WORKDIR}/rootfs-basecamp-image-full-cmdline"
BC_XEN_IMAGE_ROOTFS = "${DEPLOY_DIR_IMAGE}/basecamp-xen-image-full-cmdline${IMAGE_MACHINE_SUFFIX}${IMAGE_NAME_SUFFIX}.tar.gz"
BC_XEN_IMAGE_ROOTFS_DIR = "${WORKDIR}/rootfs-basecamp-xen-image-full-cmdline"
BC_XEN_GUEST_ROOTFS = "${DEPLOY_DIR_IMAGE}/core-image-minimal${IMAGE_MACHINE_SUFFIX}${IMAGE_NAME_SUFFIX}.cpio.gz"

WICVARS:append = "\
    WORKDIR \
    BC_IMAGE_ROOTFS_DIR \
    BC_XEN_IMAGE_ROOTFS_DIR \
    "

DEPENDS += " \
    basecamp-image-full-cmdline \
    basecamp-xen-image-full-cmdline \
    core-image-minimal \
    "

WKS_FILES = "basecamp-disk-multi-rootfs.wks"

SUPPORTED_MACHINES = "versal-common"

python() {
    machine = d.getVar("MACHINE")
    supported = d.getVar("SUPPORTED_MACHINES").split()

    if machine not in supported:
        bb.warn("This image is not supported on %s, only machine(s) %s are supported." % (machine, supported))
}

do_rootfs[depends] += " \
    core-image-minimal:do_build \
    basecamp-image-full-cmdline:do_build \
    basecamp-xen-image-full-cmdline:do_build \
    "

fakeroot do_rootfs() {
    (
     mkdir -p ${BC_IMAGE_ROOTFS_DIR}
     cd ${BC_IMAGE_ROOTFS_DIR}
     tar xvpfSz ${BC_IMAGE_ROOTFS}
    )

    (
     mkdir -p ${BC_XEN_IMAGE_ROOTFS_DIR}
     cd ${BC_XEN_IMAGE_ROOTFS_DIR}
     tar xvpfSz ${BC_XEN_IMAGE_ROOTFS}
    )

    # Copy xen kernel images to /boot directory
    if [ -f ${BC_XEN_IMAGE_ROOTFS_DIR}/boot/xen ]; then
        install -D -m 0644 ${BC_XEN_IMAGE_ROOTFS_DIR}/boot/xen* ${BC_IMAGE_ROOTFS_DIR}/boot/
    else
        bbfatal "Copying Xen files failed from location: ${BC_XEN_IMAGE_ROOTFS_DIR}boot/"
    fi

    # Copy xen images to /root directory or xen partition
    if [ -f ${BC_XEN_GUEST_ROOTFS} ]; then
        cp -r ${BC_XEN_GUEST_ROOTFS} ${BC_XEN_IMAGE_ROOTFS_DIR}/root/rootfs.cpio.gz
    else
        bbfatal "Copying minimal rootfs for xen guest failed"
    fi
}

do_rootfs[cleandirs] += "${BC_IMAGE_ROOTFS_DIR} ${BC_XEN_IMAGE_ROOTFS_DIR}"
