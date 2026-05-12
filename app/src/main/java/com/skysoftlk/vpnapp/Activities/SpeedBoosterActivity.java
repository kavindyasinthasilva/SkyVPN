package com.skysoftlk.vpnapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
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
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.skysoftlk.vpnapp.BoostAlarm;
import com.skysoftlk.vpnapp.Config;
import com.skysoftlk.vpnapp.R;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import net.grandcentrix.tray.AppPreferences;

import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpeedBoosterActivity extends AppCompatActivity {
    int mb = 1024 * 1024;
    DecoView arcView, arcView2;
    TextView scanning, centree, totalram, usedram, appused, appsfreed, processes, top, bottom, ramperct;
    LinearLayout scanlay, optimizelay;
    public static ImageView optimizebutton;
    TimerTask timer = null;
    TimerTask timer2 = null;
    int x, y;
    int counter = 0;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private LinearLayout tools, myPage;
    private NativeAd nativeAd;
    com.facebook.ads.AdView facebookAdview;

    InterstitialAd mInterstitialAd;
    public com.facebook.ads.InterstitialAd facebookInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_booster);
        // Initialize the Mobile Ads SDK
        Toolbar toolbar = findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(R.string.speed_booster);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        if (MainActivity.type.equals("ad")) {
            AdView mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
            AdRequest adRequest2 = new AdRequest.Builder().build();
//            mAdMobAdView.setAdUnitId(MainActivity.indratech_toto_27640849_ad_banner_id);
            mAdMobAdView.loadAd(adRequest2);
        } else {
            loadFBNative();
        }
        //checkshowint();
        arcView = (DecoView) findViewById(R.id.dynamicArcView2);
        arcView2 = (DecoView) findViewById(R.id.dynamicArcView3);

        arcView2.setVisibility(View.GONE);
        arcView.setVisibility(View.VISIBLE);

        scanning = findViewById(R.id.scanning);
        scanlay = findViewById(R.id.scanlay);
        optimizelay = findViewById(R.id.optimizelay);
        optimizebutton = findViewById(R.id.optbutton);
        centree = findViewById(R.id.centree);
        totalram = findViewById(R.id.totalram);
        usedram = findViewById(R.id.usedram);
        appsfreed = findViewById(R.id.appsfreed);
        appused = findViewById(R.id.appsused);
        processes = findViewById(R.id.processes);
        top = findViewById(R.id.top);
        bottom = findViewById(R.id.bottom);
        ramperct = findViewById(R.id.ramperct);
        sharedpreferences = getSharedPreferences("tvpn", Context.MODE_PRIVATE);


        try {
            Random ran3 = new Random();
            ramperct.setText(ran3.nextInt(60) + 40 + "%");


            optimizebutton.setBackgroundResource(0);
            optimizebutton.setImageResource(0);
            optimizebutton.setImageResource(R.drawable.ic_on_off);

            if (sharedpreferences.getString("booster", "1").equals("0")) {
                optimizebutton.setImageResource(0);
                optimizebutton.setImageResource(R.drawable.n_bt);

                centree.setText(sharedpreferences.getString("value", "50MB"));

            }

            start();

            optimizebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (sharedpreferences.getString("booster", "1").equals("1")) {
                        optimize();

                        editor = sharedpreferences.edit();
                        editor.putString("booster", "0");
                        editor.apply();

                        SharedPreferences sharedPreferences = getSharedPreferences("APPS_CONFIGS", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("BOOSTER_LAST_UPDATE", System.currentTimeMillis());
                        editor.apply();

                        Intent intent = new Intent(SpeedBoosterActivity.this, BoostAlarm.class);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(SpeedBoosterActivity.this, 0,
                                intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (100 * 1000), pendingIntent);
                    } else {


                        LayoutInflater inflater = getLayoutInflater();
                        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.my_toast, null);


                        TextView text = (TextView) layout.findViewById(R.id.textView1);
                        text.setText("Phone Is Aleady Optimized");

                        Toast toast = new Toast(SpeedBoosterActivity.this);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 70);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();

                    }


                }
            });


        } catch (Exception e) {

        }
    }

    public void optimize() {

        arcView2.setVisibility(View.VISIBLE);
        arcView.setVisibility(View.GONE);


        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 0)
                .setInterpolator(new AccelerateInterpolator())
                .build());


        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#00000000"))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#ffffff"))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();


        int series1Index2 = arcView2.addSeries(seriesItem2);

        arcView2.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(500)
                .setDuration(2000)
                .setListener(new DecoEvent.ExecuteEventListener() {
                    @Override
                    public void onEventStart(DecoEvent decoEvent) {
                        bottom.setText("");
                        top.setText("");
                        centree.setText("Optimizing...");
                    }

                    @Override
                    public void onEventEnd(DecoEvent decoEvent) {

                    }
                })
                .build());

        arcView2.addEvent(new DecoEvent.Builder(25).setIndex(series1Index2).setDelay(2000).setListener(new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent decoEvent) {
                bottom.setText("");
                top.setText("");
                centree.setText("Optimizing...");
            }

            @Override
            public void onEventEnd(DecoEvent decoEvent) {
                AppPreferences preferences = new AppPreferences(SpeedBoosterActivity.this);
                if (MainActivity.type.equals("ad")) {
                    if (preferences.getBoolean("admob", false))
                        if (mInterstitialAd != null)
                            mInterstitialAd.show(SpeedBoosterActivity.this);
                } else {
                    facebookInterstitialAd.show();
                }

                bottom.setText("Found");
                top.setText("Storage");
                Random ran3 = new Random();
                ramperct.setText(ran3.nextInt(40) + 20 + "%");
            }
        }).build());

        ImageView img_animation = findViewById(R.id.waves);

        TranslateAnimation animation = new TranslateAnimation(0.0f, 1000.0f, 0.0f, 0.0f);
        animation.setDuration(5000);
        animation.setRepeatCount(0);
        animation.setInterpolator(new LinearInterpolator());

        animation.setFillAfter(true);

        img_animation.startAnimation(animation);

        int counter = 0;
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                scanlay.setVisibility(View.VISIBLE);
                optimizelay.setVisibility(View.GONE);
                scanning.setText("SCANNING...");
                killall();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                scanlay.setVisibility(View.GONE);
                optimizelay.setVisibility(View.VISIBLE);

                optimizebutton.setImageResource(R.drawable.n_bt);


                Random ran = new Random();
                x = ran.nextInt(100) + 30;

                Random ran2 = new Random();
                int proc = ran2.nextInt(10) + 5;

                centree.setText(getUsedMemorySize() - x + " MB");

                editor = sharedpreferences.edit();
                editor.putString("value", getUsedMemorySize() - x + " MB");
                editor.apply();

                Log.e("used mem", getUsedMemorySize() + " MB");
                Log.e("used mem", getTotalRAM());

                totalram.setText(getTotalRAM());
                usedram.setText(getUsedMemorySize() - x + " MB/ ");

                appsfreed.setText(getTotalRAM());
                appused.setText(Math.abs(getUsedMemorySize() - x - 30) + " MB/ ");

                processes.setText(y - proc + "");


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    public void start() {


        final Timer t = new Timer();
        timer = new TimerTask() {

            @Override
            public void run() {

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            counter++;
                            centree.setText(counter + "MB");
                        }
                    });
                } catch (Exception e) {

                }


            }

        };
        t.schedule(timer, 30, 30);


        Random ran2 = new Random();
        final int proc = ran2.nextInt(60) + 30;


        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 0)
                .setInterpolator(new AccelerateInterpolator())
                .build());


        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#00000000"))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#ffffff"))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();


        int series1Index2 = arcView.addSeries(seriesItem2);

        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(0)
                .setDuration(600)
                .build());


        arcView.addEvent(new DecoEvent.Builder(proc).setIndex(series1Index2).setDelay(2000).setListener(new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent decoEvent) {


            }

            @Override
            public void onEventEnd(DecoEvent decoEvent) {

                t.cancel();
                timer.cancel();
                t.purge();


                centree.setText(getUsedMemorySize() + " MB");

                if (sharedpreferences.getString("booster", "1").equals("0")) {

                    centree.setText(sharedpreferences.getString("value", "50MB"));
                }


                final Timer t = new Timer();
                final Timer t2 = new Timer();


                try {

                    timer2 = new TimerTask() {

                        @Override
                        public void run() {

                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        centree.setText(getUsedMemorySize() + " MB");

                                        if (sharedpreferences.getString("booster", "1").equals("0")) {

                                            centree.setText(sharedpreferences.getString("value", "50MB"));
                                        }

                                        t2.cancel();
                                        timer2.cancel();
                                        t2.purge();
                                    }
                                });
                            } catch (Exception e) {

                            }

                        }

                    };

                } catch (Exception e) {

                }

                t2.schedule(timer2, 100, 100);


            }
        }).build());

        Log.e("used mem", getUsedMemorySize() + " MB");
        Log.e("used mem", getTotalRAM());

        totalram.setText(getTotalRAM());
        usedram.setText(getUsedMemorySize() + " MB/ ");
        appsfreed.setText(getTotalRAM());
        appused.setText(getUsedMemorySize() - x - 30 + " MB/ ");

        Random ran = new Random();
        y = ran.nextInt(50) + 15;

        processes.setText(y + "");


    }


    public String getTotalRAM() {

        RandomAccessFile reader = null;
        String load = null;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double totRam = 0;
        String lastValue = "";
        try {
            try {
                reader = new RandomAccessFile("/proc/meminfo", "r");
                load = reader.readLine();
            } catch (Exception e) {

            }


            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);

            }
            try {
                reader.close();
            } catch (Exception e) {

            }

            totRam = Double.parseDouble(value);


            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
            double tb = totRam / 1073741824.0;

            if (tb > 1) {
                lastValue = twoDecimalForm.format(tb).concat(" TB");
            } else if (gb > 1) {
                lastValue = twoDecimalForm.format(gb).concat(" GB");
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb).concat(" MB");
            } else {
                lastValue = twoDecimalForm.format(totRam).concat(" KB");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return lastValue;
    }


    public long getUsedMemorySize() {

        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long availableMegs = mi.availMem / 1048576L;

            return availableMegs;
        } catch (Exception e) {
            return 200;
        }

    }

    public void killall() {


    }


    public void checkshowint() {
        boolean strPref = false;
        SharedPreferences shf = getSharedPreferences("config", MODE_PRIVATE);
        strPref = shf.getBoolean("speedint", false);

        if (strPref) {

        } else {
            showint();
        }
    }

    public void showint() {
        new FancyAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.speed_booster))
                .setBackgroundColor(Color.parseColor("#0c7944"))
                .setMessage(getResources().getString(R.string.speedtxt))
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))
                .setPositiveBtnText("Ok")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))
                .setNegativeBtnText("Cancel")
                .setAnimation(com.shashank.sony.fancydialoglib.Animation.POP)
                .isCancellable(false)
                .setIcon(R.drawable.booster, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences saveint = getSharedPreferences("config", MODE_PRIVATE);
                        saveint.edit().putBoolean("speedint", true).apply();

                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences saveint = getSharedPreferences("config", MODE_PRIVATE);
                        saveint.edit().putBoolean("speedint", true).apply();

                    }
                })

                .build();
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
        if (MainActivity.indratech_toto_27640849_fb_native_id != null) {

            com.facebook.ads.NativeAd nativeAd = new com.facebook.ads.NativeAd(this, MainActivity.indratech_toto_27640849_fb_native_id);
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, AdError adError) {

                    if (MainActivity.indratech_toto_27640849_fb_native_id != null)
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
                    LayoutInflater inflater = LayoutInflater.from(SpeedBoosterActivity.this);
                    LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_layout, nativeAdLayout, false);
                    nativeAdLayout.addView(adView);

                    LinearLayout adChoicesContainer = nativeAdLayout.findViewById(R.id.ad_choices_container);
                    AdOptionsView adOptionsView = new AdOptionsView(SpeedBoosterActivity.this, nativeAd, nativeAdLayout);
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
                        if ((!Config.vip_subscription && !Config.all_subscription)) {
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
            } else {
                loadFBNative();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        if (facebookAdview != null) {
            facebookAdview.destroy();
        }
        super.onDestroy();
    }
}
