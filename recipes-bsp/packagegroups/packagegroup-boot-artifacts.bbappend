# Add EDF boot artifacts
EDF_DEPLOY_DEPENDS = "${@'edf-${QEMU_FLASH_TYPE}' if d.getVar('QEMU_FLASH_TYPE') in [ 'qspi', 'ospi' ] else ''}"

# It is up to the BSP to add this to the DEPLOY_DEPENDS
