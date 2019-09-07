package com.travel721;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.selectTab(tabLayout.getTabAt(1));
        Bundle bundle = getIntent().getBundleExtra("fragment_bundle");
        CardSwipeFragment csf = CardSwipeFragment.newInstance(0, bundle);
        ListEventsActivity lef = ListEventsActivity.newInstance(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, csf).commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, lef).commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, csf).commit();
                        break;
                    default:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, new Fragment()).commit();
                        // Continue for each tab in TabLayout
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}