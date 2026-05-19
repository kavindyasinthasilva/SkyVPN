package com.skysoftlk.skyvpnapp.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.skysoftlk.skyvpnapp.R;

import io.github.dreierf.materialintroscreen.SlideFragment;

public class IntroSlideFragment extends SlideFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_LOTTIE_RES = "lottie_res";

    public static IntroSlideFragment newInstance(String title, String description, int lottieRes) {
        IntroSlideFragment fragment = new IntroSlideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        args.putInt(ARG_LOTTIE_RES, lottieRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_slide, container, false);

        TextView titleTv = view.findViewById(R.id.intro_title);
        TextView descTv = view.findViewById(R.id.intro_description);
        LottieAnimationView lottieView = view.findViewById(R.id.intro_lottie);

        if (getArguments() != null) {
            titleTv.setText(getArguments().getString(ARG_TITLE));
            descTv.setText(getArguments().getString(ARG_DESCRIPTION));
            lottieView.setAnimation(getArguments().getInt(ARG_LOTTIE_RES));
        }

        return view;
    }

    @Override
    public int backgroundColor() {
        return R.color.futuristic_bg_start;
    }

    @Override
    public int buttonsColor() {
        return R.color.accent_teal;
    }

    @Override
    public boolean canMoveFurther() {
        return true;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return "Please complete the step";
    }
}
