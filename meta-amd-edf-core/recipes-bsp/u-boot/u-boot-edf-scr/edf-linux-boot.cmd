# Boot script for U-Boot - AMD Embedded Development Framework
# Works with any block device (MMC, UFS/SCSI, USB) where devtype and
# devnum are set by the distro boot framework before this script runs.
#
################

setenv kernel_name @@KERNEL_IMAGE@@
setenv rootpartnum @@ROOT_PARTNUM@@
setenv kernel_bootcmd @@KERNEL_BOOTCMD@@

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
${kernel_bootcmd} ${kernel_addr_r} - ${fdtcontroladdr}
