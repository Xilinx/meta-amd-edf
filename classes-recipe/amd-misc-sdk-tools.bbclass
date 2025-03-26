#
# Copyright (C) 2024-2025, Advanced Micro Devices, Inc.  All rights reserved.
#
# SPDX-License-Identifier: MIT
#
# This bbclass is added to SDK_CLASSES variable to miscellaneous nativesdk
# recipes for SDK(populate_sdk task) generation. User can generate the sdk script
# and install it to use lopper and other tools on host.

# Additional basecamp tools
BASECAMP_MISC_TOOLCHAIN_HOST_TASK = "\
    nativesdk-python3-sqlite3 \
    nativesdk-python3-pyyaml \
    nativesdk-lopper \
    nativesdk-packagegroup-basecamp-tools \
"

BASECAMP_MISC_TOOLCHAIN_HOST_TASK:sdkmingw32 = ""

TOOLCHAIN_HOST_TASK += "${BASECAMP_MISC_TOOLCHAIN_HOST_TASK}"
