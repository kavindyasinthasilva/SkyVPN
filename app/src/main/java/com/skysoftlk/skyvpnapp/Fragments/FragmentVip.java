package com.skysoftlk.skyvpnapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FragmentVip extends Fragment {

    private RecyclerView recyclerView;
    private ServerListAdapterVip adapter;
    private RelativeLayout animationHolder;
    private static final String TAG = "VPN_VIP";
    private RelativeLayout mPurchaseLayout;
    private ImageButton mUnblockButton;
    private SharedPreferences sharedPreferences;
    private Countries selectedCountry;
    private View btView;
    private ProgressDialog progressdialog;
    private BottomSheetDialog btDialog;
    private ScheduledExecutorService scheduledExecutor;
    private ExecutorService pingExecutor;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ArrayList<Countries> cachedServers;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_one, container, false);
        Context context = requireContext();

        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Please wait just a moment !!");
        progressdialog.setCancelable(false);

        recyclerView = view.findViewById(R.id.region_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        animationHolder = view.findViewById(R.id.animation_layout);
        sharedPreferences = context.getSharedPreferences("userRewarded", Context.MODE_PRIVATE);
        
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
        adapter.setOnServerClickListener(this::onItemClick);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadServers();
    }

    private void loadServers() {
        if (cachedServers != null && adapter != null && adapter.getItemCount() > 0) {
            animationHolder.setVisibility(View.GONE);
            return;
        }

        ArrayList<Countries> servers = new ArrayList<>();

        // Load Manual Servers from JSON
        try {
            JSONArray manualArray = new JSONArray(Constants.MANUAL_SERVERS_JSON);
            for (int i = 0; i < manualArray.length(); i++) {
                JSONObject obj = manualArray.getJSONObject(i);
                addIfAvailable(servers, new Countries(
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
                        addIfAvailable(servers, new Countries(object.getString("serverName"),
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
        cachedServers = servers;
        animationHolder.setVisibility(View.GONE);

        startPinging(servers);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRealTimeUpdates();
    }

    @Override
    public void onDestroyView() {
        stopRealTimeUpdates();
        if (btDialog != null && btDialog.isShowing()) {
            btDialog.dismiss();
        }
        if (progressdialog != null && progressdialog.isShowing()) {
            progressdialog.dismiss();
        }
        btDialog = null;
        progressdialog = null;
        btView = null;
        adapter = null;
        recyclerView = null;
        animationHolder = null;
        super.onDestroyView();
    }

    private void stopRealTimeUpdates() {
        if (scheduledExecutor != null && !scheduledExecutor.isShutdown()) {
            scheduledExecutor.shutdownNow();
        }
        if (pingExecutor != null && !pingExecutor.isShutdown()) {
            pingExecutor.shutdownNow();
        }
    }

    private void addIfAvailable(ArrayList<Countries> servers, Countries country) {
        if (country == null) return;
        servers.add(country);
    }

    private void startPinging(ArrayList<Countries> servers) {
        stopRealTimeUpdates();
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        pingExecutor = Executors.newFixedThreadPool(3); // Reduced thread count

        // Run pinging every 15 seconds while the fragment is visible (increased interval)
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            if (!isAdded() || adapter == null) {
                return;
            }
            Log.d(TAG, "Starting ping cycle for " + servers.size() + " servers");
            for (int i = 0; i < servers.size(); i++) {
                final int index = i;
                Countries server = servers.get(index);
                String host = server.getTcpServerHost();
                if (host != null && !pingExecutor.isShutdown()) {
                    pingExecutor.execute(() -> {
                        int ping = getPing(host, server.getTcpServerPort());
                        server.setPing(ping);
                        if (isAdded() && adapter != null) {
                            mainHandler.post(() -> {
                                if (adapter != null && index < adapter.getItemCount()) {
                                    adapter.notifyItemChanged(index);
                                }
                            });
                        }
                    });
                }
            }
        }, 500, 30000, TimeUnit.MILLISECONDS);
    }

    private int getPing(String host, int port) {
        try {
            long startTime = System.currentTimeMillis();
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 2000);
            }
            return (int) (System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            return 0;
        }
    }

    private void unblockServer() {
        if (btView == null || btDialog == null || getContext() == null) {
            return;
        }
        btView.findViewById(R.id.but_subs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), UnlockAllActivity.class));
                btDialog.dismiss();
            }
        });

        btDialog.show();
    }

    private void onItemClick(Countries country) {
        selectedCountry = country;

        if (Config.hasPremiumAccess(requireContext())) {
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.putExtra("c", country);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            unblockServer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cachedServers != null && adapter != null && adapter.getItemCount() > 0) {
            startPinging(cachedServers);
        } else {
            loadServers();
        }
    }
}
