package com.skysoftlk.vpnapp.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skysoftlk.vpnapp.Activities.MainActivity;
import com.skysoftlk.vpnapp.R;
import com.skysoftlk.vpnapp.Activities.UnlockAllActivity;
import com.skysoftlk.vpnapp.AdapterWrappers.ServerListAdapterVip;
import com.skysoftlk.vpnapp.Config;
import com.facebook.ads.*;
import com.skysoftlk.vpnapp.Utils.Constants;
import com.skysoftlk.vpnapp.model.Countries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentVip extends Fragment {

    private RecyclerView recyclerView;
    private ServerListAdapterVip adapter;
    private static RewardedAd rewardedAd;
    private RewardedAd mRewardedAd;
    private RelativeLayout animationHolder;
    private static final String TAG = "CHECKADS";
    private RelativeLayout mPurchaseLayout;
    private ImageButton mUnblockButton;
    private static RewardedVideoAd rewardedVideoAd;
    private static SharedPreferences sharedPreferences;
    static Countries countryy;
    static View btView;
    public static Context context;
    public static boolean viewSet = false;
    static View view;
    public static boolean fbAdIsLoading = true;
    public static boolean googleAdIsLoading = true;
    public static boolean googleAdResune = false;
    public static boolean fbAdResume = false;
    public static ProgressDialog progressdialog;
    private static BottomSheetDialog btDialog;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdSettings.addTestDevice("c4894289-bd58-4ec5-b608-192469edce5a");
        AdSettings.addTestDevice("4cbd7f01-b2fb-4d12-ac35-f399d9f30351");
        AdSettings.addTestDevice("ad883e4f-8d84-4631-afdb-12104e62f4b8");
        AdSettings.addTestDevice("6b5e1429-599a-4c17-adc0-c1758563d3ec");
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_one, container, false);


        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Please wait just a moment !!");
        progressdialog.setCancelable(false);

        recyclerView = view.findViewById(R.id.region_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        animationHolder = view.findViewById(R.id.animation_layout);
        sharedPreferences = getContext().getSharedPreferences("userRewarded", Context.MODE_PRIVATE);
        btView = LayoutInflater.from(context)
                .inflate(R.layout.layout_bottom_sheet, (ConstraintLayout) view.findViewById(R.id.bsContainer));

        btDialog = new BottomSheetDialog(
                context, R.style.BottomSheetDialogTheme
        );

        btDialog.setContentView(btView);

        mPurchaseLayout = view.findViewById(R.id.purchase_layout);
        mUnblockButton = view.findViewById(R.id.vip_unblock);

        mPurchaseLayout.setVisibility(View.GONE);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.e("REWARDED INITIALIZ", initializationStatus.getAdapterStatusMap().toString());
                initAdMob();
            }
        });

        initOnClick();

        adapter = new ServerListAdapterVip(getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadServers();
    }

    private void loadServers() {
        ArrayList<Countries> servers = new ArrayList<>();

        // Load Manual Servers from JSON
        try {
            JSONArray manualArray = new JSONArray(Constants.MANUAL_SERVERS_JSON);
            for (int i = 0; i < manualArray.length(); i++) {
                JSONObject obj = manualArray.getJSONObject(i);
                servers.add(new Countries(
                        obj.getString("serverName"),
                        obj.getString("flagUrl"),
                        obj.getString("ovpnConfig"),
                        obj.getString("userName"),
                        obj.getString("password")
                ));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Manual JSON error: " + e.getMessage());
        }

        if (Constants.PREMIUM_SERVERS == null || Constants.PREMIUM_SERVERS.isEmpty()) {
            if (!servers.isEmpty()) {
                animationHolder.setVisibility(View.GONE);
                adapter.setData(servers);
            } else {
                animationHolder.setVisibility(View.GONE);
            }
            return;
        }

        try {
            // Check if the response is an error object instead of a list
            if (Constants.PREMIUM_SERVERS.startsWith("{")) {
                JSONObject errorObj = new JSONObject(Constants.PREMIUM_SERVERS);
                if (errorObj.has("result") && errorObj.getString("result").equals("error")) {
                    Log.e(TAG, "OneConnect Error: " + errorObj.optString("message"));
                    animationHolder.setVisibility(View.GONE);
                    return;
                }
            }

            JSONArray jsonArray = new JSONArray(Constants.PREMIUM_SERVERS);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                servers.add(new Countries(object.getString("serverName"),
                        object.getString("flag_url"),
                        object.getString("ovpnConfiguration"),
                        object.getString("vpnUserName"),
                        object.getString("vpnPassword")
                ));
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parsing error: " + e.getMessage());
            e.printStackTrace();
        }

        adapter.setData(servers);
        animationHolder.setVisibility(View.GONE);
    }

    public void initAdMob() {
        //ADMOB
        AdRequest adRequest = new AdRequest.Builder().build();

        if (MainActivity.indratech_toto_27640849_admob_reward != null) {

            RewardedAd.load(context, MainActivity.indratech_toto_27640849_admob_reward,
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                            mRewardedAd = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            mRewardedAd = rewardedAd;
                            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdShowedFullScreenContent() {

                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {

                                    Log.d(TAG, "Ad was dismissed.");
                                    mRewardedAd = null;
                                }
                            });
                        }
                    });
        }
    }

    public static void unblockServer() {

        btView.findViewById(R.id.but_subs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UnlockAllActivity.class));
                btDialog.dismiss();
            }
        });

        btDialog.show();
    }

    public static void onItemClick(Countries country) {
        countryy = country;

        if (Config.vip_subscription || Config.all_subscription) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("c", country);
            intent.putExtra("type", MainActivity.type);
            intent.putExtra("indratech_toto_27640849_ad_banner", MainActivity.indratech_toto_27640849_ad_banner_id);
            intent.putExtra("admob_interstitial", MainActivity.admob_interstitial_id);
            intent.putExtra("indratech_toto_27640849_fb_native", MainActivity.indratech_toto_27640849_fb_native_id);
            intent.putExtra("indratech_toto_27640849_fb_interstitial", MainActivity.indratech_toto_27640849_fb_interstitial_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        } else {
            unblockServer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        googleAdIsLoading = true;
        fbAdIsLoading = true;
        googleAdResune = false;
        fbAdResume = false;
        loadServers();
    }

    private void initOnClick() {
        btView.findViewById(R.id.watch_ads).setOnClickListener(v -> {

            progressdialog.show();

            if (mRewardedAd != null) {
                mRewardedAd.show(getActivity(), new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                        Log.d("TAG", "The user earned the reward.");

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("c", countryy);
                        intent.putExtra("type", MainActivity.type);
                        intent.putExtra("indratech_toto_27640849_ad_banner", MainActivity.indratech_toto_27640849_ad_banner_id);
                        intent.putExtra("admob_interstitial", MainActivity.admob_interstitial_id);
                        intent.putExtra("indratech_toto_27640849_fb_native", MainActivity.indratech_toto_27640849_fb_native_id);
                        intent.putExtra("indratech_toto_27640849_fb_interstitial", MainActivity.indratech_toto_27640849_fb_interstitial_id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("adWatched", true);
                        editor.apply();
                        rewardedAd = null;
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                });
            } else {
                Toast.makeText(context.getApplicationContext(), "Ad Not Available or Buy Subscription", Toast.LENGTH_SHORT).show();
            }

            progressdialog.dismiss();
            btDialog.dismiss();
        });

        btView.findViewById(R.id.watch_face_ads).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressdialog.show();

                rewardedVideoAd = new RewardedVideoAd(context, MainActivity.indratech_toto_27640849_fb_reward_id);

                RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
                    @Override
                    public void onError(Ad ad, AdError error) {
                        fbAdIsLoading = false;
                        progressdialog.dismiss();
                        Toast.makeText(context, "ads Not Available Or Buy Subscription", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "Rewarded video ad failed to load: " + error.getErrorMessage());
//                        Log.e(TAG, MainActivity.indratech_toto_27640849_fb_reward_id);
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        progressdialog.dismiss();
                        Log.d(TAG, "Rewarded video ad is loaded and ready to be displayed!");
                        fbAdIsLoading = false;
                        rewardedVideoAd.show();
                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                        Log.d(TAG, "Rewarded video ad clicked!");
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {

                        Log.d(TAG, "Rewarded video ad impression logged!");
                    }

                    @Override
                    public void onRewardedVideoCompleted() {

                        Log.d(TAG, "Rewarded video completed!");
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("c", countryy);
                        intent.putExtra("type", MainActivity.type);
                        intent.putExtra("indratech_toto_27640849_ad_banner", MainActivity.indratech_toto_27640849_ad_banner_id);
                        intent.putExtra("admob_interstitial", MainActivity.admob_interstitial_id);
                        intent.putExtra("indratech_toto_27640849_fb_native", MainActivity.indratech_toto_27640849_fb_native_id);
                        intent.putExtra("indratech_toto_27640849_fb_interstitial", MainActivity.indratech_toto_27640849_fb_interstitial_id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);

                    }

                    @Override
                    public void onRewardedVideoClosed() {

                        Log.d(TAG, "Rewarded video ad closed!");
                    }
                };
                rewardedVideoAd.loadAd(rewardedVideoAd.buildLoadAdConfig().withAdListener(rewardedVideoAdListener).build());
                btDialog.dismiss();

            }
        });
    }
}