FILESEXTRAPATHS:prepend:versal-vek280-sdt-seg-ospi := "${THISDIR}/u-boot-xlnx-scr:"
SRC_URI:append:versal-vek280-sdt-seg-ospi = " file://boot.cmd.sd.basecamp"

SDBOOTDEV = "0"

BOOTMODE:versal-vek280-sdt-seg-ospi = "sd"
BOOTFILE_EXT:versal-vek280-sdt-seg-ospi = ".basecamp"
