require recipes-extended/images/container-app-base.bb

# Hello world example from meta-skeleton
IMAGE_INSTALL:append = " hello"

OCI_IMAGE_ENTRYPOINT = "hello"
