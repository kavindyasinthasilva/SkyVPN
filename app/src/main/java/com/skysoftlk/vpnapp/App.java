package com.skysoftlk.vpnapp;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;

import org.conscrypt.Conscrypt;

import java.security.Security;


public class App extends Application {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();

        // Fix: Failed to export logs. The request could not be executed. Full error message: timeout
        // Increase OpenTelemetry export timeouts to prevent InterruptedIOException
        System.setProperty("otel.exporter.otlp.timeout", "60000");
        System.setProperty("otel.exporter.otlp.logs.timeout", "60000");

        // Fix: [OneSignal-IO-1] HttpClient: null Error thrown from network stack.
        // java.io.IOException: unexpected end of stream on com.android.okhttp.Address
        // Disabling http.keepAlive to prevent "unexpected end of stream" error caused by stale connections.
        System.setProperty("http.keepAlive", "false");

        // Fix: javax.net.ssl.SSLProtocolException: SSLV3_ALERT_CLOSE_NOTIFY
        // Some servers close the connection abruptly. Forcing TLS protocols can sometimes help stability.
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");

        // OneSignal Initialization
        OneSignal.getDebug().setLogLevel(LogLevel.WARN);
        OneSignal.initWithContext(this, "6e0e45ef-fd88-45ab-a646-03a36237f0ce");

        // Fix: javax.net.ssl.SSLProtocolException: SSLV3_ALERT_CLOSE_NOTIFY
        // Update the security provider to use modern SSL/TLS protocols and fix handshake issues.
        try {
            ProviderInstaller.installIfNeeded(this);
            Log.i(TAG, "Security Provider updated via GMS.");
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "Google Play Services not available for Security Provider update. Falling back to Conscrypt.", e);
            // Fallback to Conscrypt for non-GMS devices (like Huawei)
            Security.insertProviderAt(Conscrypt.newProvider(), 1);
            Log.i(TAG, "Security Provider updated via Conscrypt.");
        }
    }

    @Nullable
    @Override
    public Object getSystemService(@NonNull String name) {
        // Fix for AccessibilityManagerService is dead (android.os.DeadObjectException)
        // This occurs when the system accessibility service crashes and the app tries to access it.
        if (Context.ACCESSIBILITY_SERVICE.equals(name)) {
            try {
                return super.getSystemService(name);
            } catch (Throwable t) {
                Log.e(TAG, "Failed to get AccessibilityManagerService, it might be dead.", t);
                return null;
            }
        }
        return super.getSystemService(name);
    }

}
