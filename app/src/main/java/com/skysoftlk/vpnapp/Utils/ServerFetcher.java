package com.skysoftlk.vpnapp.Utils;

import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import top.oneconnectapi.app.api.OneConnect;

public class ServerFetcher {
    private static final String TAG = "ServerFetcher";
    private static final String API_KEY = "K.F.P5RToLk5TXNPi2.4P6edjtY0gyfhZUAO.CWkB1KYhs4I4w";
    
    private static final AtomicBoolean isFetching = new AtomicBoolean(false);
    private static long lastFetchTime = 0;
    private static final long COOLDOWN = 30000; // 30 seconds cooldown between fetches

    public interface FetchCallback {
        void onFetchComplete(boolean success);
    }

    public static void fetchServers(Context context, FetchCallback callback) {
        // Prevent concurrent fetches
        if (isFetching.get()) {
            Log.d(TAG, "Already fetching servers, skipping...");
            return;
        }

        // Rate limiting
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFetchTime < COOLDOWN && !Constants.FREE_SERVERS.isEmpty()) {
            Log.d(TAG, "Fetched recently, skipping...");
            if (callback != null) callback.onFetchComplete(true);
            return;
        }

        isFetching.set(true);
        new Thread(() -> {
            boolean success = false;
            try {
                OneConnect oneConnect = new OneConnect();
                oneConnect.initialize(context, API_KEY);
                
                int retryCount = 0;
                int maxRetries = 2; // Reduced retries to avoid long hangs
                
                while (retryCount < maxRetries && !success) {
                    try {
                        String free = oneConnect.fetch(true);
                        String premium = oneConnect.fetch(false);
                        
                        boolean dataChanged = false;
                        if (free != null && !free.isEmpty()) {
                            Constants.FREE_SERVERS = free;
                            dataChanged = true;
                        }
                        if (premium != null && !premium.isEmpty()) {
                            Constants.PREMIUM_SERVERS = premium;
                            dataChanged = true;
                        }
                        
                        if (dataChanged) {
                            success = true;
                            lastFetchTime = System.currentTimeMillis();
                            Log.d(TAG, "Servers fetched successfully");
                        } else {
                             Log.w(TAG, "Fetch returned empty data");
                             retryCount++;
                        }
                    } catch (IOException e) {
                        retryCount++;
                        Log.e(TAG, "Fetch attempt " + retryCount + " failed: " + e.getMessage());
                        if (retryCount < maxRetries) {
                            try {
                                Thread.sleep(3000);
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
                isFetching.set(false);
                if (callback != null) {
                    callback.onFetchComplete(success);
                }
            }
        }).start();
    }
}
