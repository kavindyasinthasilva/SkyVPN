package com.skysoftlk.vpnapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.VpnService;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.billingclient.api.SkuDetails;
import com.bumptech.glide.Glide;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.skysoftlk.vpnapp.Config;
import com.skysoftlk.vpnapp.R;
import com.skysoftlk.vpnapp.Utils.ActiveServer;
import com.skysoftlk.vpnapp.model.Countries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import top.oneconnectapi.app.OpenVpnApi;
import top.oneconnectapi.app.core.OpenVPNThread;

public class MainActivity extends ContentsActivity {

    public static String indratech_toto_27640849_fb_reward_id = "";
    public static String indratech_toto_27640849_admob_reward = "";
    public static String copyright_ivpnofficial_dont_change_the_value;
    public static Countries selectedCountry = null;
    private Locale locale;
    private boolean isFirst = true;

    public static String type = "";
    public static String indratech_toto_27640849_admob_id = "ca-app-pub-4509914586377718~7682770234";
    public static String indratech_toto_27640849_ad_banner_id = "ca-app-pub-4509914586377718/3798981108";
    public static String admob_interstitial_id = "ca-app-pub-4509914586377718/6888178875";
    public static String indratech_toto_27640849_aad_native_id = "";
    public static String indratech_toto_27640849_fb_native_id = "";
    public static String indratech_toto_27640849_fb_interstitial_id = "";
    public static boolean indratech_toto_27640849_all_ads_on_off = false;

    private OpenVPNThread vpnThread = new OpenVPNThread();

    private BillingClient billingClient;
    private Map<String, SkuDetails> skusWithSkuDetails = new HashMap<>();
    private final List<String> allSubs = new ArrayList<>(Arrays.asList(
            Config.all_month_id,
            Config.all_threemonths_id,
            Config.all_sixmonths_id,
            Config.all_yearly_id));

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        }
    };

    private void billingSetup() {
        if (billingClient.isReady()) {
            checkIfSubscribed();
            return;
        }
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    Log.v("CHECKBILLING", "ready");
                    checkIfSubscribed();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Log.v("CHECKBILLING", "disconnected");
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.

            }
        });
    }

    private void checkIfSubscribed() {
        if (!billingClient.isReady()) {
            billingSetup();
            return;
        }

        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                new PurchasesResponseListener() {
                    public void onQueryPurchasesResponse(BillingResult billingResult, List purchases) {
                        // check billingResult
                        // process returned purchase list, e.g. display the plans user owns

                        int isAcknowledged = 0;
                        Log.v("CHECKBILLING", "purchases: " + (purchases != null ? purchases.size() : "null"));
                        if(purchases != null && purchases.size() > 0) {
                            for (int i = 0; i < purchases.size(); i++) {
                                Log.v("CHECKBILLING", "" + purchases.get(i).toString());
                                isAcknowledged++;
                            }
                        }

                        Config.vip_subscription = isAcknowledged > 0;
                        Config.all_subscription = isAcknowledged > 0;

                        if (!Config.vip_subscription) {
                            updateSubscription();
                        }
                    }
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));

        billingSetup();

        Intent intent = getIntent();

        if(getIntent().getExtras() != null) {
            selectedCountry = getIntent().getExtras().getParcelable("c");
            updateUI("LOAD");

            if (!Utility.isOnline(getApplicationContext())) {
                showMessage("No Internet Connection", "error");
            } else {
                showInterstitialAndConnect();
            }
        } else {
            if(selectedCountry != null) {
                updateUI("CONNECTED");

                Glide.with(this)
                        .load(selectedCountry.getFlagUrl())
                        .into(imgFlag);
                flagName.setText(selectedCountry.getCountry());
            }
        }

        if (intent.getStringExtra("type") != null) {
            type = intent.getStringExtra("type");
            indratech_toto_27640849_ad_banner_id = intent.getStringExtra("indratech_toto_27640849_ad_banner");
            admob_interstitial_id = intent.getStringExtra("admob_interstitial");
            indratech_toto_27640849_fb_native_id = intent.getStringExtra("indratech_toto_27640849_fb_native");
            indratech_toto_27640849_fb_interstitial_id = intent.getStringExtra("indratech_toto_27640849_fb_interstitial");
        }

        if(TextUtils.isEmpty(type)) {
            type = "";
            Log.v("AD_TYPE", " null");
        }

        if (type.equals("ad")) {
            RequestConfiguration.Builder requestBuilder = new RequestConfiguration.Builder();
            MobileAds.setRequestConfiguration(requestBuilder.build());
        } else {
            AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE);

            //Initialize facebook ads
            AudienceNetworkAds.initialize(this);
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

        if(billingClient.isReady())
            checkIfSubscribed();
        else
            billingSetup();

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
        inAppUpdate();

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
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
            vpnThread.stop();
            updateUI("DISCONNECTED");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, 11);
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
        if (requestCode == 11) {
            Toast.makeText(this, "Start Downloand", Toast.LENGTH_SHORT).show();
            if (resultCode != RESULT_OK) {
                Log.d("Update", "Update failed" + resultCode);
            }
        }

        if (resultCode == RESULT_OK) {
            startVpn();
        } else {
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
                    startActivityForResult(intent, 1);
                } else
                    startVpn(); //have already permission
            } else {
                showMessage("Please select a server first", "");
            }

        } else {
            showMessage("No Internet Connection", "error");
        }
    }

    protected void startVpn() {
        try {
            ActiveServer.saveServer(MainActivity.selectedCountry, MainActivity.this);
            if (selectedCountry != null && selectedCountry.getOvpn() != null) {
                OpenVpnApi.startVpn(this, selectedCountry.getOvpn(), selectedCountry.getCountry(), selectedCountry.getOvpnUserName(), selectedCountry.getOvpnUserPassword());
            } else {
                showMessage("Invalid server configuration", "error");
            }

        } catch (RemoteException e) {
            e.printStackTrace();
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
            showInterstitialAndConnect();
            updateUI("LOAD");
        }
    }
}
