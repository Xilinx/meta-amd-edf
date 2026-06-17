FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Deadlock fix (not yet upstream): mount.sh runs as a udev RUN handler and must
# not call lsblk/blkid, which query the still-locked udev database. See
# mount.sh-replace-blkid-lsblk-with-udev-env-vars.patch.
SRC_URI += " \
    file://mount.sh-replace-blkid-lsblk-with-udev-env-vars.patch \
"

# Upstream defaults MOUNT_GROUP to 'disk', which grants broad block-device
# access. Use a less privileged group for FAT mounts. The oe-core recipe
# performs the @MOUNT_GROUP@ substitution in its own do_install.
MOUNT_GROUP = "users"

# The deadlock fix above removes the only lsblk call from mount.sh (the
# fstab-skip loop), so the systemd-gated util-linux-lsblk runtime dependency
# is now unused; drop it. util-linux-blkid is intentionally retained:
# although the get_label_name() /sbin/blkid call is also gone, upstream
# declares it unconditionally and the non-systemd mount path is not
# exercised here, so it is kept out of caution.
RDEPENDS:${PN}-automount:remove = "util-linux-lsblk"
