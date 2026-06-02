# meta-amd-edf-core

This layer enables AMD Embedded Development Framework (EDF) metadata for poky, 
oe, meta-xilinx and other layers.

## Dependencies

This layer depends on:

	URI: https://git.yoctoproject.org/poky
	layers: meta, meta-poky
	branch: scarthgap

	URI: https://git.openembedded.org/meta-openembedded
	layers: meta-oe, meta-python, meta-filesystems, meta-networking.
	branch: scarthgap

	URI:
        https://git.yoctoproject.org/meta-xilinx (official version)
        https://github.com/Xilinx/meta-xilinx (development and AMD release)
	layers: meta-xilinx-core, meta-xilinx-standalone, meta-xilinx-standalone-sdt,
	        meta-microblaze.
	branch: scarthgap or AMD release version (e.g. rel-v2026.1)

	URI: https://git.yoctoproject.org/meta-security
	layers: meta-tpm
	branch: scarthgap

	URI:
        https://git.yoctoproject.org/meta-virtualization (official version)
        https://github.com/Xilinx/meta-virtualization (development and AMD release)
	branch: scarthgap or AMD release version (e.g. rel-v2026.1)

	URI:
        https://github.com/OpenAMP/meta-openamp (official version)
        https://github.com/Xilinx/meta-openamp (development and AMD release)
	branch: scarthgap or AMD release version (e.g. rel-v2026.1)

	URI:
        https://github.com/Xilinx/meta-amd-adaptive-socs (official, development and AMD release)
	layers: meta-amd-adaptive-socs-core, meta-amd-adaptive-socs-bsp
	branch: scarthgap or AMD release version (e.g. rel-v2026.1)

	URI: https://git.yoctoproject.org/meta-arm
	layers: meta-arm, meta-arm-toolchain
	branch: scarthgap

Optional dependencies (for Kria support):

	URI: https://github.com/Xilinx/meta-kria
	branch: AMD release version (e.g. rel-v2026.1)
