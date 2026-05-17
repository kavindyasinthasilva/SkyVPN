package com.skysoftlk.vpnapp;


import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.ads.AudienceNetworkAds;
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

        // Defer all heavy initializations to a background thread to prevent startup ANRs
        // especially when the system is under high load (kswapd, high CPU usage).
        // The error "No response to onStartJob" indicates the main thread is too busy to handle JobScheduler tasks.
        new Thread(() -> {
            // Fix: Failed to register com.google.android.gms.providerinstaller
            // Update the security provider to use modern SSL/TLS protocols and fix handshake issues.
            // We use Conscrypt in the background to avoid blocking the main thread during startup.
            boolean conscryptInstalled = false;
            try {
                Security.insertProviderAt(Conscrypt.newProvider(), 1);
                Log.i(TAG, "Security Provider updated via Conscrypt.");
                conscryptInstalled = true;
            } catch (Throwable e) {
                Log.e(TAG, "Conscrypt initialization failed.", e);
            }

            // Fix: Failed to export logs. Increase OpenTelemetry export timeouts.
            System.setProperty("otel.exporter.otlp.timeout", "60000");
            System.setProperty("otel.exporter.otlp.logs.timeout", "60000");
            System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");

            // Stagger initializations to reduce peak CPU load and memory pressure (major faults)
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

            // OneSignal Initialization
            if (!ChinaUtils.isLikelyInChina(this)) {
                // OneSignal 5.x initialization can happen on a background thread
                try {
                    OneSignal.getDebug().setLogLevel(LogLevel.WARN);
                    OneSignal.initWithContext(this, "6e0e45ef-fd88-45ab-a646-03a36237f0ce");
                    Log.i(TAG, "OneSignal initialized.");
                } catch (Throwable t) {
                    Log.e(TAG, "OneSignal init failed", t);
                }
            }

            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

            // Initialize Mobile Ads and Facebook Ads
            if (!ChinaUtils.isLikelyInChina(this)) {
                try {
                    AudienceNetworkAds.initialize(this);
                    MobileAds.initialize(this, initializationStatus -> {
                        Log.i(TAG, "Mobile Ads initialized.");
                    });
                } catch (Throwable t) {
                    Log.e(TAG, "Ads init failed", t);
                }
            }

            // Fallback GMS ProviderInstaller if Conscrypt failed
            if (!conscryptInstalled) {
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
