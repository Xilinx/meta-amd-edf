AMD_CORTEXA53_SC_INSTALL = " \
    packagegroup-syscontroller \
    packagegroup-scweb \
    packagegroup-systemcontroller-boards \
    lmsensors-config-sc-fancontrol \
    systemd-conf-sc \
    lmsensors-config-sc-libsensors \
    libubootenv-sc \
    "

AMD_CORTEXA53_INSTALL += " ${AMD_CORTEXA53_SC_INSTALL}"
