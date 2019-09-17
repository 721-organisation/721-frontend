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
        String accessToken = getIntent().getStringExtra("accessToken");
        String iid = getIntent().getStringExtra("IID");
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.getTabAt(1).select();
        Bundle bundle = getIntent().getBundleExtra("fragment_bundle");
        LoadingCardSwipeFragment csf = LoadingCardSwipeFragment.newInstance(accessToken, iid, bundle);
        My721Fragment lef = My721Fragment.newInstance(bundle);
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
                    case 2:
                        SelectLocationDiscoverFragment addPhotoBottomDialogFragment =
                                SelectLocationDiscoverFragment.newInstance(R.id.fragmentContainer, getIntent().getBundleExtra("fragment_bundle").getString("accessToken"));
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

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}