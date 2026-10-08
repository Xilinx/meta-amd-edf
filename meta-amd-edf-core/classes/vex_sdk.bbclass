#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# This class augments the vex.bbclass in openembedded-core.
# It adds the ability for an SDK to generate a vex file for later
# processing with the sbom-cve-check utility.
#
# We could adding INHERIT += "vex" ourselves, but our goal is to
# augment the existing class, not replace it.  Instead we detect
# and warn the user their configuration is wrong.

CVE_CHECK_SDK_MANIFEST_JSON ?= "${SDKDEPLOYDIR}/${TOOLCHAIN_OUTPUTNAME}.vex.json"

# Based closely on the vex.bbclass vex_write_rootfs_manifest function
def vex_write_sdk_manifest(d, target_type):
    """
    Create VEX/CVE manifest when building an sdk
    """

    import json
    from oe.sdk import sdk_list_installed_packages
    from oe.cve_check import cve_check_merge_jsons

    inheritlist = d.getVar("INHERIT").split()
    if "vex" not in inheritlist:
        bb.warn("vex_sdk.bbclass requires INHERIT to include vex")
        return

    # Create a list of relevant recipies
    recipies = set()

    if target_type == "target":
        pkgdata_dir = d.getVar('PKGDATA_DIR')
    else:
        pkgdata_dir = d.getVar('PKGDATA_DIR_SDK')

    for pkg in list(sdk_list_installed_packages(d, target_type == "target")):
        pkg_info = os.path.join(pkgdata_dir,
                                'runtime-reverse', pkg)
        pkg_data = oe.packagedata.read_pkgdatafile(pkg_info)
        recipies.add(pkg_data["PN"])

    build_dir = d.getVar("B")
    json_name = d.getVar("TOOLCHAIN_OUTPUTNAME") + '-' + target_type + ".vex.json"

    sdk_name = os.path.join(build_dir, json_name)

    bb.note("Writing sdk %s VEX manifest: %s" % (target_type, sdk_name))

    json_data = {"version":"1", "package": []}
    text_data = ""

    save_pn = d.getVar("PN")

    for pkg in recipies:
        # To be able to use the CVE_CHECK_RECIPE_FILE_JSON variable we have to evaluate
        # it with the different PN names set each time.
        d.setVar("PN", pkg)

        pkgfilepath = d.getVar("CVE_CHECK_RECIPE_FILE_JSON")
        if os.path.exists(pkgfilepath):
            with open(pkgfilepath) as j:
                data = json.load(j)
                cve_check_merge_jsons(json_data, data)
        else:
            bb.warn("Missing cve file for %s" % pkg)

    d.setVar("PN", save_pn)

    with open(sdk_name, "w") as f:
        json.dump(json_data, f, indent=2)

python vex_write_sdk_manifest_host () {
    vex_write_sdk_manifest(d, "host")
}

python vex_write_sdk_manifest_target () {
    vex_write_sdk_manifest(d, "target")
}

POPULATE_SDK_POST_HOST_COMMAND:append:task-populate-sdk = " vex_write_sdk_manifest_host"
POPULATE_SDK_POST_TARGET_COMMAND:append:task-populate-sdk = " vex_write_sdk_manifest_target"

# Merge the target and host vex files for a single SDK file
python sdk_create_vex() {
    import json

    inheritlist = d.getVar("INHERIT").split()
    if "vex" not in inheritlist:
        bb.warn("vex_sdk.bbclass requires INHERIT to include vex")
        return

    build_dir = d.getVar("B")
    target_vex_name = d.getVar("TOOLCHAIN_OUTPUTNAME") + '-target' + ".vex.json"
    host_vex_name = d.getVar("TOOLCHAIN_OUTPUTNAME") + '-host' + ".vex.json"

    target_vex_fname = os.path.join(build_dir, target_vex_name)
    host_vex_fname = os.path.join(build_dir, host_vex_name)

    json_data = {"version":"1", "package": []}

    # Merge the files together
    for json_file in [ target_vex_fname, host_vex_fname ]:
        with open(json_file) as j:
            data = json.load(j)
            json_data["package"].extend(data["package"])

    manifest_name = d.getVar("CVE_CHECK_SDK_MANIFEST_JSON")
    bb.plain("SDK VEX JSON report stored in: %s" % manifest_name)

    with open(manifest_name, "w") as f:
        json.dump(json_data, f, indent=2)
}

do_populate_sdk[postfuncs] += "sdk_create_vex"
