# This is a boot script for U-Boot with Xen Dom0less parameters for SD boot mode.
# Generate xen_boot.scr:
# mkimage -c none -A arm -T script -d xen-dom0less-boot.cmd xen_dom0less_boot.scr
#
################
xen_name=@@XEN_IMAGETYPE@@
kernel_name=@@KERNEL_IMAGETYPE@@
rootfs_name=@@RAMDISK_IMAGETYPE@@
domU0_kernel_name=@@XEN_DOMU0_KERNEL_IMAGETYPE@@
domU0_rootfs_name=@@XEN_DOMU0_RAMDISK_IMAGETYPE@@

setenv devtype mmc
setenv devnum @@SDBOOTDEV@@
setenv bootdev mmcblk${devnum}p
setenv bootpart @@BOOTPARTNUM@@
setenv rootpart @@ROOTPARTNUM@@

echo "Checking for xen:${xen_name}"
if test -e ${devtype} ${devnum}:${bootpart} ${xen_name}; then
	echo "Loading ${xen_name} at @@XEN_LOAD_ADDRESS@@";
	ext4load ${devtype} ${devnum}:${bootpart} @@XEN_LOAD_ADDRESS@@ ${xen_name};
fi

echo "Checking for Image:${kernel_name}"
if test -e ${devtype} ${devnum}:${bootpart} ${kernel_name}; then
	echo "Loading ${kernel_name} at @@XEN_KERNEL_LOAD_ADDRESS@@";
	ext4load ${devtype} ${devnum}:${bootpart} @@XEN_KERNEL_LOAD_ADDRESS@@ ${kernel_name};
	setenv kernel_size 0x$filesize;
fi

echo "Checking for Dom0 ramdisk:${rootfs_name}"
if test -e ${devtype} ${devnum}:${bootpart} ${rootfs_name} && test "${skip_ramdisk}" != "yes"; then
	echo "Loading Dom0 ${rootfs_name} at @@XEN_RAMDISK_LOAD_ADDRESS@@";
	ext4load ${devtype} ${devnum}:${bootpart} @@XEN_RAMDISK_LOAD_ADDRESS@@ ${rootfs_name};
	setenv ramdisk_size 0x$filesize;
fi

echo "Checking for DomU0 Image:${domU0_kernel_name}"
if test -e ${devtype} ${devnum}:${bootpart} ${domU0_kernel_name}; then
	echo "Loading DomU0 ${domU0_kernel_name} at @@XEN_DOMU0_KERNEL_LOAD_ADDRESS@@";
	ext4load ${devtype} ${devnum}:${bootpart} @@XEN_DOMU0_KERNEL_LOAD_ADDRESS@@ ${domU0_kernel_name};
	setenv domu0_kernel_size 0x$filesize;
fi

echo "Checking for DomU0 ramdisk:${domU0_rootfs_name}"
if test -e ${devtype} ${devnum}:${rootpart} root/${domU0_rootfs_name} && test "${skip_ramdisk}" != "yes"; then
	echo "Loading DomU0 ${domU0_rootfs_name} at @@XEN_DOMU0_RAMDISK_LOAD_ADDRESS@@";
	ext4load ${devtype} ${devnum}:${rootpart} @@XEN_DOMU0_RAMDISK_LOAD_ADDRESS@@ root/${domU0_rootfs_name};
	setenv domU0_ramdisk_size 0x$filesize;
fi

fdt addr $fdtcontroladdr
fdt resize 0x1000
fdt get value bootargs /chosen bootargs
fdt set /chosen \#address-cells <0x2>
fdt set /chosen \#size-cells <0x2>
setenv xen_bootargs "console=dtuart dtuart=@@XEN_SERIAL_CONSOLES@@ dom0_mem=@@DOM0_MEM@@ dom0_max_vcpus=@@DOM0_MAX_VCPUS@@ bootscrub=0 vwfi=native @@XEN_CMDLINE_APPEND@@"
fdt set /chosen xen,xen-bootargs \"${xen_bootargs}\"

# Check dom0 env $ramdisk_size is 0 or not.
if test -n ${ramdisk_size}; then
	fdt mknod /chosen dom0-ramdisk@@@XEN_DOM0_RAMDISK_NODE_UADDR@@
	fdt set /chosen/dom0-ramdisk@@@XEN_DOM0_RAMDISK_NODE_UADDR@@ compatible  "xen,linux-initrd" "xen,multiboot-module" "multiboot,module"
	fdt set /chosen/dom0-ramdisk@@@XEN_DOM0_RAMDISK_NODE_UADDR@@ reg <0x0 @@XEN_RAMDISK_LOAD_ADDRESS@@ 0x0 ${ramdisk_size}>
	setenv rootfs_param @@KERNEL_ROOT_RAMDISK@@
else
    setenv rootfs_param @@KERNEL_ROOT_SD@@
fi

fdt mknod /chosen dom0@@@XEN_DOM0_KERNEL_NODE_UADDR@@
fdt set /chosen/dom0@@@XEN_DOM0_KERNEL_NODE_UADDR@@ compatible  "xen,linux-zimage" "xen,multiboot-module" "multiboot,module"
fdt set /chosen/dom0@@@XEN_DOM0_KERNEL_NODE_UADDR@@ reg <0x0 @@XEN_KERNEL_LOAD_ADDRESS@@ 0x0 ${kernel_size}>
setenv dom0_bootargs "console=hvc0 earlycon=xen earlyprintk=xen clk_ignore_unused ${rootfs_param}"
fdt set /chosen xen,dom0-bootargs \"${dom0_bootargs}\"

# Linux DomU0 Guest
fdt mknod /chosen domU0
fdt set /chosen/domU0 compatible "xen,domain"
fdt set /chosen/domU0 \#address-cells <0x2>
fdt set /chosen/domU0 \#size-cells <0x2>
fdt set /chosen/domU0 memory <0x0 @@DOMU0_MEM@@>
fdt set /chosen/domU0 cpus <1>
fdt set /chosen/domU0 vpl011 "pl011"
fdt set /chosen/domU0 xen,enhanced "enabled"
setenv domU0_rootfs_param @@KERNEL_ROOT_RAMDISK@@
fdt mknod /chosen/domU0 module@@@XEN_DOMU0_KERNEL_NODE_UADDR@@
fdt set /chosen/domU0/module@@@XEN_DOMU0_KERNEL_NODE_UADDR@@ compatible "multiboot,kernel" "multiboot,module"
fdt set /chosen/domU0/module@@@XEN_DOMU0_KERNEL_NODE_UADDR@@ reg <0x0 @@XEN_DOMU0_KERNEL_LOAD_ADDRESS@@ 0x0 ${domu0_kernel_size}>
setenv domU0_bootargs "console=ttyAMA0 ${domU0_rootfs_param}"
fdt set /chosen/domU0/module@@@XEN_DOMU0_KERNEL_NODE_UADDR@@ bootargs \"${domU0_bootargs}\"

fdt mknod /chosen/domU0 module@@@XEN_DOMU0_RAMDISK_NODE_UADDR@@
fdt set /chosen/domU0/module@@@XEN_DOMU0_RAMDISK_NODE_UADDR@@ compatible  "multiboot,ramdisk" "multiboot,module"
fdt set /chosen/domU0/module@@@XEN_DOMU0_RAMDISK_NODE_UADDR@@ reg <0x0 @@XEN_DOMU0_RAMDISK_LOAD_ADDRESS@@ 0x0 ${domU0_ramdisk_size}>

setenv fdt_high 0xffffffffffffffff

booti @@XEN_LOAD_ADDRESS@@ - $fdtcontroladdr
