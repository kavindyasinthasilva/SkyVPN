package com.skysoftlk.skyvpnapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.skysoftlk.skyvpnapp.R;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;

public class IntroActivity extends MaterialIntroActivity {
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", true)) {
            onFinish();
        } else {
            addSlide(IntroSlideFragment.newInstance(
                    "Ultra Fast Speed",
                    "Connect to high-speed servers across the globe with zero latency.",
                    R.raw.homel
            ));
            addSlide(IntroSlideFragment.newInstance(
                    "Maximum Security",
                    "Military-grade encryption keeps your data safe from prying eyes.",
                    R.raw.homel
            ));
            addSlide(IntroSlideFragment.newInstance(
                    "Privacy First",
                    "We follow a strict no-logs policy to ensure your anonymity online.",
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

