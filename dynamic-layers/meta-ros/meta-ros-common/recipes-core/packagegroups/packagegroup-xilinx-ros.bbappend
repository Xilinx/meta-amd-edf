# meta-basecamp doesn't enable meta-qt5 hence remove qt dependency packages from
# ros packagegroup.
RDEPENDS:${PN}:remove:aarch64 = "rqt-runtime-monitor"
