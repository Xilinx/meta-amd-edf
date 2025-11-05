USERADD_PARAM:${PN}:append = ";--system --no-create-home --user-group --home-dir /var/lib/fwupd --shell /bin/nologin --comment \"Firmware update daemon\" fwupd-refresh"
