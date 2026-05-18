package com.skysoftlk.skyvpnapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.Icon;
import com.skysoftlk.skyvpnapp.R;

import me.itangqi.waveloadingview.WaveLoadingView;

public class BatteryActivity extends NavigationActivity {
    WaveLoadingView mWaveLoadingView;
    ImageView powersaving, ultrasaving, normal;
    TextView hourn, minutes, hourp, minutep, houru, minutesu, hourmain, minutesmain;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    private LinearLayout tools, myPage;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

            mWaveLoadingView.setProgressValue(level);
            mWaveLoadingView.setCenterTitle(level + "%");

            if (level <= 5) {
                hourn.setText("0");
                minutes.setText("15");
                hourp.setText("2");
                minutep.setText("25");
                houru.setText("3");
                minutesu.setText("55");

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText("0");
                    minutesmain.setText("15");
                } else if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText("2");
                    minutesmain.setText("25");
                }
            } else if (level <= 10) {
                hourn.setText("0");
                minutes.setText("30");
                hourp.setText("3");
                minutep.setText("5");
                houru.setText("6");
                minutesu.setText("0");

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText("0");
                    minutesmain.setText("30");
                } else if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText("3");
                    minutesmain.setText("5");
                }
            } else if (level <= 15) {
                hourn.setText("0");
                minutes.setText("45");
                hourp.setText("3");
                minutep.setText("50");
                houru.setText("8");
                minutesu.setText("25");

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText("0");
                    minutesmain.setText("45");
                } else if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText("3");
                    minutesmain.setText("50");
                }
            } else if (level <= 25) {
                hourn.setText("1");
                minutes.setText("30");
                hourp.setText("4");
                minutep.setText("45");
                houru.setText("12");
                minutesu.setText("55");

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText("1");
                    minutesmain.setText("30");
                } else if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText("4");
                    minutesmain.setText("45");
                }
            } else if (level <= 35) {
                hourn.setText("2");
                minutes.setText("20");
                hourp.setText("6");
                minutep.setText("2");
                houru.setText("19");
                minutesu.setText("2");

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText("2");
                    minutesmain.setText("20");
                } else if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText("6");
                    minutesmain.setText("2");
                }
            } else if (level <= 50) {
                hourn.setText("5");
                minutes.setText("20");
                hourp.setText("9");
                minutep.setText("25");
                houru.setText("22");
                minutesu.setText("0");

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText("5");
                    minutesmain.setText("20");
                } else if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText("9");
                    minutesmain.setText("20");
                }
            } else if (level <= 65) {
                hourn.setText("7");
                minutes.setText("30");
                hourp.setText("11");
                minutep.setText("1");
                houru.setText("28");
                minutesu.setText("15");

                mWaveLoadingView.setCenterTitleColor(Color.WHITE);

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText("7");
                    minutesmain.setText("30");
                } else if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText("11");
                    minutesmain.setText("1");
                }
            } else if (level <= 75) {
                hourn.setText("9");
                minutes.setText("10");
                hourp.setText("14");
                minutep.setText("25");
                houru.setText("30");
                minutesu.setText("55");
                mWaveLoadingView.setCenterTitleColor(Color.WHITE);

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText("9");
                    minutesmain.setText("10");
                } else if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText("14");
                    minutesmain.setText("25");
                }
            } else if (level <= 85) {
                hourn.setText("14");
                minutes.setText("15");
                hourp.setText("17");
                minutep.setText("10");
                houru.setText("38");
                minutesu.setText("5");
                mWaveLoadingView.setCenterTitleColor(Color.WHITE);

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText("14");
                    minutesmain.setText("15");
                } else if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText("17");
                    minutesmain.setText("10");
                }
            } else {
                hourn.setText("20");
                minutes.setText("45");
                hourp.setText("30");
                minutep.setText("0");
                houru.setText("60");
                minutesu.setText("55");
                mWaveLoadingView.setCenterTitleColor(Color.WHITE);

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText("20");
                    minutesmain.setText("45");
                } else if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText("30");
                    minutesmain.setText("0");
                }
            }
        }
    };

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_battery;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWaveLoadingView = findViewById(R.id.waveView);
        powersaving = findViewById(R.id.powersaving);
        ultrasaving = findViewById(R.id.ultra);
        normal = findViewById(R.id.normal);
        hourn = findViewById(R.id.hourn);
        minutes = findViewById(R.id.minutes);
        hourp = findViewById(R.id.hourp);
        minutep = findViewById(R.id.minutep);
        houru = findViewById(R.id.houru);
        minutesu = findViewById(R.id.minutesu);
        hourmain = findViewById(R.id.hourmain);
        minutesmain = findViewById(R.id.minutesmain);
        sharedpreferences = getSharedPreferences("was", MODE_PRIVATE);

        try {
            powersaving.setOnClickListener(v -> {
                Intent i = new Intent(BatteryActivity.this, PopUp_SavingPower.class);
                i.putExtra("hour", hourp.getText());
                i.putExtra("minutes", minutep.getText());
                i.putExtra("minutesnormal", minutes.getText());
                i.putExtra("hournormal", hourn.getText());
                startActivity(i);
            });

            ultrasaving.setOnClickListener(v -> {
                Intent i = new Intent(BatteryActivity.this, UPopUp.class);
                i.putExtra("hour", houru.getText());
                i.putExtra("minutes", minutesu.getText());
                i.putExtra("minutesnormal", minutes.getText());
                i.putExtra("hournormal", hourn.getText());
                startActivity(i);
            });

            normal.setOnClickListener(v -> {
                Intent i = new Intent(BatteryActivity.this, NormalMode.class);
                startActivity(i);
            });

            mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
            mWaveLoadingView.setCenterTitleColor(Color.WHITE);
            mWaveLoadingView.setBottomTitleColor(Color.WHITE);
            mWaveLoadingView.setAmplitudeRatio(30);
            mWaveLoadingView.setWaveColor(Color.parseColor("#00F2FF")); // neon_blue
            mWaveLoadingView.setTopTitleStrokeWidth(3);
            mWaveLoadingView.setAnimDuration(3000);
            mWaveLoadingView.startAnimation();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            unregisterReceiver(mBatInfoReceiver);
        } catch (Exception e) {}
    }

    public void showint() {
        new FancyAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.battery_saver))
                .setBackgroundColor(Color.parseColor("#0c7944"))
                .setMessage(getResources().getString(R.string.batterysavertxt))
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))
                .setPositiveBtnText("Ok")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))
                .setNegativeBtnText("Cancel")
                .setAnimation(Animation.POP)
                .isCancellable(false)
                .setIcon(R.drawable.ic_crown, Icon.Visible)
                .OnPositiveClicked(() -> {
                    SharedPreferences saveint = getSharedPreferences("config", MODE_PRIVATE);
                    saveint.edit().putBoolean("batterysaverint", true).apply();
                })
                .OnNegativeClicked(() -> {
                    SharedPreferences saveint = getSharedPreferences("config", MODE_PRIVATE);
                    saveint.edit().putBoolean("batterysaverint", true).apply();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
