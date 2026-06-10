# Enable VNC
PACKAGECONFIG:append:amd-edf = " vnc"

# Fix issue with VNC configuration
FILESEXTRAPATHS:prepend := "${THISDIR}/weston:"
SRC_URI += "file://weston-fix-aml-dependency.patch"
