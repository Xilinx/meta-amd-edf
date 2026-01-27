# Workaround for weston 9, this is fixed in later versions.
do_install:append() {
    # Append '--continue-without-input' to weston launch command
    # defined in ExecStart of the systemd service, otherwise
    # weston doesn't start if no input devices like mouse or
    # keyboard are connected.
    sed -i '/^ExecStart/ s/$/ --continue-without-input/' \
        ${D}${systemd_system_unitdir}/weston.service
}
