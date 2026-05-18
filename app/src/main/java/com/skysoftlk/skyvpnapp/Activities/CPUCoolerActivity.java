package com.skysoftlk.skyvpnapp.Activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skysoftlk.skyvpnapp.Apps;
import com.skysoftlk.skyvpnapp.R;
import com.skysoftlk.skyvpnapp.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


public class CPUCoolerActivity extends NavigationActivity {
    TextView batterytemp, showmain, showsec, nooverheating;
    float temp;
    ImageView coolbutton, tempimg, ivtemping;
    RecyclerView recyclerView;
    RecyclerAdapter mAdapter;
    public static List<Apps> apps;
    List<Apps> apps2;
    int check = 0;

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

        try {
            recyclerView = findViewById(R.id.recycler_view);

            ivtemping = findViewById(R.id.iv_tempimg);
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

            coolbutton.setOnClickListener(v -> {
                LayoutInflater inflater = getLayoutInflater();
                @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.my_toast, null);

                TextView text = layout.findViewById(R.id.textView1);
                text.setText("Temperature of CPU is Already Normal.");

                Toast toast = new Toast(CPUCoolerActivity.this);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 70);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            });


            batterytemp = findViewById(R.id.batterytemp);

            if (!((System.currentTimeMillis() - getSharedPreferences("APPS_CONFIGS", Context.MODE_PRIVATE).getLong("COOLER_LAST_UPDATE", 0)) < 1200000)) {
                new Thread(() -> makeStabilityScanning(null)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(batteryReceiver);
        } catch (Exception e) {
        }
        super.onDestroy();

    }

    public void getAllICONS() {
        new Thread(() -> {
            PackageManager pm = getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            if (packages != null) {
                for (int k = 0; k < packages.size(); k++) {
                    String packageName = packages.get(k).packageName;

                    if (!packageName.equals(getPackageName())) {
                        try {
                            Apps app = new Apps();
                            ApplicationInfo a = packages.get(k);
                            
                            // Only add if it's a non-system app
                            if (((a.flags & ApplicationInfo.FLAG_SYSTEM) == 0)) {
                                app.setImage(pm.getApplicationIcon(a));
                                
                                // Randomize size for display if needed or read properly
                                app.setSize((new File(a.publicSourceDir).length() / 1000000 + 20) + "MB");

                                synchronized (apps) {
                                    if (check < 5) {
                                        check++;
                                        apps.add(app);
                                    } else {
                                        break;
                                    }
                                }
                            }

                        } catch (Exception e) {
                            Log.e("ERROR", "Error fetching icon: " + e.getMessage());
                        }
                    }
                }
                
                runOnUiThread(() -> {
                    if (apps != null && !apps.isEmpty()) {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void makeStabilityScanning(Intent intent) {
        try {
            if (intent == null)
                intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

            if (intent != null) {
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


                    coolbutton.setOnClickListener(v -> {
                        SharedPreferences sharedPreferences = getSharedPreferences("APPS_CONFIGS", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("COOLER_LAST_UPDATE", System.currentTimeMillis());
                        editor.apply();

                        Intent i = new Intent(CPUCoolerActivity.this, ScannerCPU.class);
                        startActivity(i);

                        final Handler handler = new Handler();
                        handler.postDelayed(() -> {
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
                        }, 2000);


                        coolbutton.setOnClickListener(v1 -> {
                            LayoutInflater inflater = getLayoutInflater();
                            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.my_toast, null);

                            TextView text = layout.findViewById(R.id.textView1);
                            text.setText("CPU Temperature is Already Normal.");

                            Toast toast = new Toast(CPUCoolerActivity.this);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 70);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();
                        });
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
        } catch (Exception ignored) {
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
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
