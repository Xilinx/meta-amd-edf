# for u-boot
devtype=mmc
devnum=0
bootpartnum=2

# for linux
kernelname=Image
rootpartnum=3
rootdev=mmcblk${devnum}p${rootpartnum}

# RAM locations
ramaddr_kernel=@@KERNEL_LOAD_ADDRESS@@
ramaddr_dtb=@@DEVICETREE_LOAD_ADDRESS@@

echo "Checking for kernel:${kernelname}"
if test -e ${devtype} ${devnum}:${bootpartnum} ${kernelname}; then
	echo "Loading ${kernelname} at ${ramaddr_kernel}"
	ext4load ${devtype} ${devnum}:${bootpartnum} ${ramaddr_kernel} ${kernelname};
else
	echo "kernel image ${kernelname} not found on ${devtype} ${devnum}:${bootpartnum}"
	exit
fi

fdt addr ${ramaddr_dtb}
fdt get value bootargs /chosen bootargs
setenv bootargs $bootargs  root=/dev/${rootdev} ro rootwait uio_pdrv_genirq.of_id=generic-uio
booti ${ramaddr_kernel} - ${ramaddr_dtb}
