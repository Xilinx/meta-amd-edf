FILESEXTRAPATHS:prepend:xlnx-versal-vek280-revb := "${THISDIR}/u-boot-xlnx-scr:"
SRC_URI:append:xlnx-versal-vek280-revb = " file://boot.cmd.sd.basecamp"

BOOTMODE:xlnx-versal-vek280-revb = "sd"
BOOTFILE_EXT:xlnx-versal-vek280-revb = ".basecamp"
