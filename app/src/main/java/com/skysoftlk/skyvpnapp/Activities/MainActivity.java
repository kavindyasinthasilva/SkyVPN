package com.skysoftlk.skyvpnapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import com.skysoftlk.skyvpnapp.Utils.LanguageManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.bumptech.glide.Glide;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.skysoftlk.skyvpnapp.Config;
import com.skysoftlk.skyvpnapp.R;
import com.skysoftlk.skyvpnapp.Utils.ActiveServer;
import com.skysoftlk.skyvpnapp.model.Countries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import top.oneconnectapi.app.OpenVpnApi;

public class MainActivity extends ContentsActivity {

    private static final int REQUEST_APP_UPDATE = 11;
    private static final int REQUEST_VPN_PERMISSION = 1001;
    private static final long VPN_CONNECTION_TIMEOUT_MS = 150000L;
    public static Countries selectedCountry = null;
    private Locale locale;
    private boolean isFirst = true;

    private BillingClient billingClient;
    private final List<String> allSubs = new ArrayList<>(Arrays.asList(
            Config.all_month_id,
            Config.all_threemonths_id,
            Config.all_sixmonths_id,
            Config.all_yearly_id));

    private final PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, list) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            checkIfSubscribed();
        }
    };

    private void updateTrialBanner() {
        if (!Config.vip_subscription && !Config.all_subscription) {
            if (Config.isTrialActive(this)) {
                // Show trial status
                long installTime = getSharedPreferences("vpn_prefs", MODE_PRIVATE).getLong("install_time", 0);
                long timeLeft = (3 * 24 * 60 * 60 * 1000L) - (System.currentTimeMillis() - installTime);
                long daysLeft = timeLeft / (24 * 60 * 60 * 1000L);
                long hoursLeft = (timeLeft % (24 * 60 * 60 * 1000L)) / (60 * 60 * 1000L);
                
                String trialMsg = "Free Trial: " + daysLeft + "d " + hoursLeft + "h left";
                if (tvConnectionStatus != null) tvConnectionStatus.setText(trialMsg);
            } else {
                if (tvConnectionStatus != null) tvConnectionStatus.setText("Trial Expired");
            }
        } else {
            if (tvConnectionStatus != null) tvConnectionStatus.setText("Premium Active");
        }
    }

    private void billingSetup() {
        if (billingClient == null) return;
        if (billingClient.isReady()) {
            checkIfSubscribed();
            return;
        }
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    Log.v("CHECKBILLING", "ready");
                    checkIfSubscribed();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Log.v("CHECKBILLING", "disconnected");
            }
        });
    }

    private void checkIfSubscribed() {
        if (billingClient == null || !billingClient.isReady()) {
            return;
        }

        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                (billingResult, purchases) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        boolean hasActiveSubs = false;
                        if (purchases != null) {
                            for (Purchase purchase : purchases) {
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                    hasActiveSubs = true;
                                    break;
                                }
                            }
                        }
                        
                    Config.vip_subscription = hasActiveSubs;
                    Config.all_subscription = hasActiveSubs;
                    runOnUiThread(this::updateTrialBanner);
                    }
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));

        // updateTrialBanner depends on Config values updated by billing
        runOnUiThread(this::updateTrialBanner);

        // Check if coming from Server selection
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("c")) {
            selectedCountry = getIntent().getExtras().getParcelable("c");
            
            if (selectedCountry != null) {
                updateUI("LOAD");
                updateFlagUI();

                if (!Utility.isOnline(getApplicationContext())) {
                    showMessage("No Internet Connection", "error");
                    updateUI("DISCONNECTED");
                } else {
                    prepareVpn();
                }
            }
        } else {
            // Load saved server on fresh start or return to app
            if (selectedCountry == null) {
                Countries saved = ActiveServer.getSavedServer(this);
                if (saved != null && !TextUtils.isEmpty(saved.getCountry())) {
                    selectedCountry = saved;
                }
            }
            
            if (selectedCountry != null) {
                updateFlagUI();
                // Sync UI with actual VPN status
                if (Utility.isVpnConnected(this)) {
                    updateUI("CONNECTED");
                } else {
                    updateUI("DISCONNECTED");
                }
            }
        }
    }

    private void updateFlagUI() {
        if (selectedCountry != null && imgFlag != null && flagName != null) {
            Glide.with(this)
                    .load(selectedCountry.getFlagUrl())
                    .placeholder(R.drawable.logo)
                    .into(imgFlag);
            flagName.setText(selectedCountry.getCountry());
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (billingClient != null && !billingClient.isReady()) {
             billingSetup();
        }

        // Sync VPN status to ensure UI reflects actual connection
        String currentStatus = getVpnStatus();
        if (Utility.isVpnConnected(this)) {
            if (currentStatus != null && currentStatus.equals("DISCONNECTED")) {
                updateUI("CONNECTED");
            }
        } else {
            if (currentStatus != null && !currentStatus.equals("DISCONNECTED") && !currentStatus.equals("LOAD")) {
                updateUI("DISCONNECTED");
            }
        }

        // Huawei battery optimization check
        if (Utility.isHuawei() && !Utility.isIgnoringBatteryOptimizations(this)) {
            showHuaweiBatteryDialog();
        }
    }

    private void showHuaweiBatteryDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Huawei Battery Optimization")
                .setMessage("To ensure a stable VPN connection on Huawei devices, please set this app to 'Manage manually' in App Launch settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    try {
                        Intent intent = new Intent();
                        intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity");
                        startActivity(intent);
                    } catch (Exception e) {
                        try {
                            Intent intent = new Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                            startActivity(intent);
                        } catch (Exception e2) {
                            Toast.makeText(this, "Please go to Battery settings manually", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Dismiss", null)
                .show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        inAppUpdate();
        billingSetup();

        findViewById(R.id.btnLanguage).setOnClickListener(v -> showLanguageDialog());
    }

    private void showLanguageDialog() {
        String[] languages = {"English", "Chinese", "German", "Italian", "Spanish", "French", "Arabic", "Hindi", "Tamil", "Sinhala", "Danish"};
        String[] langCodes = {"en", "zh", "de", "it", "es", "fr", "ar", "hi", "ta", "si", "da"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.change_language));
        builder.setItems(languages, (dialog, which) -> {
            String selectedLang = langCodes[which];
            LanguageManager.setNewLocale(this, selectedLang);
            
            // Restart activity to apply changes
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        if (billingClient != null) {
            billingClient.endConnection();
        }
        super.onDestroy();
    }

    @Override
    protected void disconnectFromVpn() {
        try {
            Intent intent = new Intent(this, top.oneconnectapi.app.DisconnectVPNActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateUI("DISCONNECTED");
    }

    @Override
    protected void checkRemainingTraffic() {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    private void inAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, REQUEST_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                    // Request the update.
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_APP_UPDATE) {
            Toast.makeText(this, "Start Download", Toast.LENGTH_SHORT).show();
            if (resultCode != RESULT_OK) {
                Log.d("Update", "Update failed" + resultCode);
            }
            return;
        }

        if (requestCode == REQUEST_VPN_PERMISSION && resultCode == RESULT_OK) {
            startVpn();
        } else if (requestCode == REQUEST_VPN_PERMISSION) {
            showMessage("Permission Denied", "error");
        }
    }

    public void prepareVpn() {

        Glide.with(this)
                .load(selectedCountry.getFlagUrl())
                .into(imgFlag);
        flagName.setText(selectedCountry.getCountry());

        if (Utility.isOnline(getApplicationContext())) {

            if(selectedCountry != null) {
                Intent intent = VpnService.prepare(this);
                Log.v("CHECKSTATE", "start");

                if (intent != null) {
                    startActivityForResult(intent, REQUEST_VPN_PERMISSION);
                } else
                    startVpn(); //have already permission
            } else {
                showMessage("Please select a server first", "");
            }

        } else {
            showMessage("No Internet Connection", "error");
        }
    }

    private final Handler connectionWatchdog = new Handler(Looper.getMainLooper());
    private final Runnable connectionTimeoutRunnable = () -> {
        if (isVpnStillConnecting()) {
            disconnectFromVpn();
            showMessage("Server is not responding. Please try another server.", "error");
        }
    };

    public void startVpn() {
        Countries country = selectedCountry;
        if (country == null || country.getOvpn() == null) {
            showMessage("Invalid server configuration", "error");
            return;
        }

        ActiveServer.saveServer(country, MainActivity.this);

        // Normalize OVPN string: convert literal "\n" to actual newline characters.
        String config = normalizeVpnConfig(country.getOvpn(), country.getCountry());
        String countryName = country.getCountry();
        String username = country.getOvpnUserName();
        String password = country.getOvpnUserPassword();

        // India and some high-latency servers can spend more than a minute retrying before they connect.
        connectionWatchdog.removeCallbacks(connectionTimeoutRunnable);
        connectionWatchdog.postDelayed(connectionTimeoutRunnable, VPN_CONNECTION_TIMEOUT_MS);

        new Thread(() -> {
            try {
                OpenVpnApi.startVpn(MainActivity.this, config, countryName, username, password);
            } catch (Exception e) {
                Log.e("CHECKSTATE", "Error starting VPN", e);
                runOnUiThread(() -> {
                    connectionWatchdog.removeCallbacks(connectionTimeoutRunnable);
                    updateUI("DISCONNECTED");
                    showMessage("Error starting VPN: " + e.getMessage(), "error");
                });
            }
        }, "openvpn-start").start();
    }

    private String normalizeVpnConfig(String rawConfig, String country) {
        if (rawConfig == null) return "";

        return rawConfig.replace("\\n", "\n");
    }

    private boolean isVpnStillConnecting() {
        String status = getVpnStatus();
        if (status == null) return false;

        switch (status.toUpperCase(Locale.US)) {
            case "LOAD":
            case "CONNECTING":
            case "AUTHENTICATION":
            case "WAIT":
            case "WAITING":
            case "RECONNECTING":
            case "CONNECTRETRY":
            case "TCP_CONNECT":
            case "VPN_GENERATE_CONFIG":
                return true;
            default:
                return false;
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isFinishing() || isDestroyed() || intent == null) return;
            try {
                String state = intent.getStringExtra("state");
                if (state != null) {
                    updateUI(state);
                    Log.v("CHECKSTATE", state);
                    
                    // Cancel watchdog on any successful state progression or termination
                    if (state.equals("CONNECTED") || state.equals("DISCONNECTED") || state.equals("EXITING") || state.equals("AUTH_FAILED") || state.equals("NONETWORK")) {
                        connectionWatchdog.removeCallbacks(connectionTimeoutRunnable);
                    }

                    if (state.equals("AUTH_FAILED")) {
                        updateUI("DISCONNECTED");
                        showMessage("VPN username/password was rejected by this server.", "error");
                    }
                }

                if (isFirst) {
                    Countries saved = ActiveServer.getSavedServer(MainActivity.this);
                    if (saved.getCountry() != null && !saved.getCountry().isEmpty()) {
                        selectedCountry = saved;

                        if (imgFlag != null) {
                            Glide.with(MainActivity.this)
                                    .load(selectedCountry.getFlagUrl())
                                    .into(imgFlag);
                        }
                        if (flagName != null) {
                            flagName.setText(selectedCountry.getCountry());
                        }
                    }

                    isFirst = false;
                }
            } catch (Exception e) {
                Log.e("CHECKSTATE", "Error updating state UI: " + e.getMessage());
            }

            try {
                String duration = intent.getStringExtra("duration");
                String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
                String byteIn = intent.getStringExtra("byteIn");
                String byteOut = intent.getStringExtra("byteOut");

                if (duration == null) duration = "00:00:00";
                if (lastPacketReceive == null) lastPacketReceive = "0";
                if (byteIn == null) byteIn = " ";
                if (byteOut == null) byteOut = " ";

                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut);
            } catch (Exception e) {
                Log.e("CHECKSTATE", "Error updating traffic UI: " + e.getMessage());
            }

        }
    };

    public void checkSelectedCountry() {
        if (selectedCountry == null) {
            updateUI("DISCONNECT");
            showMessage("Please select a server first", "");
        } else {
            prepareVpn();
            updateUI("LOAD");
        }
    }
}
