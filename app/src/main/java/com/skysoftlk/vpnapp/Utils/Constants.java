package com.skysoftlk.vpnapp.Utils;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Constants {
    public static String FREE_SERVERS = "";
    public static String PREMIUM_SERVERS = "";
    public static String ONECONNECT_KEY = "";

    // MANUAL SERVER LIST FOR CHINA RESILIENCE
    public static final String MANUAL_SERVERS_JSON = "[" +
            "  {" +
            "    \"serverName\": \"India (Mumbai) Optimized\"," +
            "    \"flagUrl\": \"https://flagpedia.net/data/flags/w580/in.png\"," +
            "    \"ovpnConfig\": \"india.ovpn\"," +
            "    \"userName\": \"0n0p0v0n0e0p0o\"," +
            "    \"password\": \"0q0m020O0D0Y0w0i0x0k0N0j\"" +
            "  }" +
            "]";

    public static String getAssetFileContent(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            int read = is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }
}
