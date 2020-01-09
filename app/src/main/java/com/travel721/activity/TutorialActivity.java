package com.travel721.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.travel721.R;
import com.travel721.fragment.TutorialSlideFragment;

import org.jetbrains.annotations.NotNull;


public class TutorialActivity extends FragmentActivity {
    private static final int NUM_PAGES = 3;
    private ViewPager2 mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_layout);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.view_pager);
        FragmentStateAdapter pagerAdapter = new TutorialSlidePagerAdapter(this);
        mPager.setAdapter(pagerAdapter);
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ImageView progress = findViewById(R.id.tutorial_progress_imageview);
                switch (position) {
                    case 0:
                        progress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_loading_1));
                        break;
                    case 1:
                        progress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_loading_2));
                        break;
                    case 2:
                        progress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_loading_3));
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public void progressFromTutorial(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("firstrun", false).apply();
        Intent i = new Intent(this, InitialLoadSplashActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class TutorialSlidePagerAdapter extends FragmentStateAdapter {

        public TutorialSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NotNull
        @Override
        public Fragment createFragment(int position) {
            return new TutorialSlideFragment(position);
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}
