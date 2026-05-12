package com.skysoftlk.vpnapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skysoftlk.vpnapp.Config;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.skysoftlk.vpnapp.Apps;
import com.skysoftlk.vpnapp.CPUApplications_Scanning;
import com.skysoftlk.vpnapp.R;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.skyfishjy.library.RippleBackground;
import com.zys.brokenview.BrokenTouchListener;
import com.zys.brokenview.BrokenView;

import net.grandcentrix.tray.AppPreferences;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ScannerCPU extends BaseActivity {
    private static final String TAG = "ScannerCPU";


    ImageView scanner, img_animation, cpu, ivCompltecheck, shadowCpu;
    BrokenView brokenView;
    BrokenTouchListener listener;
    CPUApplications_Scanning mAdapter;
    RecyclerView recyclerView;
    List<Apps> app = null;
    PackageManager pm;
    List<ApplicationInfo> packages;
    TextView cooledcpu;
    RelativeLayout rel;
    InterstitialAd mInterstitialAd;
    com.facebook.ads.AdView facebookAdview;
    public com.facebook.ads.InterstitialAd facebookInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_c_p_u);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        if(MainActivity.type.equals("ad")) {
            AdView mAdMobAdView = findViewById(R.id.admob_adview);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdMobAdView.loadAd(adRequest);
        }else{
            facebookAdview = new com.facebook.ads.AdView(this,MainActivity.indratech_toto_27640849_fb_native_id, AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
            adContainer.addView(facebookAdview);
            facebookAdview.loadAd();

        }

        scanner = (ImageView) findViewById(R.id.scann);
        cpu = (ImageView) findViewById(R.id.cpu);
        cooledcpu = (TextView) findViewById(R.id.cpucooler);
        img_animation = (ImageView) findViewById(R.id.heart);
        rel = (RelativeLayout) findViewById(R.id.rel);
        ivCompltecheck = (ImageView) findViewById(R.id.iv_completecheck);
        shadowCpu = (ImageView) findViewById(R.id.shadowcpu);
        app = new ArrayList<>();

        if (getResources().getBoolean(R.bool.ads_switch) &&
                !Config.ads_subscription &&
                !Config.all_subscription &&
                !Config.vip_subscription) {

            if(MainActivity.type.equals("ad")) {

                AdRequest adRequest = new AdRequest.Builder().build();
                        InterstitialAd.load(ScannerCPU.this, MainActivity.admob_interstitial_id, adRequest,
                                new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                        // The mInterstitialAd reference will be null until
                                        // an ad is loaded.
                                        mInterstitialAd = interstitialAd;
                                        Log.i("INTERSTITIAL", "onAdLoaded");

                                        if (mInterstitialAd != null) {

                                            mInterstitialAd.show(ScannerCPU.this);

                                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                                @Override
                                                public void onAdDismissedFullScreenContent() {
                                                    // Called when fullscreen content is dismissed.
                                                    Log.d("TAG", "The ad was dismissed.");
                                                    Log.d("TESTAD", " dismissed update");
                                                }

                                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                                    // Called when fullscreen content failed to show.
                                                    Log.d("TAG", "The ad failed to show.");
                                                }

                                                @Override
                                                public void onAdShowedFullScreenContent() {
                                                    // Called when fullscreen content is shown.
                                                    // Make sure to set your reference to null so you don't
                                                    // show it a second time.
                                                    mInterstitialAd = null;
                                                    Log.d("TAG", "The ad was shown.");
                                                }
                                            });

                                        } else {
                                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                        }
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        // Handle the error
                                        Log.i("INTERSTITIAL", loadAdError.getMessage());
                                        mInterstitialAd = null;
                                    }
                                });
                    }else {
                        AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE);


                        AudienceNetworkAds.initialize(ScannerCPU.this);
                        com.facebook.ads.InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {

                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {

                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                Log.d("ADerror",adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                facebookInterstitialAd.show();
                            }

                            @Override
                            public void onAdClicked(Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {

                            }
                        };
                        facebookInterstitialAd = new com.facebook.ads.InterstitialAd(ScannerCPU.this,MainActivity.indratech_toto_27640849_fb_interstitial_id);
                        facebookInterstitialAd.loadAd(facebookInterstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());

                    }


                }


        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1500);
        rotate.setRepeatCount(3);
        rotate.setInterpolator(new LinearInterpolator());
        scanner.startAnimation(rotate);

        TranslateAnimation animation = new TranslateAnimation(0.0f, 1000.0f, 0.0f, 0.0f);
        animation.setDuration(5000);
        animation.setRepeatCount(0);
        animation.setInterpolator(new LinearInterpolator());

        animation.setFillAfter(true);

        img_animation.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                img_animation.setImageResource(0);
                img_animation.setBackgroundResource(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setItemAnimator(new SlideInLeftAnimator());

        mAdapter = new CPUApplications_Scanning(CPUCoolerActivity.apps);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        recyclerView.computeHorizontalScrollExtent();
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        try {
            final Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    add("Limit Brightness Upto 80%", 0);


                }
            }, 0);

            final Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    remove(0);
                    add("Decrease Device Performance", 1);


                }
            }, 900);

            final Handler handler3 = new Handler();
            handler3.postDelayed(new Runnable() {
                @Override
                public void run() {
                    remove(0);
                    add("Close All Battery Consuming Apps", 2);


                }
            }, 1800);

            final Handler handler4 = new Handler();
            handler4.postDelayed(new Runnable() {
                @Override
                public void run() {
                    remove(0);
                    add("Closes System Services like Bluetooth,Screen Rotation,Sync etc.", 3);


                }
            }, 2700);

            final Handler handler5 = new Handler();
            handler5.postDelayed(new Runnable() {
                @Override
                public void run() {
                    remove(0);
                    add("Closes System Services like Bluetooth,Screen Rotation,Sync etc.", 4);
                }
            }, 3700);

            final Handler handler6 = new Handler();
            handler6.postDelayed(new Runnable() {
                @Override
                public void run() {
                    remove(0);
                    add("Closes System Services like Bluetooth,Screen Rotation,Sync etc.", 5);
                }
            }, 4400);

            final Handler handler7 = new Handler();
            handler7.postDelayed(new Runnable() {
                @Override
                public void run() {
                    add("Closes System Services like Bluetooth,Screen Rotation,Sync etc.", 6);
                    remove(0);

                    final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
                    ImageView imageView = (ImageView) findViewById(R.id.centerImage);
                    rippleBackground.startRippleAnimation();

                    img_animation.setImageResource(0);
                    img_animation.setBackgroundResource(0);
                    cpu.setImageResource(R.drawable.ic_cooling_complete);
                    shadowCpu.setVisibility(View.GONE);

                    scanner.setVisibility(View.GONE);
                    ivCompltecheck.setImageResource(R.drawable.ic_cooling_complete_check);
                    ivCompltecheck.setVisibility(View.VISIBLE);
                    ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flipping);
                    anim.setTarget(scanner);
                    anim.setDuration(3000);
                    anim.start();

                    rel.setVisibility(View.GONE);

                    cooledcpu.setText("Cooled CPU to 25.3°C");
                    anim.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            img_animation.setImageResource(0);
                            img_animation.setBackgroundResource(0);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            rippleBackground.stopRippleAnimation();


                            AppPreferences preferences = new AppPreferences(ScannerCPU.this);


                            Log.d(TAG, "onAnimationEnd: preferences.getBoolean(\"admob\",false)" + preferences.getBoolean("admob", false));
                            if(MainActivity.type.equals("ad")) {
                                if (preferences.getBoolean("admob", false))
                                    if (mInterstitialAd != null)
                                        mInterstitialAd.show(ScannerCPU.this);
                            }else {
                                facebookInterstitialAd.show();
                            }

                            final Handler handler6 = new Handler();
                            handler6.postDelayed(new Runnable() {
                                @Override
                                public void run() {


                                    finish();

                                }
                            }, 1000);

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                }
            }, 5500);


        } catch (Exception e) {

        }

    }

    public void add(String text, int position) {


        try {
            mAdapter.notifyItemInserted(position);
        } catch (Exception e) {

        }
    }

    public void remove(int position) {

        mAdapter.notifyItemRemoved(position);
        try {
            CPUCoolerActivity.apps.remove(position);
        } catch (Exception e) {

        }
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

    }
}