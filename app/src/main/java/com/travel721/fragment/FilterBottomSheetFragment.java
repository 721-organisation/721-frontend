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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.travel721.R;

import java.util.ArrayList;
import java.util.Objects;

import static com.travel721.Constants.BEST_OF_SHEFFIELD_TAG;
import static com.travel721.Constants.CITY_CENTRE_TAG;
import static com.travel721.Constants.ECCY_ROAD_TAG;
import static com.travel721.Constants.HALLAM_TAG;
import static com.travel721.Constants.KELHAM_ISLAND_TAG;
import static com.travel721.Constants.SHEFFIELD_FILTERS_ENABLED_KEY;
import static com.travel721.Constants.UOS_TAG;

public class FilterBottomSheetFragment extends RoundedBottomSheetDialogFragment {
    private LoadingFragment callingLoader;
    private ArrayList<String> tags;
    private static ArrayList<String> tagsToFilterBy = new ArrayList<>();
    private boolean enableSheffieldFilters = false;
    FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static FilterBottomSheetFragment newInstance(LoadingFragment callingLoader, ArrayList<String> tags, boolean enableSheffieldFilters) {
        FilterBottomSheetFragment filterBottomSheetFragment = new FilterBottomSheetFragment();
        filterBottomSheetFragment.callingLoader = callingLoader;
        ArrayList<String> sheffieldLocalTags = new ArrayList<>();
        sheffieldLocalTags.add(BEST_OF_SHEFFIELD_TAG);
        sheffieldLocalTags.add(UOS_TAG);
        sheffieldLocalTags.add(HALLAM_TAG);
        sheffieldLocalTags.add(ECCY_ROAD_TAG);
        sheffieldLocalTags.add(CITY_CENTRE_TAG);
        sheffieldLocalTags.add(KELHAM_ISLAND_TAG);
        filterBottomSheetFragment.tags = sheffieldLocalTags;
        filterBottomSheetFragment.enableSheffieldFilters = enableSheffieldFilters;
// cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
// will use fetch data from the Remote Config service, rather than cached parameter values,
// if cached parameter values are more than cacheExpiration seconds old.
// See Best Practices in the README for more information.
        filterBottomSheetFragment.mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        filterBottomSheetFragment.mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(600)
                .build();
        filterBottomSheetFragment.mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        filterBottomSheetFragment.mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean updated = task.getResult();

                    }
                });
        return filterBottomSheetFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_filter_bottom_sheet, null);
        tagsToFilterBy = new ArrayList<>();
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
            ChipGroup chipGroup = Objects.requireNonNull(getView()).findViewById(R.id.near_me_filters_chip_group);
            // Logic for determining tags to filter by go here
            for (int i : chipGroup.getCheckedChipIds()) {
                tagsToFilterBy.add(tags.get(i));
            }
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
        ChipGroup tagChipGroup = root.findViewById(R.id.near_me_filters_chip_group);
        boolean remoteConfigSheffieldTagsEnabled = mFirebaseRemoteConfig.getBoolean(SHEFFIELD_FILTERS_ENABLED_KEY);
        if (enableSheffieldFilters && !remoteConfigSheffieldTagsEnabled) {
            tagChipGroup.setVisibility(View.GONE);
            root.findViewById(R.id.sheffield721tag).setVisibility(View.GONE);
        }
        tagChipGroup.setChipSpacing(10);


        for (int i = 0; i < tags.size(); i++) {
            Chip chip = new Chip(Objects.requireNonNull(getContext()));
            chip.setId(i);
            chip.setTag(i);
            chip.setChipBackgroundColor(ResourcesCompat.getColorStateList(getResources(), R.color.chip_selector_state_list, null));
            chip.setTextColor(ResourcesCompat.getColor(getResources(), R.color.chip_text_selector_state_list, null));
            chip.setText(tags.get(i));
            chip.setCheckable(true);
            tagChipGroup.addView(chip);

        }
        tagChipGroup.setOnCheckedChangeListener((chipGroup1, i) -> {

        });
        tagChipGroup.invalidate();
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
