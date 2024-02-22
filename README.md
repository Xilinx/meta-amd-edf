# meta-basecamp ⛺

Repo for Basecamp development 🏔

```
mkdir basecamp && cd basecamp
repo init -u https://gitenterprise.xilinx.com/Yocto/meta-basecamp.git -b 2024.1 -m manifests/default.xml
repo sync

TEMPLATECONF=../sources/meta-basecamp/conf/templates/internal/ . sources/poky/oe-init-build-env

MACHINE=vek280-versal bitbake core-image-full-cmdline

MACHINE=vek280-versal runqemu nographic slirp
```
