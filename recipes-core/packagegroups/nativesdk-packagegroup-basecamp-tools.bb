DESCRIPTION = "basecamp packages for native SDK"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup
inherit_defer nativesdk

PACKAGEGROUP_DISABLE_COMPLEMENTARY = "1"

BASECAMP_NATIVESDK_PACKAGES = " \
	bmaptool \
	dosfstools \
	e2fsprogs \
	e2fsprogs-resize2fs \
	gptfdisk \
	mtools \
	parted \
	util-linux \
	wic \
	"

RDEPENDS:${PN} = "${BASECAMP_NATIVESDK_PACKAGES}"
