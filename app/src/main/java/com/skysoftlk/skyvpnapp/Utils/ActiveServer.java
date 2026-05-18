package com.skysoftlk.skyvpnapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.skysoftlk.skyvpnapp.model.Countries;

public class ActiveServer {
    private static final String PREF_NAME = "activeServer";
    
    // In-memory cache to reduce SharedPreferences reads and improve performance
    private static Countries cachedServer = null;

    public static void saveServer(Countries countries, Context context) {
        if (countries == null) return;
        
        // Update memory cache
        cachedServer = countries;

        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit()
            .putString("countryName", countries.getCountry())
            .putString("vpnUserName", countries.getOvpnUserName())
            .putString("vpnPassword", countries.getOvpnUserPassword())
            .putString("config", countries.getOvpn())
            .putString("flagUrl", countries.getFlagUrl())
            .apply(); // apply() is asynchronous and doesn't block the UI thread
    }

    public static Countries getSavedServer(Context context) {
        // Return memory cache if available
        if (cachedServer != null) {
            return cachedServer;
        }

        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        
        String country = sp.getString("countryName", "");
        if (TextUtils.isEmpty(country)) {
            return new Countries("", "", "", "", "");
        }

        cachedServer = new Countries(
                country,
                sp.getString("flagUrl", ""),
                sp.getString("config", ""),
                sp.getString("vpnUserName", ""),
                sp.getString("vpnPassword", "")
        );

        return cachedServer;
    }

    public static void deleteSaveServer(Context context) {
        cachedServer = null;
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
}

