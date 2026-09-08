# AMD EDF - carry the upstream --no-cma option, which lets a kexec load opt
# out of having its payload placed in CMA.
#
# Deliberately pinned to 2.0.32 rather than '%'. The upstream commit landed
# after the 2.0.32 tag and is unreleased, so 2.0.32 is the only release that
# needs it and every later one already carries it. Delete this bbappend and
# its patch when oe-core moves past 2.0.32.

FILESEXTRAPATHS:prepend:amd-edf := "${THISDIR}/files:"

SRC_URI:append:amd-edf = " file://0001-kexec-Add-support-for-disabling-usage-of-CMA.patch"
