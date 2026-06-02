SUMMARY = "Helper recipe that builds an RPM/DNF package repository \
(createrepo metadata) over the EDF SDK package feed."
DESCRIPTION = "Creates an RPM/DNF package repository (repodata, \
primary.xml.gz, etc.) over the AMD Embedded Development Framework \
(EDF) package feed so target devices and SDK installs can pull \
packages with dnf/rpm-ostree."
TOOLCHAIN_OUTPUTNAME ?= "${SDK_ARCH}-createrepo-nativesdk-standalone-${DISTRO_VERSION}"

# Clear this, we just want what we specify
SDK_CLASSES = ""

require recipes-core/meta/buildtools-tarball.bb

TOOLCHAIN_TARGET_TASK = ""
TOOLCHAIN_HOST_TASK = "nativesdk-sdk-provides-dummy meta-environment-${MACHINE} nativesdk-createrepo-c"

SDK_TITLE = "DNF Repository Indexing"

