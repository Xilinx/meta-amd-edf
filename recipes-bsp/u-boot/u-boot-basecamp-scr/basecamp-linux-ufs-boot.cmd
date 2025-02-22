# This is a boot script for U-Boot with Linux env for UFS boot mode.
# Generate basecamp_linux_ufs_boot.scr:
# mkimage -c none -A arm -T script -d basecamp-linux-boot.cmd basecamp_linux_ufs_boot.scr
#
################
kernel_name=@@KERNEL_IMAGETYPE@@

setenv devtype scsi
setenv devnum @@SCSI_BOOTDEV@@
setenv bootpartnum @@SCSI_BOOTPARTNUM@@

echo "Checking for kernel:${kernel_name}"
if test -e ${devtype} ${devnum}:${bootpartnum} ${kernel_name}; then
	echo "Loading ${kernel_name} at @@KERNEL_LOAD_ADDRESS@@"
	ext4load ${devtype} ${devnum}:${bootpartnum} @@KERNEL_LOAD_ADDRESS@@ ${kernel_name};
else
	echo "kernel image ${kernel_name} not found on ${devtype} ${devnum}:${bootpartnum}"
	exit
fi

fdt addr $fdtcontroladdr
fdt get value bootargs /chosen bootargs
setenv bootargs $bootargs @@KERNEL_ROOT_SCSI@@
booti @@KERNEL_LOAD_ADDRESS@@ - $fdtcontroladdr
