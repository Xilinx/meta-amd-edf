DESCRIPTION = "A full featured console image for Basecamp Xen"

require basecamp-image-common.inc

IMAGE_FEATURES += "ssh-server-openssh package-management"

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    ${BASECAMP_IMAGE_COMMON_INSTALL} \
    packagegroup-xen \    
    "

inherit core-image

SUPPORTED_MACHINES = "versal-common"

python() {
    machine = d.getVar("MACHINE") 
    supported = d.getVar("SUPPORTED_MACHINES").split()
    
    if machine not in supported:
        bb.warn("This image is not supported on %s, only machine(s) %s are supported." % (machine, supported))
}
