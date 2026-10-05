# meta-amd-edf-bsp ⛺

This layer enables AMD Embedded Development Framework (EDF) specific machines.

Yocto Project guidelines indicate machines and distro settings should be in
different layers.  The AMD EDF machines and related settings are captured in
this layer, while the regular meta-amd-edf layer does NOT contain any
machine specific items.

## Maintainers, Mailing list, Patches

See meta-amd-edf README.md file for details on sending patches, and maintainers.

## Dependencies

This layer depends on:

	URI: https://github.com/Xilinx/meta-amd-edf
	layers: meta-amd-edf-core
	branch: AMD release version (e.g. rel-v2026.2)

See meta-amd-edf README.md for additional dependencies.
