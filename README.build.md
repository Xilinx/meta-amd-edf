# Build Instructions

This section describes how to get your build host ready to work with meta-amd-edf
layers.

The following instructions require OE-Core meta and BitBake. Poky provides these
components, however they can be acquired separately.

> **Pre-requisites:** See [Preparing Build Host](https://docs.yoctoproject.org/5.0.1/singleindex.html#preparing-the-build-host) documentation.

1. Install Repo tool.

   * Download the Repo script.
   ```
   $ curl https://storage.googleapis.com/git-repo-downloads/repo > repo
   ```

   * Make it executable.
   ```
   $ chmod a+x repo
   ```

   * Move it on to your system path.
   ```
   $ sudo mv repo /usr/local/bin/
   ```
   * If it is correctly installed, you should see a Usage message when invoked
   with the help flag.
   ```
   $ repo --help
   ```

2. Initialize a Repo client.
    
    * Create a project directory.
    ```
    $ mkdir -p yocto/<release_version>
    $ cd yocto/<release_version>
    ```

    * Clone the Yocto meta layer source using yocto manifest as show below.
    ```
    $ repo init -u https://gitenterprise.xilinx.com/Yocto/meta-amd-edf.git -b <release_version> -m manifests/default.xml
    ```
    A successful initialization will end with a message stating that Repo is
    initialized in your working directory. Your directory should now contain a
    .repo directory where repo control files such as the manifest are stored but
    you should not need to touch this directory.

    To learn more about repo, look at https://source.android.com/setup/develop/repo

3. Fetch all the repositories.
```
$ repo sync
```

4. Start a branch with for development starting from the revision specified in
   the manifest. This is an optional step.
```
$ repo start <branch_name> --all
```

5. Initialize a build environment by sourcing the `edf-init-build-env` script.
```
$ source edf-init-build-env
```
> **Note:** This expects you are using the repo configuration from earlier steps.
> This script is simply automating the Yocto Project workflow steps.
```
$ export TEMPLATECONF=./<absolute-path-to-layer>/meta-amd-edf/conf/templates/default
$ source ./<path-to-layer>/poky/oe-init-build-env
```

6. Once environment is initialized `bblayers.conf` add `local.conf` are set from
   meta-amd-edf templates.

7. Set hardware `MACHINE` configuration variable in build/conf/local.conf
   file for a specific target which can boot and run the in the board or QEMU.
```
MACHINE="versal-vek280-sdt-seg-ospi"
```

* For list of available target machines see meta-adaptive-socs/meta-amd-<eval-board>/conf/machine/*.conf file.

8. For NFS build host system modify the build/conf/local.conf and add TMPDIR
   path as shown below. On local storage $TMPDIR will be set to build/tmp
```
TMPDIR = "/tmp/$USER/yocto/release_version/build"
```

9. Build the qemu-helper-native package to setup QEMU network tap devices.
```
$ bitbake qemu-helper-native
```

10. Manually configure a tap interface for your build system. As root run
   <path-to>/sources/poky/scripts/runqemu-gen-tapdevs, which should generate a
   list of tap devices. Once tap interfaces are successfully create you should
   be able to see all the interfaces by running ifconfig command.

```
$ sudo ./<path-to-layer>/poky/scripts/runqemu-gen-tapdevs $(id -g $USER) 4
```

11. Build an OS image for the target using `bitbake` command.
```
$ bitbake core-image-full-cmdline
```

12. Build OSPI bin file for QEMU boot.
```
$ bitbake ospi
```

13. Once complete the images for the target machine will be available in the output
   directory `${TMPDIR}/deploy/images/${MACHINE}/`.

14. Follow [Booting Instructions](https://github.com/Xilinx/meta-xilinx/blob/master/README.booting.md)
