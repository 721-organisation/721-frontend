package com.travel721.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.ChipGroup;
import com.travel721.R;

import java.util.ArrayList;
import java.util.Objects;

import static com.travel721.Constants.FITNESS_TAG;
import static com.travel721.Constants.FOOD_AND_DRINK_TAG;
import static com.travel721.Constants.MUSIC_TAG;
import static com.travel721.Constants.TRAVEL_TAG;
import static com.travel721.Constants.WHATS_HOT_TAG;
import static com.travel721.Constants.XMAS_TAG;

public class FilterBottomSheetFragment extends RoundedBottomSheetDialogFragment {
    private LoadingFragment callingLoader;
    private static ArrayList<String> tags;
    private static ArrayList<String> tagsToFilterBy = new ArrayList<>();

    public static FilterBottomSheetFragment newInstance(LoadingFragment callingLoader, ArrayList<String> tags) {
        FilterBottomSheetFragment filterBottomSheetFragment = new FilterBottomSheetFragment();
        filterBottomSheetFragment.callingLoader = callingLoader;
        FilterBottomSheetFragment.tags = tags;

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
//            for (int i : chipGroup.getCheckedChipIds()) {
//                tagsToFilterBy.add(tags.get(i));
//            }
            ToggleButton whatsHotToggleButton = root.findViewById(R.id.whatshot_tag);
            ToggleButton travelToggleButton = root.findViewById(R.id.travel_tag);
            ToggleButton xmasToggleButton = root.findViewById(R.id.xmas_tag);
            ToggleButton foodAndDrinkToggleButton = root.findViewById(R.id.food_drink_tag);
            ToggleButton fitnessToggleButton = root.findViewById(R.id.fitness_tag);
            ToggleButton musicToggleButton = root.findViewById(R.id.music_tag);

            if (whatsHotToggleButton.isChecked()) tagsToFilterBy.add(WHATS_HOT_TAG);
            if (xmasToggleButton.isChecked()) tagsToFilterBy.add(XMAS_TAG);
            if (travelToggleButton.isChecked()) tagsToFilterBy.add(TRAVEL_TAG);
            if (fitnessToggleButton.isChecked()) tagsToFilterBy.add(FITNESS_TAG);
            if (musicToggleButton.isChecked()) tagsToFilterBy.add(MUSIC_TAG);
            if (foodAndDrinkToggleButton.isChecked()) tagsToFilterBy.add(FOOD_AND_DRINK_TAG);
            dismiss();
        });
        ChipGroup chipGroup = root.findViewById(R.id.near_me_filters_chip_group);
        chipGroup.setChipSpacing(10);

        // Temporarily disable enumerating of all filters
//        for (int i = 0; i < tags.size(); i++) {
//            Chip chip = new Chip(Objects.requireNonNull(getContext()));
//            chip.setId(i);
//            chip.setTag(i);
//            chip.setChipBackgroundColor(ResourcesCompat.getColorStateList(getResources(), R.color.chip_selector_state_list, null));
//            chip.setTextColor(ResourcesCompat.getColor(getResources(), R.color.chip_text_selector_state_list, null));
//            chip.setText(tags.get(i));
//            chip.setCheckable(true);
//            chipGroup.addView(chip);
//
//        }
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
