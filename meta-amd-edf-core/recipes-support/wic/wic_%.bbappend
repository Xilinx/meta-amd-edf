# wic shells out to a range of host-side tools (parted, mkdosfs, mcopy,
# mkfs.*, mdir, sfdisk, e2fsck, resize2fs, ...) that it does not declare
# as dependencies. Wherever wic is installed as a package those tools may
# be absent, and its offline lookup falls back to the host PATH, so wic
# works only by chance depending on what the host has installed. Pull
# them in as RDEPENDS of the nativesdk variant only; the native and
# target variants are unaffected (and wic is not expected on a target).
#
# resize2fs is named explicitly because e2fsprogs splits it into its own
# e2fsprogs-resize2fs package.
#
# mkisofs (cdrtools) is omitted: cdrtools is a native-only recipe with no
# nativesdk variant, and is only needed for ISO images.
RDEPENDS:${PN}:append:class-nativesdk = " \
    nativesdk-parted \
    nativesdk-gptfdisk \
    nativesdk-dosfstools \
    nativesdk-mtools \
    nativesdk-e2fsprogs \
    nativesdk-e2fsprogs-resize2fs \
    nativesdk-util-linux \
    nativesdk-btrfs-tools \
    nativesdk-squashfs-tools \
    nativesdk-erofs-utils \
    nativesdk-tar \
    nativesdk-bmaptool \
    nativesdk-grub \
"

# syslinux (and its isohybrid helper, in the -misc package) only builds
# for x86 hosts.
RDEPENDS:${PN}:append:class-nativesdk = "${@' nativesdk-syslinux nativesdk-syslinux-misc' if d.getVar('SDK_ARCH') in ['x86_64', 'i686'] else ''}"
