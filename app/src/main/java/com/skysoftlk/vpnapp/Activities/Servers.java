package com.skysoftlk.vpnapp.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.skysoftlk.vpnapp.Fragments.FragmentFree;
import com.skysoftlk.vpnapp.R;
import com.google.android.material.tabs.TabLayout;
import com.skysoftlk.vpnapp.AdapterWrappers.TabAdapter;
import com.skysoftlk.vpnapp.Fragments.FragmentVip;

public class Servers extends BaseActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarold);
        toolbar.setTitle("VPN Servers");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentVip(), "All Servers");
        // Removed FragmentFree as we only want one category
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        
        // Hide tab layout if only one fragment is present
        tabLayout.setVisibility(android.view.View.GONE);
    }
}
