# This is a boot script for U-Boot with Linux env for UFS boot mode.
# Generate edf_linux_ufs_boot.scr:
# mkimage -c none -A arm -T script -d edf-linux-ufs-boot.cmd edf_linux_ufs_boot.scr
#
################
setenv kernel_name Image
setenv rootpartnum 3

# UFS Boot
setenv devtype scsi
setenv devnum 0

echo "Checking for kernel: /boot/${kernel_name}"
if test -e ${devtype} ${devnum}:${rootpartnum} /boot/${kernel_name}; then
	echo "Loading ${kernel_name} at ${kernel_addr_r}"
	ext4load ${devtype} ${devnum}:${rootpartnum} ${kernel_addr_r} /boot/${kernel_name};
else
	echo "kernel image /boot/${kernel_name} not found on ${devtype} ${devnum}:${rootpartnum}"
	exit
fi

part uuid ${devtype} ${devnum}:${rootpartnum} distro_rootpart_uuid
if test -z "${distro_rootpart_uuid}"; then
	echo "Failed to get PARTUUID for ${devtype} ${devnum}:${rootpartnum}"
	exit
fi

fdt addr ${fdtcontroladdr}
fdt get value bootargs /chosen bootargs
setenv bootargs ${bootargs} root=PARTUUID=${distro_rootpart_uuid} ro rootwait
booti ${kernel_addr_r} - ${fdtcontroladdr}
