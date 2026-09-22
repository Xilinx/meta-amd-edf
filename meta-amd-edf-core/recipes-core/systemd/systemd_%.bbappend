# In order to support root partition and file system expansion, we want
# to always enable repart and openssl. These items will not cause any
# issues on systems where no systemd-repart conf file is supplied or
# where the systemd-repart and systemd-growfs-root services are not
# enabled.
PACKAGECONFIG:append = " repart openssl"

# TEMPORARY: drop the OSC 3008 shell hook; its escape sequences are invisible
# in real terminals but pollute raw log capture (Vitis/ROAST).
PACKAGECONFIG:remove = " osc-context"

# Enable systemd-growfs-root.service by default.  Upstream ships the unit
# with no [Install] section because it is normally pulled in on demand by
# the x-systemd.growfs fstab option; enabling it directly runs it on every
# boot instead.
SYSTEMD_SERVICE:${PN}:append = " systemd-growfs-root.service"
SYSTEMD_PACKAGES += "${PN}"

# findmnt backs the ExecCondition below.  Without it the condition always
# fails and the root file system silently stops being grown.
RDEPENDS:${PN} += "util-linux-findmnt"

# The unit needs an [Install] section before it can be enabled.  The
# drop-in restricts it to a root on a block device: systemd-growfs exits
# ENODEV when / is a ramdisk, and an ExecCondition exiting 1-254 makes
# systemd skip the unit instead of failing it.
do_install:append() {
    echo "" >> ${D}${systemd_system_unitdir}/systemd-growfs-root.service
    echo "[Install]" >> ${D}${systemd_system_unitdir}/systemd-growfs-root.service
    echo "WantedBy=multi-user.target" >> ${D}${systemd_system_unitdir}/systemd-growfs-root.service

    install -d ${D}${systemd_system_unitdir}/systemd-growfs-root.service.d
    cat > ${D}${systemd_system_unitdir}/systemd-growfs-root.service.d/10-block-device-only.conf <<'EOF'
[Service]
ExecCondition=/bin/sh -c 'test -b "$(findmnt -no SOURCE /)"'
EOF
}
