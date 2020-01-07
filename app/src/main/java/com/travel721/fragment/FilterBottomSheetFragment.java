package com.travel721.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.travel721.R;

import java.util.ArrayList;
import java.util.Objects;

public class FilterBottomSheetFragment extends RoundedBottomSheetDialogFragment {
    private LoadingFragment callingLoader;

    public static FilterBottomSheetFragment newInstance(LoadingFragment callingLoader, ArrayList<String> tags, boolean enableSheffieldFilters) {
        FilterBottomSheetFragment filterBottomSheetFragment = new FilterBottomSheetFragment();
        filterBottomSheetFragment.callingLoader = callingLoader;
        return filterBottomSheetFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_filter_bottom_sheet, null);
        Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(Objects.requireNonNull(bottomSheet))
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });
//        getFragmentManager().beginTransaction().replace(android.R.id.content, new CurationSettingsFragment()).commit();
        getChildFragmentManager().beginTransaction().replace(android.R.id.content, new CurationSettingsFragment(callingLoader)).commit();
        root.findViewById(R.id.filterTitle).setOnClickListener(view -> dismiss());
        root.findViewById(R.id.filterCloseButton).setOnClickListener(view -> dismiss());
        root.findViewById(R.id.filter_ok_button).setOnClickListener(view -> {
//            ChipGroup chipGroup = Objects.requireNonNull(getView()).findViewById(R.id.near_me_filters_chip_group);
            // Logic for determining tags to filter by go here
//            for (int i : chipGroup.getCheckedChipIds()) {
//                tagsToFilterBy.add(tags.get(i));
//            }
//            ToggleButton whatsHotToggleButton = root.findViewById(R.id.whatshot_tag);
//            ToggleButton travelToggleButton = root.findViewById(R.id.travel_tag);
//            ToggleButton xmasToggleButton = root.findViewById(R.id.xmas_tag);
//            ToggleButton foodAndDrinkToggleButton = root.findViewById(R.id.food_drink_tag);
//            ToggleButton fitnessToggleButton = root.findViewById(R.id.fitness_tag);
//            ToggleButton musicToggleButton = root.findViewById(R.id.music_tag);
//
//            if (whatsHotToggleButton.isChecked()) tagsToFilterBy.add(WHATS_HOT_TAG);
//            if (xmasToggleButton.isChecked()) tagsToFilterBy.add(XMAS_TAG);
//            if (travelToggleButton.isChecked()) tagsToFilterBy.add(TRAVEL_TAG);
//            if (fitnessToggleButton.isChecked()) tagsToFilterBy.add(FITNESS_TAG);
//            if (musicToggleButton.isChecked()) tagsToFilterBy.add(MUSIC_TAG);
//            if (foodAndDrinkToggleButton.isChecked()) tagsToFilterBy.add(FOOD_AND_DRINK_TAG);
            dismiss();
        });
//        ChipGroup tagChipGroup = root.findViewById(R.id.near_me_filters_chip_group);
//        boolean remoteConfigSheffieldTagsEnabled = mFirebaseRemoteConfig.getBoolean(SHEFFIELD_FILTERS_ENABLED_KEY);
//        if (enableSheffieldFilters && !remoteConfigSheffieldTagsEnabled) {
//            tagChipGroup.setVisibility(View.GONE);
//            root.findViewById(R.id.sheffield721tag).setVisibility(View.GONE);
//        }
//        tagChipGroup.setChipSpacing(10);


//        for (int i = 0; i < tags.size(); i++) {
//            Chip chip = new Chip(Objects.requireNonNull(getContext()));
//            chip.setId(i);
//            chip.setTag(i);
//            chip.setChipBackgroundColor(ResourcesCompat.getColorStateList(getResources(), R.color.chip_selector_state_list, null));
//            chip.setTextColor(ResourcesCompat.getColor(getResources(), R.color.chip_text_selector_state_list, null));
//            chip.setText(tags.get(i));
//            chip.setCheckable(true);
//            tagChipGroup.addView(chip);
//
//        }
//        tagChipGroup.setOnCheckedChangeListener((chipGroup1, i) -> {
//
//        });
//        tagChipGroup.invalidate();
        return root;
    }

    /**
     * This inner class is a special utility that provides the 'filter' button's functionality
     * <p>
     * Once a change in the settings is detected; this fragment clones the LoadingFragment
     * that loaded the current set of events and changes its settings accordingly.
     * The LoadingFragment is then swapped in to the UI and loads events.
     */
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
            if (settingsChanged) {
                String newdays = String.valueOf(getPreferenceManager().getSharedPreferences().getInt("daysFromNow", 1));
                String newradius = String.valueOf(getPreferenceManager().getSharedPreferences().getInt("radius", 1));
                if (callingLoader instanceof LoadingNearMeFragment) {
                    ((LoadingNearMeFragment) callingLoader).radius = newradius;
                    ((LoadingNearMeFragment) callingLoader).daysFromNow = newdays;
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, LoadingNearMeFragment.clone((LoadingNearMeFragment) callingLoader)).commit();
                }
                if (callingLoader instanceof LoadingDiscoverFragment) {
                    ((LoadingDiscoverFragment) callingLoader).radius = newradius;
                    ((LoadingDiscoverFragment) callingLoader).daysFromNow = newdays;
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, LoadingDiscoverFragment.clone((LoadingDiscoverFragment) callingLoader)).commit();

                }
            }


        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            settingsChanged = true;
        }
    }


}
