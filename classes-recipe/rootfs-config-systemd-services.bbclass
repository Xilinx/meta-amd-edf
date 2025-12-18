# Common console wiring for EDF rootfs images.

# These variables let individual images append to the enable/disable lists.
SERVICES_TO_ENABLE ?= ""

SERVICES_TO_DISABLE ?= ""

# Define a new image feature 'console-getty'

IMAGE_FEATURES[validitems] += "console-getty"
SERVICES_TO_ENABLE += '${@bb.utils.contains("IMAGE_FEATURES", "console-getty", "console-getty.service", "", d)}'

disable_systemd_services () {
    SERVICES_TO_DISABLE="${SERVICES_TO_DISABLE}"
    if [ -n "${SERVICES_TO_DISABLE}" ]; then
        echo "Disabling systemd services:"
        for service in ${SERVICES_TO_DISABLE}; do
            echo "    ${service}"
            systemctl --root="${IMAGE_ROOTFS}" mask "${service}" >/dev/null 2>&1 || true
        done
    fi
}

enable_systemd_services () {
    SERVICES_TO_ENABLE="${SERVICES_TO_ENABLE}"
    if [ -n "${SERVICES_TO_ENABLE}" ]; then
        echo "Enabling additional systemd services:"
        for service in ${SERVICES_TO_ENABLE}; do
            echo "    ${service}"
            systemctl --root="${IMAGE_ROOTFS}" enable "${service}" >/dev/null 2>&1 || true
        done
    fi
}

ROOTFS_POSTPROCESS_COMMAND += "disable_systemd_services; enable_systemd_services;"
