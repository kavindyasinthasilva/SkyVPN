package com.skysoftlk.skyvpnapp.Utils;

import android.content.Context;
import android.os.Build;
import java.util.Locale;
import java.util.TimeZone;

public class ChinaUtils {

    /**
     * Checks if the device is likely in China based on locale or manufacturer.
     */
    public static boolean isLikelyInChina(Context context) {
        String country = Locale.getDefault().getCountry();
        if ("CN".equalsIgnoreCase(country)) {
            return true;
        }

        String language = Locale.getDefault().getLanguage();
        String timezone = TimeZone.getDefault().getID();
        if ("zh".equalsIgnoreCase(language) && timezone != null && timezone.startsWith("Asia/Shanghai")) {
            return true;
        }

        if (!"Asia/Shanghai".equals(timezone)) {
            return false;
        }

        // Manufacturer is only a weak signal, so use it after locale/timezone checks.
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
