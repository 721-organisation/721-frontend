package com.travel721.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.travel721.R;
import com.travel721.fragment.LoadingNearMeFragment;
import com.travel721.fragment.My721Fragment;
import com.travel721.fragment.SelectLocationDiscoverFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);
        String accessToken = getIntent().getStringExtra("accessToken");
        String iid = getIntent().getStringExtra("IID");
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        Objects.requireNonNull(tabLayout.getTabAt(1)).select();
        String longitude = getIntent().getStringExtra("longitude");
        String latitude = getIntent().getStringExtra("latitude");
        String radius = getIntent().getStringExtra("radius");
        String daysFromNow = getIntent().getStringExtra("daysfromnow");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, LoadingNearMeFragment.newInstance(accessToken, iid, longitude, latitude, radius, daysFromNow, null)).commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            LoadingNearMeFragment loadingNearMeFragment;
            My721Fragment my721Fragment;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.v("TAG1", "SELECT");

                String prefRadius = String.valueOf(getSharedPreferences("com.travel721_preferences", MODE_PRIVATE).getInt("radius", 1));
                String prefDaysFromNow = String.valueOf(getSharedPreferences("com.travel721_preferences", MODE_PRIVATE).getInt("daysFromNow", 1));
                switch (tab.getPosition()) {
                    case 0:
                        my721Fragment = My721Fragment.newInstance(accessToken);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, my721Fragment).commit();
                        break;
                    case 1:
                        loadingNearMeFragment = LoadingNearMeFragment.newInstance(accessToken, iid, longitude, latitude, prefRadius, prefDaysFromNow, null);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, loadingNearMeFragment).commit();
                        break;
                    case 2:
                        SelectLocationDiscoverFragment addPhotoBottomDialogFragment =
                                SelectLocationDiscoverFragment.newInstance(R.id.fragmentContainer, accessToken, iid);
                        addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                                "discover_sheet_fragment");
                        break;
                    default:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, new Fragment()).commit();
                        // Continue for each tab in TabLayout
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.v("TAG2", "UNSELECT");


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.v("TAG3", "RESELECT");
            }

        });
    }
}