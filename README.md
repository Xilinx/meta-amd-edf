# meta-basecamp ⛺

This layer enables Basecamp development metadata for poky, oe, meta-xilinx and other layers. 🏔

## Maintainers, Mailing list, Patches

Please send any patches, pull requests, comments or questions for this layer to
the [meta-xilinx mailing list](https://lists.yoctoproject.org/g/meta-xilinx)
with ['meta-basecamp'] in the subject:

	meta-xilinx@lists.yoctoproject.org

When sending patches, please make sure the email subject line includes
`[meta-basecamp][<BRANCH_NAME>][PATCH]` and cc'ing the maintainers.

For more details follow the OE community patch submission guidelines, as described in:

https://www.openembedded.org/wiki/Commit_Patch_Message_Guidelines
https://www.openembedded.org/wiki/How_to_submit_a_patch_to_OpenEmbedded

`git send-email --to meta-xilinx@lists.yoctoproject.org *.patch`

> **Note:** When creating patches, please use below format. To follow best practice,
> if you have more than one patch use `--cover-letter` option while generating the
> patches. Edit the 0000-cover-letter.patch and change the title and top of the
> body as appropriate.

**Syntax:**
`git format-patch -s --subject-prefix="meta-basecamp][<BRANCH_NAME>][PATCH" -1`

**Example:**
`git format-patch -s --subject-prefix="meta-basecamp][langdale][PATCH" -1`

**Maintainers:**

	Mark Hatle <mark.hatle@amd.com>
	John Toomey <john.toomey@amd.com>
	Sandeep Gundlupet Raju <sandeep.gundlupet-raju@amd.com>
    Trevor Woerner <trevor.woerner@amd.com>
---

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
        https://github.com/Xilinx/meta-xilinx (development and amd xilinx release)
	layers: meta-xilinx-core, meta-xilinx-standalone, meta-xilinx-standalone-sdt,
	        meta-microblaze.
	branch: scarthgap or amd xilinx release version (e.g. rel-v2024.2)

	URI: https://git.yoctoproject.org/meta-security
	layers: meta-tpm
	branch: scarthgap

	URI:
        https://git.yoctoproject.org/meta-virtualization (official version)
        https://github.com/Xilinx/meta-virtualization (development and amd xilinx release)
	branch: scarthgap or amd xilinx release version (e.g. rel-v2024.2)

	URI:
        https://github.com/OpenAMP/meta-openamp (official version)
        https://github.com/Xilinx/meta-openamp (development and amd xilinx release)
	branch: scarthgap or amd xilinx release version (e.g. rel-v2024.2)

	URI:
        https://gitenterprise.xilinx.com/Yocto/meta-amd-adaptive-socs (development and amd xilinx release)
	layers: meta-amd-adaptive-socs-core, meta-amd-vek280-versal-sdt
	branch: scarthgap or amd xilinx release version (e.g. rel-v2024.2)
---
