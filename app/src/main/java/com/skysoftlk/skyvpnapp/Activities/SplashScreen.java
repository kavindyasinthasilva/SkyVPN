package com.skysoftlk.skyvpnapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.skysoftlk.skyvpnapp.R;
import com.skysoftlk.skyvpnapp.Utils.ChinaUtils;

public class SplashScreen extends AppCompatActivity {
    View coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        coordinatorLayout = findViewById(R.id.cordi);

        // Animations
        View logoImg = findViewById(R.id.logo_img);
        View logoGlow = findViewById(R.id.logo_glow);
        View slogan = findViewById(R.id.app_slogan);

        logoImg.setAlpha(0f);
        logoImg.setScaleX(0.8f);
        logoImg.setScaleY(0.8f);
        
        logoImg.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(1200).setInterpolator(new android.view.animation.OvershootInterpolator()).start();
        
        android.view.animation.Animation pulse = new android.view.animation.AlphaAnimation(0.2f, 0.5f);
        pulse.setDuration(1000);
        pulse.setRepeatMode(android.view.animation.Animation.REVERSE);
        pulse.setRepeatCount(android.view.animation.Animation.INFINITE);
        logoGlow.startAnimation(pulse);

        slogan.setAlpha(0f);
        slogan.setTranslationY(20f);
        slogan.animate().alpha(1f).translationY(0f).setStartDelay(500).setDuration(1000).start();

        // Offload Firebase auth to a background thread
        new Thread(() -> {
            try {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if (mAuth.getCurrentUser() == null) {
                    mAuth.signInAnonymously();
                }
            } catch (Exception e) {
                Log.e("SplashScreen", "Firebase initialization failed", e);
            }
        }).start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!Utility.isOnline(getApplicationContext())) {
                Snackbar.make(coordinatorLayout, "Check internet connection", Snackbar.LENGTH_LONG).show();
                
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    startNextActivity();
                }, 2000);
            } else {
                startNextActivity();
            }
        }, ChinaUtils.isLikelyInChina(this) ? 2000 : 3000);
    }

    private void startNextActivity() {
        if (!isFinishing()) {
            startActivity(new Intent(SplashScreen.this, IntroActivity.class));
            finish();
        }
    }
}
