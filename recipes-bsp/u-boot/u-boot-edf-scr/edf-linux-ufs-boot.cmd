# This is a boot script for U-Boot with Linux env for UFS boot mode.
#
################
setenv kernel_name Image
setenv bootpartnum 1
setenv rootpartnum 3

# UFS Boot
setenv devtype scsi
setenv devnum 0

echo "Checking for kernel:${kernel_name}"
if test -e ${devtype} ${devnum}:${bootpartnum} ${kernel_name}; then
	echo "Loading ${kernel_name} at ${kernel_addr_r}"
	fatload ${devtype} ${devnum}:${bootpartnum} ${kernel_addr_r} ${kernel_name};
else
	echo "kernel image ${kernel_name} not found on ${devtype} ${devnum}:${bootpartnum}"
	exit
fi

fdt addr ${fdtcontroladdr}
fdt get value bootargs /chosen bootargs
setenv bootargs ${bootargs} root=/dev/sda${rootpartnum} ro rootwait uio_pdrv_genirq.of_id=generic-uio
bootefi ${kernel_addr_r} - ${fdtcontroladdr}
booti ${kernel_addr_r} - ${fdtcontroladdr}
