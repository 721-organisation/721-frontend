package com.travel721.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceFragmentCompat;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.travel721.R;

import java.util.ArrayList;

public class FilterBottomSheetFragment extends RoundedBottomSheetDialogFragment {
    private LoadingFragment callingLoader;
    private static ArrayList<String> tags;
    private static ArrayList<String> tagsToFilterBy = new ArrayList<>();

    public static FilterBottomSheetFragment newInstance(LoadingFragment callingLoader, ArrayList<String> tags) {
        FilterBottomSheetFragment filterBottomSheetFragment = new FilterBottomSheetFragment();
        filterBottomSheetFragment.callingLoader = callingLoader;
        filterBottomSheetFragment.tags = tags;

        return filterBottomSheetFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_filter_bottom_sheet, null);
        tagsToFilterBy = new ArrayList<>();
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });
//        getFragmentManager().beginTransaction().replace(android.R.id.content, new CurationSettingsFragment()).commit();
        getChildFragmentManager().beginTransaction().replace(android.R.id.content, new CurationSettingsFragment(callingLoader)).commit();
        root.findViewById(R.id.filterTitle).setOnClickListener(view -> {
            dismiss();
        });
        root.findViewById(R.id.filterCloseButton).setOnClickListener(view -> {
            dismiss();
        });
        root.findViewById(R.id.filter_ok_button).setOnClickListener(view -> {
            ChipGroup chipGroup = getView().findViewById(R.id.near_me_filters_chip_group);
            for (int i : chipGroup.getCheckedChipIds()) {
                tagsToFilterBy.add(tags.get(i));
            }
            dismiss();
        });
        ChipGroup chipGroup = root.findViewById(R.id.near_me_filters_chip_group);
        chipGroup.setChipSpacing(10);

        for (int i = 0; i < tags.size(); i++) {
            Chip chip = new Chip(getContext());
            chip.setId(i);
            chip.setTag(i);
            chip.setChipBackgroundColor(ResourcesCompat.getColorStateList(getResources(), R.color.chip_selector_state_list, null));
            chip.setTextColor(ResourcesCompat.getColor(getResources(), R.color.chip_text_selector_state_list, null));
            chip.setText(tags.get(i));
            chip.setCheckable(true);
            chipGroup.addView(chip);

        }
        chipGroup.setOnCheckedChangeListener((chipGroup1, i) -> {

        });
        chipGroup.invalidate();
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
            if (settingsChanged || !tagsToFilterBy.isEmpty()) {
                String newdays = String.valueOf(getPreferenceManager().getSharedPreferences().getInt("daysFromNow", 1));
                String newradius = String.valueOf(getPreferenceManager().getSharedPreferences().getInt("radius", 1));
                if (callingLoader instanceof LoadingNearMeFragment) {
                    ((LoadingNearMeFragment) callingLoader).radius = newradius;
                    ((LoadingNearMeFragment) callingLoader).daysFromNow = newdays;
                    ((LoadingNearMeFragment) callingLoader).tagsToFilterBy = tagsToFilterBy;
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
