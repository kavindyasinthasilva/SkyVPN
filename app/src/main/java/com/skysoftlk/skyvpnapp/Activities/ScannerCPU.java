package com.skysoftlk.skyvpnapp.Activities;

import androidx.appcompat.app.ActionBar;
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
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skysoftlk.skyvpnapp.Apps;
import com.skysoftlk.skyvpnapp.CPUApplications_Scanning;
import com.skysoftlk.skyvpnapp.R;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ScannerCPU extends BaseActivity {
    private static final String TAG = "ScannerCPU";

    ImageView scanner, img_animation, cpu, ivCompltecheck, shadowCpu;
    CPUApplications_Scanning mAdapter;
    RecyclerView recyclerView;
    List<Apps> app = null;
    PackageManager pm;
    List<ApplicationInfo> packages;
    TextView cooledcpu;
    RelativeLayout rel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_c_p_u);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);

            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
            upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }

        scanner = (ImageView) findViewById(R.id.scann);
        cpu = (ImageView) findViewById(R.id.cpu);
        cooledcpu = (TextView) findViewById(R.id.cpucooler);
        img_animation = (ImageView) findViewById(R.id.heart);
        rel = (RelativeLayout) findViewById(R.id.rel);
        ivCompltecheck = (ImageView) findViewById(R.id.iv_completecheck);
        shadowCpu = (ImageView) findViewById(R.id.shadowcpu);
        app = new ArrayList<>();

        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1500);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        scanner.startAnimation(rotate);

        img_animation.setVisibility(View.VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, -500.0f, 500.0f);
        animation.setDuration(3000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setInterpolator(new LinearInterpolator());
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
                    add("Optimizing CPU Performance...", 6);
                    remove(0);

                    final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
                    rippleBackground.startRippleAnimation();

                    img_animation.clearAnimation();
                    img_animation.setVisibility(View.GONE);
                    
                    cpu.setImageResource(R.drawable.ic_cooling_complete);
                    cpu.setColorFilter(getResources().getColor(R.color.neon_green), PorterDuff.Mode.SRC_ATOP);
                    shadowCpu.setVisibility(View.GONE);

                    scanner.clearAnimation();
                    scanner.setVisibility(View.GONE);
                    ivCompltecheck.setVisibility(View.VISIBLE);
                    
                    ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flipping);
                    anim.setTarget(ivCompltecheck);
                    anim.setDuration(3000);
                    anim.start();

                    rel.setVisibility(View.GONE);

                    cooledcpu.setText("Cooled CPU to 25.3°C");
                    cooledcpu.setTextColor(getResources().getColor(R.color.neon_green));
                    anim.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            img_animation.setImageResource(0);
                            img_animation.setBackgroundResource(0);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            rippleBackground.stopRippleAnimation();

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
            cooledcpu.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(int position) {
        try {
            if (CPUCoolerActivity.apps != null && position < CPUCoolerActivity.apps.size()) {
                CPUCoolerActivity.apps.remove(position);
                mAdapter.notifyItemRemoved(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
