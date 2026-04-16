PACKAGES =+ " \
    ${PN}-mt7610 \
    ${PN}-mt7662 \
    ${PN}-mt7663 \
    ${PN}-mt7921 \
    ${PN}-mt7925 \
    ${PN}-rtl8192eu \
    ${PN}-rtw88 \
    ${PN}-rtw89 \
"

# MediaTek mt76x0U (MT7610U) - 802.11ac USB
# MT7650E uses the same mt76x0 driver as MT7610U so its firmware is grouped here.
LICENSE:${PN}-mt7610 = "Firmware-ralink_a_mediatek_company_firmware"
FILES:${PN}-mt7610 = " \
    ${nonarch_base_libdir}/firmware/mediatek/mt7610u.bin \
    ${nonarch_base_libdir}/firmware/mediatek/mt7610e.bin \
    ${nonarch_base_libdir}/firmware/mediatek/mt7650e.bin \
"
RDEPENDS:${PN}-mt7610 += "${PN}-mt76x-license"

# MediaTek mt76x2U (MT7612U) - 802.11ac USB
LICENSE:${PN}-mt7662 = "Firmware-ralink_a_mediatek_company_firmware"
FILES:${PN}-mt7662 = " \
    ${nonarch_base_libdir}/firmware/mediatek/mt7662u.bin \
    ${nonarch_base_libdir}/firmware/mediatek/mt7662u_rom_patch.bin \
"
RDEPENDS:${PN}-mt7662 += "${PN}-mt76x-license"

# MediaTek MT7663U - 802.11ac USB
LICENSE:${PN}-mt7663 = "Firmware-ralink_a_mediatek_company_firmware"
FILES:${PN}-mt7663 = " \
    ${nonarch_base_libdir}/firmware/mediatek/mt7663_n9_v3.bin \
    ${nonarch_base_libdir}/firmware/mediatek/mt7663pr2h.bin \
    ${nonarch_base_libdir}/firmware/mediatek/mt7663_n9_rebb.bin \
    ${nonarch_base_libdir}/firmware/mediatek/mt7663pr2h_rebb.bin \
"
RDEPENDS:${PN}-mt7663 += "${PN}-mt76x-license"

# MediaTek MT7921U - Wi-Fi 6 USB
# Firmware uses MT7961 naming convention
LICENSE:${PN}-mt7921 = "Firmware-mediatek"
FILES:${PN}-mt7921 = " \
    ${nonarch_base_libdir}/firmware/mediatek/WIFI_RAM_CODE_MT7961_1.bin \
    ${nonarch_base_libdir}/firmware/mediatek/WIFI_MT7961_patch_mcu_1_2_hdr.bin \
"
RDEPENDS:${PN}-mt7921 += "${PN}-mediatek-license"

# MediaTek MT7925U - Wi-Fi 6E USB
LICENSE:${PN}-mt7925 = "Firmware-mediatek"
FILES:${PN}-mt7925 = " \
    ${nonarch_base_libdir}/firmware/mediatek/mt7925/WIFI_RAM_CODE_MT7925_1_1.bin \
    ${nonarch_base_libdir}/firmware/mediatek/mt7925/WIFI_MT7925_PATCH_MCU_1_1_hdr.bin \
"
RDEPENDS:${PN}-mt7925 += "${PN}-mediatek-license"

# Realtek RTL8192EU - 802.11n USB (rtl8xxxu driver)
# Firmware exists in upstream linux-firmware but has no subpackage.
LICENSE:${PN}-rtl8192eu = "Firmware-rtlwifi_firmware"
FILES:${PN}-rtl8192eu = " \
    ${nonarch_base_libdir}/firmware/rtlwifi/rtl8192eu_nic.bin \
    ${nonarch_base_libdir}/firmware/rtlwifi/rtl8192eu_wowlan.bin \
    ${nonarch_base_libdir}/firmware/rtlwifi/rtl8192eu_ap_wowlan.bin \
"
RDEPENDS:${PN}-rtl8192eu += "${PN}-rtl-license"

# Realtek rtw88 - firmware not split into named subpackages upstream.
# rtw8821*/rtw8822* are already claimed by linux-firmware-rtl8821/rtl8822.
LICENSE:${PN}-rtw88 = "Firmware-rtlwifi_firmware"
FILES:${PN}-rtw88 = " \
    ${nonarch_base_libdir}/firmware/rtw88/rtw8723* \
    ${nonarch_base_libdir}/firmware/rtw88/rtw8703* \
    ${nonarch_base_libdir}/firmware/rtw88/README \
"
RDEPENDS:${PN}-rtw88 += "${PN}-rtl-license"

# Realtek rtw89 - Wi-Fi 6 USB firmware not split into named subpackages upstream.
LICENSE:${PN}-rtw89 = "Firmware-rtlwifi_firmware"
FILES:${PN}-rtw89 = "${nonarch_base_libdir}/firmware/rtw89/*"
RDEPENDS:${PN}-rtw89 += "${PN}-rtl-license"
