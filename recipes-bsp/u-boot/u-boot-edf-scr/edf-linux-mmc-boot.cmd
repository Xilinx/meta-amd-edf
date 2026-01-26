# This is a boot script for U-Boot with Linux env for MMC boot mode.
# Generate edf_linux_mmc_boot.scr:
# mkimage -c none -A arm -T script -d edf-linux-mmc-boot.cmd edf_linux_mmc_boot.scr
#
################

setenv kernelname Image

setenv rootpartnum 3

echo "Checking for kernel: /boot/${kernelname}"
if test -e ${devtype} ${devnum}:${rootpartnum} /boot/${kernelname}; then
       echo "Loading ${kernelname} at ${kernel_addr_r}"
       ext4load ${devtype} ${devnum}:${rootpartnum} ${kernel_addr_r} /boot/${kernelname};
else
       echo "kernel image /boot/${kernelname} not found on ${devtype} ${devnum}:${rootpartnum}"
       exit
fi

fdt addr ${fdtcontroladdr}
fdt get value bootargs /chosen bootargs
setenv bootargs ${bootargs} root=/dev/mmcblk${devnum}p${rootpartnum} ro rootwait
booti ${kernel_addr_r} - ${fdtcontroladdr}
