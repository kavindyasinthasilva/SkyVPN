package com.skysoftlk.vpnapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.skysoftlk.vpnapp.R;
import com.skysoftlk.vpnapp.Utils.Constants;

import java.io.IOException;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;

public class IntroActivity extends MaterialIntroActivity {
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", true)) {
            onFinish();
        } else {
//            To familiar the user with the basic requirements of the application...
            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.colorPrimaryDark)
                    .buttonsColor(R.color.colorPrimary)
                    .image(R.drawable.intro)
                    .title("Secure VPN Servers")
                    .description("Premium VPN App is Very Fast & Secure. And Easy to Use")
                    .build());
            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.colorPrimaryDark)
                    .buttonsColor(R.color.colorPrimary)
                    .image(R.drawable.intro2)
                    .title("Use Premium ")
                    .description("Buy Premium Servers and get more Secure Servers ")
                    .build());

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

