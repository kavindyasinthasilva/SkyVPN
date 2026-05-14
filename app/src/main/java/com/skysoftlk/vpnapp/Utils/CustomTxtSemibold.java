package com.skysoftlk.vpnapp.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class CustomTxtSemibold extends androidx.appcompat.widget.AppCompatTextView {

    public CustomTxtSemibold(Context context) {
        super(context);
        init();
    }

    public CustomTxtSemibold(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTxtSemibold(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        if (!isInEditMode()){
            try {
                Typeface normalTypeface = Typeface.createFromAsset(getContext().getAssets(), "Montserrat-SemiBold.ttf");
                setTypeface(normalTypeface);
            } catch (Exception e) {
                try {
                    Typeface regularTypeface = Typeface.createFromAsset(getContext().getAssets(), "Montserrat-Regular.ttf");
                    setTypeface(regularTypeface);
                } catch (Exception ex) {
                    // Fallback to default
                }
            }
        }
    }
}
