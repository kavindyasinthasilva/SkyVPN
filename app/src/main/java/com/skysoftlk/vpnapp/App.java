package com.skysoftlk.vpnapp;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;


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

        // OneSignal Initialization
        OneSignal.getDebug().setLogLevel(LogLevel.WARN);
        OneSignal.initWithContext(this, "6e0e45ef-fd88-45ab-a646-03a36237f0ce");
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
