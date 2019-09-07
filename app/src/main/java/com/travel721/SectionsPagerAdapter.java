package com.travel721;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.my_721_tab, R.string.near_me_tab, R.string.discover_tab};
    private final Context mContext;
    public Bundle bundle;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    //TODO Make this return different fragments
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return CardSwipeFragment.newInstance(position + 1, bundle);
            default:
                return new Fragment();
        }
        // getItem is called to instantiate the fragment for the given page.
        // Return a CardSwipeFragment (defined as a static inner class below).


    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }


    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}