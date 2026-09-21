SUMMARY = "Package for building a installable toolchain for AMD \
Embedded Development Framework SDK"
DESCRIPTION = "Meta-recipe that pulls in the toolchain components, \
sysroots and helper scripts needed to produce an installable \
application SDK tarball for the AMD Embedded Development Framework \
(EDF)."
LICENSE = "MIT"

PR = "r0"

# Buildable for any MACHINE; validated on EDF common bases only.
# Warn (when building) if none of these overrides are active.
AMD_EDF_APP_SDK_EXPECTED_MACHINE_OVERRIDES = "\
    amd-cortexa9thf-neon-common \
    amd-cortexa53-common \
    amd-cortexa53-mali-common \
    amd-cortexa72-common \
    amd-cortexa78-common \
    amd-cortexa78-mali-common \
"

python amd_edf_app_sdk_machinecheck() {
    expected = set((d.getVar('AMD_EDF_APP_SDK_EXPECTED_MACHINE_OVERRIDES') or "").split())

    if expected and expected.isdisjoint((d.getVar('OVERRIDES') or "").split(':')):
        bb.warn("meta-edf-app-sdk: current MACHINE '%s' is not based on a validated AMD common base (%s); SDK output is unvalidated and may be incorrect."
                % (d.getVar('MACHINE'), " ".join(sorted(expected))))
}

inherit populate_sdk amd-qemu-xilinx-sdk-tools amd-misc-sdk-tools

do_populate_sdk[prefuncs] += "amd_edf_app_sdk_machinecheck"

# add these items to the "cross" side of the SDK
TOOLCHAIN_TARGET_TASK:append = " \
    kernel-devsrc \
    "

TOOLCHAIN_TARGET_TASK:append:aarch64 = " \
    packagegroup-vitis-aiml-dev \
    packagegroup-opencv \
    packagegroup-xilinx-audio \
    packagegroup-xilinx-gstreamer \
    xrt-dev \
    libmetal \
    "

# add these items to the "native" side of the SDK
# i.e. these tools are built to run on the build host
TOOLCHAIN_HOST_TASK:append = " \
	nativesdk-packagegroup-edf-tools \
	"

TOOLCHAIN_HOST_TASK:append:aarch64 = " \
	nativesdk-packagegroup-vitis-aiml \
	"
