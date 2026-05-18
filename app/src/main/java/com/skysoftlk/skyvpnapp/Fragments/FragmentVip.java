package com.skysoftlk.skyvpnapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skysoftlk.skyvpnapp.Activities.MainActivity;
import com.skysoftlk.skyvpnapp.R;
import com.skysoftlk.skyvpnapp.Activities.UnlockAllActivity;
import com.skysoftlk.skyvpnapp.AdapterWrappers.ServerListAdapterVip;
import com.skysoftlk.skyvpnapp.Config;
import com.skysoftlk.skyvpnapp.Utils.Constants;
import com.skysoftlk.skyvpnapp.model.Countries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentVip extends Fragment {

    private RecyclerView recyclerView;
    private ServerListAdapterVip adapter;
    private RelativeLayout animationHolder;
    private static final String TAG = "VPN_VIP";
    private RelativeLayout mPurchaseLayout;
    private ImageButton mUnblockButton;
    private static SharedPreferences sharedPreferences;
    static Countries countryy;
    static View btView;
    public static Context context;
    public static boolean viewSet = false;
    static View view;
    public static ProgressDialog progressdialog;
    private static BottomSheetDialog btDialog;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_one, container, false);

        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Please wait just a moment !!");
        progressdialog.setCancelable(false);

        recyclerView = view.findViewById(R.id.region_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        animationHolder = view.findViewById(R.id.animation_layout);
        sharedPreferences = getContext().getSharedPreferences("userRewarded", Context.MODE_PRIVATE);
        
        btView = LayoutInflater.from(context)
                .inflate(R.layout.layout_bottom_sheet, (ConstraintLayout) view.findViewById(R.id.bsContainer));

        btDialog = new BottomSheetDialog(
                context, R.style.BottomSheetDialogTheme
        );

        btDialog.setContentView(btView);

        mPurchaseLayout = view.findViewById(R.id.purchase_layout);
        mUnblockButton = view.findViewById(R.id.vip_unblock);

        mPurchaseLayout.setVisibility(View.GONE);

        // Hide ad-related buttons in the bottom sheet if they exist in the layout
        View watchAdBtn = btView.findViewById(R.id.watch_ads);
        if (watchAdBtn != null) watchAdBtn.setVisibility(View.GONE);
        View watchFaceAdBtn = btView.findViewById(R.id.watch_face_ads);
        if (watchFaceAdBtn != null) watchFaceAdBtn.setVisibility(View.GONE);

        adapter = new ServerListAdapterVip(getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadServers();
    }

    private void loadServers() {
        ArrayList<Countries> servers = new ArrayList<>();

        // Load Manual Servers from JSON
        try {
            JSONArray manualArray = new JSONArray(Constants.MANUAL_SERVERS_JSON);
            for (int i = 0; i < manualArray.length(); i++) {
                JSONObject obj = manualArray.getJSONObject(i);
                servers.add(new Countries(
                        obj.getString("serverName"),
                        obj.getString("flagUrl"),
                        obj.getString("ovpnConfig"),
                        obj.getString("userName"),
                        obj.getString("password")
                ));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Manual JSON error: " + e.getMessage());
        }

        if (Constants.PREMIUM_SERVERS != null && !Constants.PREMIUM_SERVERS.isEmpty()) {
            try {
                boolean isError = false;
                if (Constants.PREMIUM_SERVERS.startsWith("{")) {
                    JSONObject errorObj = new JSONObject(Constants.PREMIUM_SERVERS);
                    if (errorObj.has("result") && errorObj.getString("result").equals("error")) {
                        Log.e(TAG, "Fetch Error: " + errorObj.optString("message"));
                        isError = true;
                    }
                }

                if (!isError) {
                    JSONArray jsonArray = new JSONArray(Constants.PREMIUM_SERVERS);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        servers.add(new Countries(object.getString("serverName"),
                                object.getString("flag_url"),
                                object.getString("ovpnConfiguration"),
                                object.getString("vpnUserName"),
                                object.getString("vpnPassword")
                        ));
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        adapter.setData(servers);
        animationHolder.setVisibility(View.GONE);
    }

    public static void unblockServer() {
        btView.findViewById(R.id.but_subs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UnlockAllActivity.class));
                btDialog.dismiss();
            }
        });

        btDialog.show();
    }

    public static void onItemClick(Countries country) {
        countryy = country;

        if (Config.hasPremiumAccess(context)) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("c", country);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        } else {
            unblockServer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadServers();
    }
}
