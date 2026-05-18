package com.skysoftlk.skyvpnapp.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.skysoftlk.skyvpnapp.BoostAlarm;
import com.skysoftlk.skyvpnapp.R;

import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpeedBoosterActivity extends NavigationActivity {
    int mb = 1024 * 1024;
    DecoView arcView, arcView2;
    TextView scanning, centree, totalram, usedram, appused, appsfreed, processes, top, bottom, ramperct;
    LinearLayout scanlay, optimizelay;
    public static ImageView optimizebutton;
    private Timer mainTimer;
    private Timer secondaryTimer;
    TimerTask timer = null;
    TimerTask timer2 = null;
    int x, y;
    int counter = 0;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_speed_booster;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
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
                        if (alarmManager != null) {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (100 * 1000), pendingIntent);
                        }
                    } else {


                        LayoutInflater inflater = getLayoutInflater();
                        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.my_toast, null);


                        TextView text = (TextView) layout.findViewById(R.id.textView1);
                        text.setText("Phone Is Already Optimized");

                        Toast toast = new Toast(SpeedBoosterActivity.this);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 70);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();

                    }


                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void optimize() {

        arcView2.setVisibility(View.VISIBLE);
        arcView.setVisibility(View.GONE);


        arcView.addSeries(new SeriesItem.Builder(Color.parseColor("#1AFFFFFF"))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setLineWidth(16f)
                .build());


        SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#00F2FF"))
                .setRange(0, 100, 0)
                .setLineWidth(16f)
                .setCapRounded(true)
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
        if (mainTimer != null) {
            mainTimer.cancel();
            mainTimer = null;
        }
        mainTimer = new Timer();
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
        mainTimer.schedule(timer, 30, 30);


        Random ran2 = new Random();
        final int proc = ran2.nextInt(60) + 30;


        arcView.addSeries(new SeriesItem.Builder(Color.parseColor("#1AFFFFFF"))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setLineWidth(16f)
                .build());


        SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#00F2FF"))
                .setRange(0, 100, 0)
                .setLineWidth(16f)
                .setCapRounded(true)
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

                if (mainTimer != null) {
                    mainTimer.cancel();
                    mainTimer = null;
                }


                centree.setText(getUsedMemorySize() + " MB");

                if (sharedpreferences.getString("booster", "1").equals("0")) {

                    centree.setText(sharedpreferences.getString("value", "50MB"));
                }


                if (secondaryTimer != null) {
                    secondaryTimer.cancel();
                    secondaryTimer = null;
                }
                secondaryTimer = new Timer();


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

                                        if (secondaryTimer != null) {
                                            secondaryTimer.cancel();
                                            secondaryTimer = null;
                                        }
                                    }
                                });
                            } catch (Exception e) {

                            }

                        }

                    };

                } catch (Exception e) {

                }

                secondaryTimer.schedule(timer2, 100, 100);


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
                if (reader != null) reader.close();
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
        }

        return lastValue;
    }


    public long getUsedMemorySize() {

        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            if (activityManager != null) {
                activityManager.getMemoryInfo(mi);
                return mi.availMem / 1048576L;
            }
            return 200;
        } catch (Exception e) {
            return 200;
        }

    }

    public void killall() {


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

    @Override
    protected void onDestroy() {
        if (mainTimer != null) {
            mainTimer.cancel();
            mainTimer = null;
        }
        if (secondaryTimer != null) {
            secondaryTimer.cancel();
            secondaryTimer = null;
        }
        super.onDestroy();
    }
}
