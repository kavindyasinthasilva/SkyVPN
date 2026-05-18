package com.skysoftlk.skyvpnapp.Utils;

import android.content.Context;
import android.os.Build;
import java.util.Locale;

public class ChinaUtils {

    /**
     * Checks if the device is likely in China based on locale or manufacturer.
     */
    public static boolean isLikelyInChina(Context context) {
        // Check Locale
        String country = Locale.getDefault().getCountry();
        if ("CN".equalsIgnoreCase(country)) {
            return true;
        }

        // Check Manufacturer (common in China)
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        if (manufacturer.contains("huawei") || 
            manufacturer.contains("xiaomi") || 
            manufacturer.contains("oppo") || 
            manufacturer.contains("vivo") || 
            manufacturer.contains("meizu")) {
            return true;
        }

        return false;
    }

    /**
     * Provides a fallback API endpoint if the primary one is blocked.
     * You should replace this with a URL hosted on a China-friendly CDN (like Aliyun).
     */
    public static String getFallbackConfigUrl() {
        return "https://your-fallback-config-domain.com/vpn_config.json";
    }
}
