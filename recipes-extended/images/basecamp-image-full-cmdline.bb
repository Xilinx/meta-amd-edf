require basecamp-image-common.inc

SUMMARY = "A full featured console image for Basecamp"

IMAGE_FEATURES += "ssh-server-openssh package-management"

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    ${BASECAMP_IMAGE_FULL_INSTALL} \
    "

inherit core-image

SUPPORTED_MACHINES = " \
    amd-cortexa53-common \
    amd-cortexa53-mali-common \
    amd-cortexa72-common \
    amd-cortexa78-common \
    amd-cortexa78-mali-common \
    "

python() {
    machine = d.getVar("MACHINE") 
    supported = d.getVar("SUPPORTED_MACHINES").split()
    
    if machine not in supported:
        bb.warn("This image is not supported on %s, only machine(s) %s are supported." % (machine, supported))
}
