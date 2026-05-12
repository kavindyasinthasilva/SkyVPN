package com.skysoftlk.vpnapp;


import android.app.Application;

import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Fix: Failed to export logs. The request could not be executed. Full error message: timeout
        // Increase OpenTelemetry export timeouts to prevent InterruptedIOException
        System.setProperty("otel.exporter.otlp.timeout", "60000");
        System.setProperty("otel.exporter.otlp.logs.timeout", "60000");

        // OneSignal Initialization
        OneSignal.getDebug().setLogLevel(LogLevel.WARN);
        OneSignal.initWithContext(this, "6e0e45ef-fd88-45ab-a646-03a36237f0ce");
    }

}
