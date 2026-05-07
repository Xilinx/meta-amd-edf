SUMMARY = "Container image with the EDF on-target developer tooling \
(compilers, debuggers, headers) for use on AMD adaptive SoC boards."
DESCRIPTION = "OCI/Docker container image that ships the AMD Embedded \
Development Framework on-target developer tooling - compilers, \
debuggers, headers, and helper scripts - so developers can rebuild and \
debug EDF user-space applications directly on the target board."
require recipes-extended/images/container-devtools-base.bb

IMAGE_INSTALL:append = " \
	ca-certificates \
	git \
"
