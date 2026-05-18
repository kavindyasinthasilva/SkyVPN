package com.skysoftlk.skyvpnapp.Activities;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    
    @Nullable
    @Override
    public Object getSystemService(@NonNull String name) {
        if (Context.ACCESSIBILITY_SERVICE.equals(name)) {
            try {
                return super.getSystemService(name);
            } catch (Throwable t) {
                Log.e("BaseActivity", "Failed to get AccessibilityManagerService, it might be dead.", t);
                return null;
            }
        }
        return super.getSystemService(name);
    }
}
