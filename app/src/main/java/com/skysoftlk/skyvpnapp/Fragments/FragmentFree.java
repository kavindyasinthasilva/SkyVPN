package com.skysoftlk.skyvpnapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skysoftlk.skyvpnapp.R;
import com.skysoftlk.skyvpnapp.AdapterWrappers.ServerListAdapterFree;
import com.skysoftlk.skyvpnapp.Utils.Constants;
import com.skysoftlk.skyvpnapp.model.Countries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentFree extends Fragment implements ServerListAdapterFree.RegionListAdapterInterface {
    private RecyclerView recyclerView;
    private ServerListAdapterFree adapter;
    private ArrayList<Countries> countryArrayList;
    int server;

    private RelativeLayout animationHolder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        recyclerView = view.findViewById(R.id.region_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        countryArrayList = new ArrayList<>();
        animationHolder = view.findViewById(R.id.animation_layout);

        adapter = new ServerListAdapterFree(getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadServers();
    }

    private void loadServers() {
        // Skip reloading if we already have data to avoid list flashing and unnecessary CPU usage
        if (adapter != null && adapter.getItemCount() > 0 && countryArrayList.size() > 0) {
            animationHolder.setVisibility(View.GONE);
            return;
        }

        ArrayList<Countries> servers = new ArrayList<>();

        // 1. Load Manual Servers from JSON
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
            Log.e("FragmentFree", "Manual JSON error: " + e.getMessage());
        }

        if (Constants.FREE_SERVERS != null && !Constants.FREE_SERVERS.isEmpty()) {
            try {
                // Check if the response is an error object instead of a list
                boolean isError = false;
                if (Constants.FREE_SERVERS.startsWith("{")) {
                    JSONObject errorObj = new JSONObject(Constants.FREE_SERVERS);
                    if (errorObj.has("result") && errorObj.getString("result").equals("error")) {
                        Log.e("FragmentFree", "Fetch Error: " + errorObj.optString("message"));
                        isError = true;
                    }
                }

                if (!isError) {
                    JSONArray jsonArray = new JSONArray(Constants.FREE_SERVERS);
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
                Log.e("FragmentFree", "JSON Parsing error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        animationHolder.setVisibility(View.GONE);
        countryArrayList.clear();
        countryArrayList.addAll(servers);
        adapter.setData(servers);
    }

    private void addIfAvailable(ArrayList<Countries> servers, Countries country) {
        if (country == null) return;
        servers.add(country);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadServers();
    }

    @Override
    public void onCountrySelected(Countries item) {
    }
}
