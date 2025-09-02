require edf-image-common.inc

SUMMARY = "A full featured console image for AMD Embedded Development Framework"

IMAGE_FEATURES += "ssh-server-openssh package-management"

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    ${AMD-EDF_IMAGE_FULL_INSTALL} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'packagegroup-xen', '', d)} \
    "

inherit core-image

do_rootfs[prefuncs] += "edf_check_rootfs"
