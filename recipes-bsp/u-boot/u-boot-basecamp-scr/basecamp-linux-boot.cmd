# for u-boot
devtype=mmc
devnum=0
bootpartnum=2

# for linux
kernelname=Image
rootpartnum=3
rootdev=mmcblk${devnum}p${rootpartnum}

echo "Checking for kernel:${kernelname}"
if test -e ${devtype} ${devnum}:${bootpartnum} ${kernelname}; then
	echo "Loading ${kernelname} at ${kernel_addr_r}"
	ext4load ${devtype} ${devnum}:${bootpartnum} ${kernel_addr_r} ${kernelname};
else
	echo "kernel image ${kernelname} not found on ${devtype} ${devnum}:${bootpartnum}"
	exit
fi

fdt addr ${fdtcontroladdr}
fdt get value bootargs /chosen bootargs
setenv bootargs $bootargs  root=/dev/${rootdev} ro rootwait uio_pdrv_genirq.of_id=generic-uio
booti ${kernel_addr_r} - ${fdtcontroladdr}
