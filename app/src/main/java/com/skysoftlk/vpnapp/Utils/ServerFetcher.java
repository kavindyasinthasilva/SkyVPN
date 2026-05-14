package com.skysoftlk.vpnapp.Utils;

import android.content.Context;
import android.util.Log;
import java.io.IOException;
import top.oneconnectapi.app.api.OneConnect;

public class ServerFetcher {
    private static final String TAG = "ServerFetcher";
    private static final String API_KEY = "K.F.P5RToLk5TXNPi2.4P6edjtY0gyfhZUAO.CWkB1KYhs4I4w";

    public interface FetchCallback {
        void onFetchComplete();
    }

    public static void fetchServers(Context context, FetchCallback callback) {
        new Thread(() -> {
            try {
                OneConnect oneConnect = new OneConnect();
                oneConnect.initialize(context, API_KEY);
                
                int retryCount = 0;
                int maxRetries = 3;
                boolean success = false;
                
                while (retryCount < maxRetries && !success) {
                    try {
                        String free = oneConnect.fetch(true);
                        String premium = oneConnect.fetch(false);
                        
                        if (free != null && !free.isEmpty()) {
                            Constants.FREE_SERVERS = free;
                        }
                        if (premium != null && !premium.isEmpty()) {
                            Constants.PREMIUM_SERVERS = premium;
                        }
                        
                        success = true;
                        Log.d(TAG, "Servers fetched successfully");
                    } catch (IOException e) {
                        retryCount++;
                        Log.e(TAG, "Fetch attempt " + retryCount + " failed: " + e.getMessage());
                        if (retryCount < maxRetries) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "OneConnect initialization failed: " + e.getMessage());
            } finally {
                if (callback != null) {
                    callback.onFetchComplete();
                }
            }
        }).start();
    }
}
