include ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'edf-virt.inc', '', d)}
