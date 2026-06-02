# meta-amd-edf ⛺

Collection of layers to enable AMD Embedded Development Framework (EDF). 🏔

* **meta-amd-edf-core**: layer containing the AMD Embedded Development Framework (EDF) metadata for poky, oe, meta-xilinx and other layers..

* **meta-amd-edf-bsp**: layer containing the AMD Embedded Development Framework (EDF) specific machines.

## Maintainers, Mailing list, Patches

Please send any patches, pull requests, comments or questions for this layer to
the [meta-xilinx mailing list](https://lists.yoctoproject.org/g/meta-xilinx)
with ['meta-amd-edf'] in the subject:

	meta-xilinx@lists.yoctoproject.org

When sending patches, please make sure the email subject line includes
`[meta-amd-edf][<BRANCH_NAME>][PATCH]` and cc'ing the maintainers.

For more details follow the Yocto Project community patch submission guidelines,
as described in:

https://docs.yoctoproject.org/dev/contributor-guide/submit-changes.html#

`git send-email --to meta-xilinx@lists.yoctoproject.org *.patch`

> **Note:** When creating patches, please use below format. To follow best practice,
> if you have more than one patch use `--cover-letter` option while generating the
> patches. Edit the 0000-cover-letter.patch and change the title and top of the
> body as appropriate.

**Syntax:**
`git format-patch -s --subject-prefix="meta-amd-edf][<BRANCH_NAME>][PATCH" -1`

**Example:**
`git format-patch -s --subject-prefix="meta-amd-edf][scarthgap][PATCH" -1`

**Maintainers:**

	Mark Hatle <mark.hatle@amd.com>
	John Toomey <john.toomey@amd.com>
	Sandeep Gundlupet Raju <sandeep.gundlupet-raju@amd.com>
	Trevor Woerner <trevor.woerner@amd.com>
