package com.skysoftlk.vpnapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.skysoftlk.vpnapp.Config;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.skysoftlk.vpnapp.R;

import net.grandcentrix.tray.AppPreferences;

public class NormalMode extends AppCompatActivity {
    DecoView arcView;
    TextView ist, sec, thir, fou, completion;
    ImageView istpic, secpic, thirpic, foupic;
    View one, two, three, four;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    int check = 0;
    InterstitialAd mInterstitialAd;
    public com.facebook.ads.InterstitialAd facebookInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_mode);
        ist = (TextView) findViewById(R.id.ist);
        sec = (TextView) findViewById(R.id.sec);
        thir = (TextView) findViewById(R.id.thi);
        fou = (TextView) findViewById(R.id.fou);
        istpic = (ImageView) findViewById(R.id.istpic);
        secpic = (ImageView) findViewById(R.id.secpic);
        thirpic = (ImageView) findViewById(R.id.thipic);
        foupic = (ImageView) findViewById(R.id.foupic);
        completion = (TextView) findViewById(R.id.completion);

        one = findViewById(R.id.view_one);
        two = findViewById(R.id.view_two);
        three = findViewById(R.id.view_three);
        four = findViewById(R.id.view_four);


        sharedpreferences = getSharedPreferences("was", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        if (getResources().getBoolean(R.bool.ads_switch) &&
                !Config.ads_subscription &&
                !Config.all_subscription &&
                !Config.vip_subscription) {
                if (MainActivity.type.equals("ad")) {

                    AdRequest adRequest = new AdRequest.Builder().build();
                    InterstitialAd.load(NormalMode.this, MainActivity.admob_interstitial_id, adRequest,
                            new InterstitialAdLoadCallback() {
                                @Override
                                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                    // The mInterstitialAd reference will be null until
                                    // an ad is loaded.
                                    mInterstitialAd = interstitialAd;
                                    Log.i("INTERSTITIAL", "onAdLoaded");

                                    if (mInterstitialAd != null) {

                                        mInterstitialAd.show(NormalMode.this);

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
                } else {
                    AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE);


                    AudienceNetworkAds.initialize(NormalMode.this);
                    com.facebook.ads.InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {
                            Log.d("ADerror", adError.getErrorMessage());
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
                    facebookInterstitialAd = new com.facebook.ads.InterstitialAd(NormalMode.this, MainActivity.indratech_toto_27640849_fb_interstitial_id);
                    facebookInterstitialAd.loadAd(facebookInterstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());

                }
        }


        arcView = (DecoView) findViewById(R.id.dynamicArcView2);


        arcView.addSeries(new SeriesItem.Builder(Color.parseColor("#FFFFFF"))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(12f)
                .build());


        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#FFFFFF"))
                .setRange(0, 100, 0)
                .setLineWidth(20f)
                .build();

        SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#2a7af7"))
                .setRange(0, 100, 0)
                .setLineWidth(22f)
                .build();


        int series1Index2 = arcView.addSeries(seriesItem2);

        seriesItem2.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float v, float v1) {

                Float obj = new Float(v1);
                int i = obj.intValue();
                completion.setText(i + "%");

                if (v1 >= 10 && v1 < 50) {
                    ist.setTextColor(Color.parseColor("#4e5457"));
                    istpic.setImageResource(R.drawable.ic_blue_dot);


                } else if (v1 >= 50 && v1 < 75) {
                    sec.setTextColor(Color.parseColor("#4e5457"));
                    secpic.setImageResource(R.drawable.ic_blue_dot);

                } else if (v1 >= 75 && v1 < 90) {
                    thir.setTextColor(Color.parseColor("#4e5457"));
                    thirpic.setImageResource(R.drawable.ic_blue_dot);

                } else if (v1 >= 90 && v1 <= 100) {
                    fou.setTextColor(Color.parseColor("#4e5457"));
                    foupic.setImageResource(R.drawable.ic_blue_dot);

                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float v) {

            }
        });

        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(0)
                .setDuration(0)
                .setListener(new DecoEvent.ExecuteEventListener() {
                    @Override
                    public void onEventStart(DecoEvent decoEvent) {


                    }

                    @Override
                    public void onEventEnd(DecoEvent decoEvent) {

                    }

                })
                .build());

        arcView.addEvent(new DecoEvent.Builder(100).setIndex(series1Index2).setDelay(1000).setListener(new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent decoEvent) {


            }

            @Override
            public void onEventEnd(DecoEvent decoEvent) {
                try {

                    AppPreferences preferences = new AppPreferences(NormalMode.this);
                    if(MainActivity.type.equals("ad")) {
                        if (preferences.getBoolean("admob", false))
                            if(mInterstitialAd != null)
                                mInterstitialAd.show(NormalMode.this);
                    }else {
                        facebookInterstitialAd.show();
                    }

                } catch (Exception e) {

                }


                check = 1;
                youDesirePermissionCode(NormalMode.this);


                editor.putString("mode", "0");
                editor.apply();

            }
        }).build());
    }

    public void enablesall() {


        Saving_Power_Comp.setAutoOrientationEnabled(getApplicationContext(), true);

        Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);

        ContentResolver.setMasterSyncAutomatically(true);

    }

    @Override
    public void onBackPressed() {

    }


    public void youDesirePermissionCode(Activity context) {


        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;


        }
        if (permission) {

            enablesall();

            finish();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, 1);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS}, 1);
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && Settings.System.canWrite(this)) {
            Log.d("TAG", "CODE_WRITE_SETTINGS_PERMISSION success");


            enablesall();

            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            enablesall();

            finish();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (check == 1) {
            try {
                enablesall();
            } catch (Exception e) {
                finish();
            }
            finish();
        }
    }
}

