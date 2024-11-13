include ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'basecamp-virt.inc', '', d)}
