package com.skysoftlk.vpnapp;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {

    public static final String all_month_id = "toto1";
    public static final String all_threemonths_id = "toto2";
    public static final String all_sixmonths_id = "toto3";
    public static final String all_yearly_id = "toto4";

    /*settings parameters (don't change them these are auto controlled by application flow)*/
    public static boolean ads_subscription = false;
    public static boolean vip_subscription = false;
    public static boolean all_subscription = false;

    /**
     * Checks if the user is still within the 3-day free trial period.
     */
    public static boolean isTrialActive(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("vpn_prefs", Context.MODE_PRIVATE);
        long installTime = prefs.getLong("install_time", 0);
        
        if (installTime == 0) {
            installTime = System.currentTimeMillis();
            prefs.edit().putLong("install_time", installTime).apply();
            return true;
        }
        
        long currentTime = System.currentTimeMillis();
        long threeDaysInMillis = 3 * 24 * 60 * 60 * 1000L;
        
        return (currentTime - installTime) < threeDaysInMillis;
    }

    /**
     * Checks if the user has access to the VPN (either via subscription or active trial).
     */
    public static boolean hasPremiumAccess(Context context) {
        return vip_subscription || all_subscription || isTrialActive(context);
    }
}
