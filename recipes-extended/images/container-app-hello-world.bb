SUMMARY = "Reference container image that ships the GNU Hello sample \
application as an OCI/Docker container for AMD EDF targets."
DESCRIPTION = "OCI/Docker container image built on top of the EDF base \
container that ships the GNU Hello sample application as a minimal \
demonstration of the EDF container packaging flow."
require recipes-extended/images/container-app-base.bb

# Hello world example from meta-skeleton
CONTAINER_APP = " hello"

CONTAINER_APP_CMD = "hello"
