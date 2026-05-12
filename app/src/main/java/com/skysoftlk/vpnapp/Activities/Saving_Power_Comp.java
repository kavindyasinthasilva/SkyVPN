package com.skysoftlk.vpnapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

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

public class Saving_Power_Comp extends AppCompatActivity {
    DecoView arcView;
    TextView ist,sec,thir,fou,completion;
    ImageView istpic,secpic,thirpic,foupic;
    int check=0;
    InterstitialAd mInterstitialAd;

    public com.facebook.ads.InterstitialAd facebookInterstitialAd;

    View viewOne, viewTwo, viewThree, viewFour, viewFive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_power_comp);
        ist=(TextView) findViewById(R.id.ist);
        sec=(TextView) findViewById(R.id.sec);
        thir=(TextView) findViewById(R.id.thi);
        fou=(TextView) findViewById(R.id.fou);
        istpic=(ImageView) findViewById(R.id.istpic);
        secpic=(ImageView) findViewById(R.id.secpic);
        thirpic=(ImageView) findViewById(R.id.thipic);
        foupic=(ImageView) findViewById(R.id.foupic);
        completion=(TextView) findViewById(R.id.completion);

        viewOne= findViewById(R.id.viewone);
        viewTwo= findViewById(R.id.viewtwo);
        viewThree= findViewById(R.id.viewthree);
        viewFour= findViewById(R.id.viewfour);

        arcView = (DecoView) findViewById(R.id.dynamicArcView2);

        //if not admob
        if(!MainActivity.type.equals("ad")) {
            if (getResources().getBoolean(R.bool.ads_switch) &&
                    !Config.ads_subscription &&
                    !Config.all_subscription &&
                    !Config.vip_subscription) {
                if (!MainActivity.type.equals("ad")) {
                    AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE);


                    AudienceNetworkAds.initialize(Saving_Power_Comp.this);
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
                    facebookInterstitialAd = new com.facebook.ads.InterstitialAd(Saving_Power_Comp.this, MainActivity.indratech_toto_27640849_fb_interstitial_id);
                    facebookInterstitialAd.loadAd(facebookInterstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());

                }
            }
        }






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
                completion.setText(i+"%");

                if(v1>=10 && v1<50)
                {
                    ist.setTextColor(Color.parseColor("#4e5457"));
                    istpic.setImageResource(R.drawable.ic_blue_dot);



                }
                else if(v1>=50 && v1<75)
                {
                    sec.setTextColor(Color.parseColor("#4e5457"));
                    secpic.setImageResource(R.drawable.ic_blue_dot);

                }
                else if(v1>=75 && v1<90)
                {
                    thir.setTextColor(Color.parseColor("#4e5457"));
                    thirpic.setImageResource(R.drawable.ic_blue_dot);

                }
                else if(v1>=90 && v1<=100)
                {
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

                }).build());

        arcView.addEvent(new DecoEvent.Builder(100).setIndex(series1Index2).setDelay(1000).setListener(new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent decoEvent) {



            }

            @Override
            public void onEventEnd(DecoEvent decoEvent) {







                AppPreferences preferences = new AppPreferences(Saving_Power_Comp.this);
                if(MainActivity.type.equals("ad")) {
                    if (preferences.getBoolean("admob", false)) {
                        AdRequest adRequest = new AdRequest.Builder().build();
                        InterstitialAd.load(Saving_Power_Comp.this, MainActivity.admob_interstitial_id, adRequest,
                                new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                        // The mInterstitialAd reference will be null until
                                        // an ad is loaded.
                                        mInterstitialAd = interstitialAd;
                                        Log.i("INTERSTITIAL", "onAdLoaded");

                                        if (mInterstitialAd != null) {

                                            mInterstitialAd.show(Saving_Power_Comp.this);

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
                    }
                }else {
                    facebookInterstitialAd.show();
                }

                youDesirePermissionCode(Saving_Power_Comp.this);


                closesall();

                check=1;



            }
        }).build());
    }

    public void closesall()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }
        }
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }










        ContentResolver.setMasterSyncAutomatically(false);
    }

    public static void setAutoOrientationEnabled(Context context, boolean enabled) {
        Settings.System.putInt( context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }

    @Override
    public void onBackPressed() {

    }

    public void youDesirePermissionCode(Activity context){
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;


        }
        if (permission) {

            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 30);
            setAutoOrientationEnabled(context, false);

            finish();
        }  else {
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
        if (requestCode == 1 && Settings.System.canWrite(this)){
            Log.d("TAG", "CODE_WRITE_SETTINGS_PERMISSION success");




            Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 30);
            setAutoOrientationEnabled(this, false);

            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            Toast.makeText(getApplicationContext(),"onRequestPermissionsResult",Toast.LENGTH_LONG).show();

            Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 30);
            setAutoOrientationEnabled(this, false);

            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(check==1)
        {
            try
            {
                Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 30);
                setAutoOrientationEnabled(this, false);
            }
            catch(Exception e)
            {
                finish();
            }
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
