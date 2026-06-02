FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://mount.sh-replace-blkid-lsblk-with-udev-env-vars.patch \
    file://mount.sh-allow-fat-mount-group.patch \
"

MOUNT_GROUP = "users"

do_install:append() {
    sed -i 's|@MOUNT_GROUP@|${MOUNT_GROUP}|g' ${D}${sysconfdir}/udev/scripts/mount.sh
}
