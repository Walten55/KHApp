package com.kehua.energy.monitor.app.model.entity;

import android.net.wifi.SupplicantState;
import android.text.TextUtils;

import com.kehua.energy.monitor.app.utils.WiFiUtils;
/**
 * Created by walten on 2017/12/8.
 */
public class HotspotInfo {
    private String ssid;

    private String capabilities;

    private int frequency;

    private int level;

    private SupplicantState supplicantState;

    public SupplicantState getSupplicantState() {
        return supplicantState;
    }

    public void setSupplicantState(SupplicantState supplicantState) {
        this.supplicantState = supplicantState;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public WiFiUtils.Data getCapabilitiesType(){

        if (!TextUtils.isEmpty(capabilities)) {
            if (capabilities.contains("WPA2") || capabilities.contains("wpa2")) {
                return  WiFiUtils.Data.WIFI_CIPHER_WPA2;

            }else if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                return  WiFiUtils.Data.WIFI_CIPHER_WPA;

            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                return  WiFiUtils.Data.WIFI_CIPHER_WEP;
            } else {
                return  WiFiUtils.Data.WIFI_CIPHER_NOPASS;
            }
        }else {
            return WiFiUtils.Data.WIFI_CIPHER_NOPASS;
        }
    }
}
