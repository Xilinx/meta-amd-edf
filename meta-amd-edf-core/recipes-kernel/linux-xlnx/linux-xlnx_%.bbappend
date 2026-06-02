FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

EDF_DISTRO_INCLUDE = ""
EDF_DISTRO_INCLUDE:amd-edf = "linux-xlnx-edf.inc"

require ${EDF_DISTRO_INCLUDE}
