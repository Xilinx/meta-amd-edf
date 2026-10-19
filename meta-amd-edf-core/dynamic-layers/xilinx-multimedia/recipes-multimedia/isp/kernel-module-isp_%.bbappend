# EDF kernel debug enables UBSAN, which embeds absolute source paths in the .ko.
# Keep buildpaths visible as a warning only for EDF debug builds, including the
# split kernel-module-visp* packages.
ERROR_QA:remove:amd-edf = "${@bb.utils.contains('ENABLE_KERNEL_DEBUG', '1', 'buildpaths', '', d)}"
WARN_QA:append:amd-edf = "${@bb.utils.contains('ENABLE_KERNEL_DEBUG', '1', ' buildpaths', '', d)}"
