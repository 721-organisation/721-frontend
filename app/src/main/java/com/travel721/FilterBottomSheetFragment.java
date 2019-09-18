package com.travel721;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;

public class FilterBottomSheetFragment extends RoundedBottomSheetDialogFragment {
    private LoadingFragment callingLoader;

    public static FilterBottomSheetFragment newInstance(LoadingFragment callingLoader) {
        FilterBottomSheetFragment filterBottomSheetFragment = new FilterBottomSheetFragment();
        filterBottomSheetFragment.callingLoader = callingLoader;
        return filterBottomSheetFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_filter_bottom_sheet, null);
//        getFragmentManager().beginTransaction().replace(android.R.id.content, new CurationSettingsFragment()).commit();
        getChildFragmentManager().beginTransaction().replace(android.R.id.content, new CurationSettingsFragment(callingLoader)).commit();
        root.findViewById(R.id.filterTitle).setOnClickListener(view -> {
            dismiss();
        });
        root.findViewById(R.id.filter_go_button).setOnClickListener(view -> {
            dismiss();
        });
        return root;
    }

    public static class CurationSettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        boolean settingsChanged = false;
        LoadingFragment callingLoader;

        CurationSettingsFragment(LoadingFragment callingLoader) {
            this.callingLoader = callingLoader;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.event_curation_preferences, rootKey);
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            Log.v("YO", "DETACHED");
            if (settingsChanged) {
                String newdays = String.valueOf(getPreferenceManager().getSharedPreferences().getInt("daysFromNow", 1));
                String newradius = String.valueOf(getPreferenceManager().getSharedPreferences().getInt("radius", 1));
                if (callingLoader instanceof LoadingNearMeFragment) {
                    ((LoadingNearMeFragment) callingLoader).radius = newradius;
                    ((LoadingNearMeFragment) callingLoader).daysFromNow = newdays;
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, LoadingNearMeFragment.clone((LoadingNearMeFragment) callingLoader)).commit();
                }
                if (callingLoader instanceof LoadingDiscoverFragment) {
                    ((LoadingDiscoverFragment) callingLoader).radius = newradius;
                    ((LoadingDiscoverFragment) callingLoader).daysFromNow = newdays;
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, LoadingDiscoverFragment.clone((LoadingDiscoverFragment) callingLoader)).commit();

                }


            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            settingsChanged = true;
        }
    }


}
