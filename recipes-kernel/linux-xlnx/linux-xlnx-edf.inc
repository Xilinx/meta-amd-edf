FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "${@bb.utils.contains('COMBINED_FEATURES', 'usbgadget', \
    ' file://usb-gadgets-extra.cfg', '', d)}"
KERNEL_FEATURES += "${@bb.utils.contains('COMBINED_FEATURES', 'usbgadget', \
    ' features/usb/usb-gadgets.scc', '', d)}"
