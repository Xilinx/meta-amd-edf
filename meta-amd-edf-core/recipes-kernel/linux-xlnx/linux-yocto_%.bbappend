FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Audit is required or systemd can fail to start the journal
KERNEL_FEATURES += "cgl/features/audit/audit.cfg"
