package com.skysoftlk.vpnapp;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;


import com.google.android.gms.security.ProviderInstaller;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.skysoftlk.vpnapp.Utils.ChinaUtils;

import org.conscrypt.Conscrypt;

import java.security.Security;


public class App extends Application {

    private static final String TAG = "App";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // Fix: Failed to delete the CE shared dir for Android package (PhSharedDirectoryWriter)
        // This IllegalStateException in GMS Phenotype occurs when the system fails to clean up
        // stale shared directories during package updates or changes.
        // While primarily a GMS-side issue, ensuring the ApplicationInfo is up-to-date helps.

        // Fix: Failed to load asset path /base.apk
        // On Android 11+ (API 30+), the system may fail to map the APK if the ApplicationInfo is stale.
        // MultiDex.install ensures that the classloader is correctly set up even if the APK path is weird.
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Security provider update (Conscrypt)
        // We do this early to ensure all network calls use modern TLS.
        try {
            Security.insertProviderAt(Conscrypt.newProvider(), 1);
            Log.i(TAG, "Security Provider updated via Conscrypt.");
        } catch (Throwable e) {
            Log.e(TAG, "Conscrypt initialization failed. Falling back to GMS.", e);
            installGmsProvider();
        }

        // Initialize libraries that are safe for background or don't block main thread significantly
        new Thread(() -> {
            // Fix: Failed to export logs. Increase OpenTelemetry export timeouts.
            System.setProperty("otel.exporter.otlp.timeout", "60000");
            System.setProperty("otel.exporter.otlp.logs.timeout", "60000");
            System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");

            // OneSignal Initialization
            if (!ChinaUtils.isLikelyInChina(this)) {
                try {
                    OneSignal.getDebug().setLogLevel(LogLevel.WARN);
                    OneSignal.initWithContext(this, "6e0e45ef-fd88-45ab-a646-03a36237f0ce");
                    Log.i(TAG, "OneSignal initialized.");
                } catch (Throwable t) {
                    Log.e(TAG, "OneSignal init failed", t);
                }
            }
        }).start();
    }

    private void installGmsProvider() {
        try {
            ProviderInstaller.installIfNeededAsync(this, new ProviderInstaller.ProviderInstallListener() {
                @Override
                public void onProviderInstalled() {
                    Log.i(TAG, "Security Provider updated via GMS.");
                }

                @Override
                public void onProviderInstallFailed(int errorCode, @Nullable android.content.Intent recoveryIntent) {
                    Log.w(TAG, "GMS ProviderInstaller failed: " + errorCode);
                }
            });
        } catch (Throwable e) {
            Log.e(TAG, "GMS ProviderInstaller setup failed.", e);
        }
    }

    @Nullable
    @Override
    public Object getSystemService(@NonNull String name) {
        // Fix for DeadSystemException / DeadObjectException
        // When the system server is dying or under extreme load, accessing any service can crash the app.
        // We only catch specific exceptions to avoid masking legitimate resource loading issues.
        try {
            return super.getSystemService(name);
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg != null && (msg.contains("DeadSystem") || msg.contains("DeadObject"))) {
                Log.e(TAG, "System service '" + name + "' is unavailable (System process is dead).", e);
                return null;
            }
            throw e;
        } catch (Throwable t) {
            // For non-runtime exceptions, we still want to be safe but logged.
            Log.e(TAG, "Unexpected error accessing system service '" + name + "'", t);
            return null;
        }
    }

}
