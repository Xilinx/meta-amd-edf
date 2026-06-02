USERADD_PARAM:${PN}:append = ";--system --no-create-home --user-group --home-dir /var/lib/fwupd --shell /bin/nologin --comment \"Firmware update daemon\" fwupd-refresh"

# Mark as supported build to suppress "package has not been validated" warning
EXTRA_OEMESON += "-Dsupported_build=enabled"
