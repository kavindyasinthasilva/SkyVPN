package com.skysoftlk.vpnapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.skysoftlk.vpnapp.Activities.SpeedBoosterActivity;

public class BoostAlarm extends BroadcastReceiver {

    public final static String PREFERENCES_RES_BOOSTER = "ivpn";

    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedpreferences = context.getSharedPreferences(PREFERENCES_RES_BOOSTER, Context.MODE_PRIVATE);


        editor = sharedpreferences.edit();
        editor.putString("booster", "1");
        editor.apply();

        try {
            SpeedBoosterActivity.optimizebutton.setBackgroundResource(0);
            SpeedBoosterActivity.optimizebutton.setImageResource(0);
            SpeedBoosterActivity.optimizebutton.setImageResource(R.drawable.n_bt);
        }
        catch(Exception e)
        {

        }

    }
}
