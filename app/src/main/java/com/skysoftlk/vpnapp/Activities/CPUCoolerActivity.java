package com.skysoftlk.vpnapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.skysoftlk.vpnapp.Apps;
import com.skysoftlk.vpnapp.Config;
import com.skysoftlk.vpnapp.R;
import com.skysoftlk.vpnapp.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


public class CPUCoolerActivity extends NavigationActivity {
    TextView batterytemp, showmain, showsec, nooverheating;
    float temp;
    ImageView coolbutton, tempimg,ivtemping;
    RecyclerView recyclerView;
    RecyclerAdapter mAdapter;
    public static List<Apps> apps;
    List<Apps> apps2;
    int check = 0;

    private NativeAd nativeAd;

    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            makeStabilityScanning(intent);
        }
    };

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_c_p_u_cooler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(MainActivity.type.equals("ad")) {
        }else{
            NativeAdLayout nativeAdLayout = findViewById(R.id.native_ad_container);

            com.facebook.ads.NativeAd nativeAd = new com.facebook.ads.NativeAd(this, MainActivity.indratech_toto_27640849_fb_native_id);
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad)
                {
                }

                @Override
                public void onError(Ad ad, AdError adError)
                {
                    if(MainActivity.indratech_toto_27640849_fb_native_id != null) Log.w("AdLoader", MainActivity.indratech_toto_27640849_fb_native_id);
                    Log.w("AdLoader", "onAdFailedToLoad" + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd == null || nativeAd != ad) {
                        return;
                    }
                    nativeAd.unregisterView();

                    if ((!Config.vip_subscription && !Config.all_subscription))
                    {
                        nativeAdLayout.setVisibility(View.VISIBLE);
                    }
                    LayoutInflater inflater = LayoutInflater.from(CPUCoolerActivity.this);
                    LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_layout, nativeAdLayout, false);
                    nativeAdLayout.addView(adView);

                    LinearLayout adChoicesContainer = nativeAdLayout.findViewById(R.id.ad_choices_container);
                    AdOptionsView adOptionsView = new AdOptionsView(CPUCoolerActivity.this, nativeAd, nativeAdLayout);
                    adChoicesContainer.removeAllViews();
                    adChoicesContainer.addView(adOptionsView, 0);

                    com.facebook.ads.MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
                    TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
                    com.facebook.ads.MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
                    TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
                    TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
                    TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
                    Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

                    nativeAdTitle.setText(nativeAd.getAdvertiserName());
                    nativeAdBody.setText(nativeAd.getAdBodyText());
                    nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                    nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                    nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                    sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

                    List<View> clickableViews = new ArrayList<>();
                    clickableViews.add(nativeAdTitle);
                    clickableViews.add(nativeAdCallToAction);

                    nativeAd.registerViewForInteraction(
                            adView, nativeAdMedia, nativeAdIcon, clickableViews);
                }

                @Override
                public void onAdClicked(Ad ad)
                {

                }

                @Override
                public void onLoggingImpression(Ad ad)
                {

                }
            };
            nativeAd.loadAd(
                    nativeAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        }

        try {
            recyclerView = findViewById(R.id.recycler_view);

            ivtemping= findViewById(R.id.iv_tempimg);
            tempimg = findViewById(R.id.tempimg);
            showmain = findViewById(R.id.showmain);
            showsec = findViewById(R.id.showsec);
            coolbutton = findViewById(R.id.coolbutton);
            nooverheating = findViewById(R.id.nooverheating);



            coolbutton.setImageResource(R.drawable.n_bt);
            ivtemping.setImageResource(R.drawable.ic_after_cooling_icon);
            tempimg.setImageResource(R.drawable.ic_cooling_complete);
            showmain.setText("NORMAL");
            showmain.setTextColor(Color.parseColor("#39c900"));
            showsec.setText("Temperature of CPU is Good");
            showsec.setTextColor(Color.parseColor("#4e5457"));
            nooverheating.setText("No Apps Overheating");
            nooverheating.setTextColor(Color.parseColor("#4e5457"));

            coolbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     LayoutInflater inflater = getLayoutInflater();
                    @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.my_toast, null);

                    TextView text = (TextView) layout.findViewById(R.id.textView1);
                    text.setText("Temperature of CPU is Already Normal.");

                    Toast toast = new Toast(CPUCoolerActivity.this);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 70);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            });


            batterytemp = findViewById(R.id.batterytemp);

            if (!((System.currentTimeMillis() - getSharedPreferences("APPS_CONFIGS", Context.MODE_PRIVATE).getLong("COOLER_LAST_UPDATE", 0)) < 1200000)) {
                makeStabilityScanning(null);
            }

            Log.e("Temperrature", temp + "");
        } catch (Exception e) {

        }
    }
    @Override
    public void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        try {

           unregisterReceiver(batteryReceiver);
        } catch (Exception e) {

        }
        super.onDestroy();

    }

    public void getAllICONS() {

        PackageManager pm = getPackageManager();

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);


        if (packages != null) {
            for (int k = 0; k < packages.size(); k++) {

                String packageName = packages.get(k).packageName;
                Log.e("packageName-->", "" + packageName);

                if (!packageName.equals("fast.cleaner.battery.saver")) {



                    Drawable ico = null;
                    try {
                        String pName = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
                        Apps app = new Apps();



                        File file = new File(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA).publicSourceDir);
                        long size = file.length();

                        Log.e("SIZE", size / 1000000 + "");
                        app.setSize(size / 1000000 + 20 + "MB");

                        ApplicationInfo a = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                        app.setImage(ico = getPackageManager().getApplicationIcon(packages.get(k).packageName));
                        getPackageManager();
                        Log.e("ico-->", "" + ico);

                        if (((a.flags & ApplicationInfo.FLAG_SYSTEM) == 0)) {


                            if (check <= 5) {
                                check++;
                                apps.add(app);
                            } else {
                                unregisterReceiver(batteryReceiver);

                                break;
                            }

                        }
                        mAdapter.notifyDataSetChanged();


                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e("ERROR", "Unable to find icon for package '"
                                + packageName + "': " + e.getMessage());
                    }

                }
            }

        }

        if (apps.size() > 1) {
            mAdapter = new RecyclerAdapter(apps);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void makeStabilityScanning (Intent intent) {
        try {
            if (intent == null)
                intent =registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            temp = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 21)) / 10;

            batterytemp.setText(temp + "°C");

            if (temp >= 30.0) {
                apps = new ArrayList<>();
                apps2 = new ArrayList<>();
                tempimg.setImageResource(R.drawable.ic_cpu_cooler_bg);
                ivtemping.setImageResource(R.drawable.ic_before_cpu_cooler_icon);
                coolbutton.setImageResource(R.drawable.n_bt);
                showmain.setText("OVERHEATED");
                showmain.setTextColor(Color.parseColor("#F63030"));
                showsec.setText("Apps are causing problem hit cool down");
                nooverheating.setText("");


                coolbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPreferences = getSharedPreferences("APPS_CONFIGS", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("COOLER_LAST_UPDATE", System.currentTimeMillis());
                        editor.apply();

                        Intent i = new Intent(CPUCoolerActivity.this, ScannerCPU.class);
                        startActivity(i);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                nooverheating.setText("No App Overheating");
                                nooverheating.setTextColor(Color.parseColor("#4e5457"));
                                showmain.setText("NORMAL");
                                showmain.setTextColor(Color.parseColor("#39c900"));
                                showsec.setText("Temperature of CPU is Good");
                                showsec.setTextColor(Color.parseColor("#4e5457"));
                                coolbutton.setImageResource(R.drawable.n_bt);
                                ivtemping.setImageResource(R.drawable.ic_after_cooling_icon);
                                tempimg.setImageResource(R.drawable.ic_ultra_power_mode_rounded_bg);
                                batterytemp.setText("25.3" + "°C");
                                recyclerView.setAdapter(null);

                            }
                        }, 2000);


                        coolbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                LayoutInflater inflater = getLayoutInflater();
                                @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.my_toast, null);

                                TextView text = (TextView) layout.findViewById(R.id.textView1);
                                text.setText("CPU Temperature is Already Normal.");

                                Toast toast = new Toast(CPUCoolerActivity.this);
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 70);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                            }
                        });
                    }
                });

                recyclerView.setItemAnimator(new SlideInLeftAnimator());

                recyclerView.getItemAnimator().setAddDuration(10000);

                mAdapter = new RecyclerAdapter(apps);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
                recyclerView.computeHorizontalScrollExtent();
                recyclerView.setAdapter(mAdapter);
                getAllICONS();

            }
        }
        catch(Exception ignored) {}
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadFBNative() {
        NativeAdLayout nativeAdLayout = findViewById(R.id.native_ad_container);
if(MainActivity.indratech_toto_27640849_fb_native_id != null){

    com.facebook.ads.NativeAd nativeAd = new com.facebook.ads.NativeAd(this, MainActivity.indratech_toto_27640849_fb_native_id);
    NativeAdListener nativeAdListener = new NativeAdListener() {
        @Override
        public void onMediaDownloaded(Ad ad) {
        }

        @Override
        public void onError(Ad ad, AdError adError) {
            Log.w("AdLoader", MainActivity.indratech_toto_27640849_fb_native_id);
            Log.w("AdLoader", "onAdFailedToLoad" + adError.getErrorMessage());
        }

        @Override
        public void onAdLoaded(Ad ad) {
            if (nativeAd == null || nativeAd != ad) {
                return;
            }
            nativeAd.unregisterView();

            if ((!Config.vip_subscription && !Config.all_subscription)) {
                nativeAdLayout.setVisibility(View.VISIBLE);
            }
            LayoutInflater inflater = LayoutInflater.from(CPUCoolerActivity.this);
            LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_layout, nativeAdLayout, false);
            nativeAdLayout.addView(adView);

            LinearLayout adChoicesContainer = nativeAdLayout.findViewById(R.id.ad_choices_container);
            AdOptionsView adOptionsView = new AdOptionsView(CPUCoolerActivity.this, nativeAd, nativeAdLayout);
            adChoicesContainer.removeAllViews();
            adChoicesContainer.addView(adOptionsView, 0);

            com.facebook.ads.MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
            TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
            com.facebook.ads.MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
            TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
            TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
            TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
            Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

            nativeAdTitle.setText(nativeAd.getAdvertiserName());
            nativeAdBody.setText(nativeAd.getAdBodyText());
            nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
            nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
            sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(nativeAdTitle);
            clickableViews.add(nativeAdCallToAction);

            nativeAd.registerViewForInteraction(
                    adView, nativeAdMedia, nativeAdIcon, clickableViews);
        }

        @Override
        public void onAdClicked(Ad ad) {

        }

        @Override
        public void onLoggingImpression(Ad ad) {

        }
    };
    nativeAd.loadAd(
            nativeAd.buildLoadAdConfig()
                    .withAdListener(nativeAdListener)
                    .build());
}
    }

    private void refreshAd() {

        RelativeLayout frameLayout =
                findViewById(R.id.fl_adplaceholder);

        AdLoader adLoader = new AdLoader.Builder(this, MainActivity.indratech_toto_27640849_aad_native_id)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                        frameLayout.setVisibility(View.VISIBLE);
                        NativeAdView adView = (NativeAdView) getLayoutInflater()
                                .inflate(R.layout.ad_unified, null);
                        if ((!Config.vip_subscription && !Config.all_subscription))
                        {
                            populateUnifiedNativeAdView(nativeAd, adView);
                            frameLayout.removeAllViews();
                            frameLayout.addView(adView);
                        }
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .build();


        VideoOptions videoOptions = new VideoOptions.Builder()
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        adLoader.loadAd(new AdRequest.Builder()
                .build());
    }

    private void populateUnifiedNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }


        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!Config.vip_subscription && !Config.all_subscription && !Config.ads_subscription) {
            if (MainActivity.type.equals("ad")) {
                refreshAd();
            }else{
                loadFBNative();
            }
        }
    }

}
