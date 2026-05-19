package com.skysoftlk.skyvpnapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.skysoftlk.skyvpnapp.R;
import com.skysoftlk.skyvpnapp.Utils.LanguageManager;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;

public class IntroActivity extends MaterialIntroActivity {
    SharedPreferences prefs;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageManager.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", true)) {
            onFinish();
        } else {
            addSlide(IntroSlideFragment.newInstance(
                    "Fast VPN locations",
                    "Choose a server, connect quickly, and keep your session easy to monitor.",
                    R.raw.homel
            ));
            addSlide(IntroSlideFragment.newInstance(
                    "Privacy when you need it",
                    "Protect your connection on Wi-Fi and browse with a cleaner sense of control.",
                    R.raw.homel
            ));
            addSlide(IntroSlideFragment.newInstance(
                    "Useful phone tools",
                    "Boost memory, cool busy apps, and manage battery modes from one place.",
                    R.raw.homel
            ));
        }
    }

    @Override
    public void onFinish() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstTime", false);
        editor.apply();
        super.onFinish();
    }
}
