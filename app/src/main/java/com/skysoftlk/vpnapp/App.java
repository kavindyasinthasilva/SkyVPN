package com.skysoftlk.vpnapp;


import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.security.ProviderInstaller;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.skysoftlk.vpnapp.Utils.ChinaUtils;

import org.conscrypt.Conscrypt;

import java.security.Security;


public class App extends Application {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();

        // Fix: Failed to register com.google.android.gms.providerinstaller#com.skysoftlk.vpnapp
        // Fix: API: Phenotype.API is not available on this device.
        // Update the security provider to use modern SSL/TLS protocols and fix handshake issues.
        // We prioritize Conscrypt to avoid GMS-related connection errors (DEVELOPER_ERROR) and ensure stability.
        // Conscrypt must be initialized first to ensure all subsequent network calls use the modern provider.
        boolean conscryptInstalled = false;
        try {
            Security.insertProviderAt(Conscrypt.newProvider(), 1);
            Log.i(TAG, "Security Provider updated via Conscrypt.");
            conscryptInstalled = true;
        } catch (Throwable e) {
            Log.e(TAG, "Conscrypt initialization failed.", e);
        }

        // Defer non-critical initializations to a background thread to prevent startup ANRs
        // especially when the system is under high load (kswapd, high CPU usage).
        final boolean finalConscryptInstalled = conscryptInstalled;
        new Thread(() -> {
            // Fix: Failed to export logs. The request could not be executed. Full error message: timeout
            // Increase OpenTelemetry export timeouts to prevent InterruptedIOException
            System.setProperty("otel.exporter.otlp.timeout", "60000");
            System.setProperty("otel.exporter.otlp.logs.timeout", "60000");

            // Fix: javax.net.ssl.SSLProtocolException: SSLV3_ALERT_CLOSE_NOTIFY
            // Some servers close the connection abruptly. Forcing TLS protocols can sometimes help stability.
            System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");

            // OneSignal Initialization
            // OneSignal recommends initialization on the main thread.
            // CHINA FIX: Skip OneSignal in China to prevent constant CPU-heavy network retries
            if (!ChinaUtils.isLikelyInChina(this)) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    OneSignal.getDebug().setLogLevel(LogLevel.WARN);
                    OneSignal.initWithContext(this, "6e0e45ef-fd88-45ab-a646-03a36237f0ce");
                });
            } else {
                Log.i(TAG, "Skipping OneSignal initialization in China region.");
            }

            // Initialize Mobile Ads
            // CHINA FIX: Skip AdMob in China to prevent constant CPU-heavy network retries
            if (!ChinaUtils.isLikelyInChina(this)) {
                MobileAds.initialize(this, initializationStatus -> {
                    Log.i(TAG, "Mobile Ads initialized.");
                });
            } else {
                Log.i(TAG, "Skipping Mobile Ads initialization in China region.");
            }

            // Only attempt GMS ProviderInstaller if Conscrypt failed or as a fallback,
            // to avoid redundant service calls that can trigger SecurityExceptions in GoogleApiManager.
            if (!finalConscryptInstalled) {
                try {
                    ProviderInstaller.installIfNeededAsync(this, new ProviderInstaller.ProviderInstallListener() {
                        @Override
                        public void onProviderInstalled() {
                            Log.i(TAG, "Security Provider updated via GMS.");
                        }

                        @Override
                        public void onProviderInstallFailed(int errorCode, @Nullable android.content.Intent recoveryIntent) {
                            Log.w(TAG, "Google Play Services Security Provider update failed. GMS Phenotype API may be unavailable. Error: " + errorCode);
                        }
                    });
                } catch (Throwable e) {
                    Log.e(TAG, "GMS ProviderInstaller setup failed.", e);
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public Object getSystemService(@NonNull String name) {
        // Fix for DeadSystemException / DeadObjectException
        // When the system server is dying or under extreme load, accessing any service can crash the app.
        try {
            return super.getSystemService(name);
        } catch (Throwable t) {
            Log.e(TAG, "System service '" + name + "' is unavailable or the system process is dead.", t);
            return null;
        }
    }

}
