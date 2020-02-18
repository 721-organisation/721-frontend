package com.travel721.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.travel721.R;
import com.travel721.activity.UnlockedCountriesActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import static com.travel721.Constants.testDaysFromNow;
import static com.travel721.Constants.testRadius;

public class SelectLocationDiscoverFragment extends RoundedBottomSheetDialogFragment {
    private boolean discovering = false;
    private String accessToken;
    private String IID;
    private boolean prefill = false;
    private String searchLocation;
    private LoadingFragment callingLoader;


    public static SelectLocationDiscoverFragment newInstance(@Nullable LoadingFragment callingLoader, String accessToken, String IID) {
        SelectLocationDiscoverFragment discoverFragmentSelectLocation = new SelectLocationDiscoverFragment();
        discoverFragmentSelectLocation.accessToken = accessToken;
        discoverFragmentSelectLocation.IID = IID;
        discoverFragmentSelectLocation.callingLoader = callingLoader;
        return discoverFragmentSelectLocation;
    }

    // Prefill mode
    public static SelectLocationDiscoverFragment newInstance(@Nullable LoadingFragment callingLoader, String accessToken, String IID, String searchLocation) {
        SelectLocationDiscoverFragment discoverFragmentSelectLocation = new SelectLocationDiscoverFragment();
        discoverFragmentSelectLocation.callingLoader = callingLoader;
        discoverFragmentSelectLocation.prefill = true;
        discoverFragmentSelectLocation.accessToken = accessToken;
        discoverFragmentSelectLocation.IID = IID;
        discoverFragmentSelectLocation.searchLocation = searchLocation;
        return discoverFragmentSelectLocation;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!discovering) {
            TabLayout tabLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.tabLayout);
            Objects.requireNonNull(tabLayout.getTabAt(1)).select();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_discover_bottom_sheet, container,
                false);

        Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(Objects.requireNonNull(bottomSheet))
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        getChildFragmentManager().beginTransaction().replace(android.R.id.content, new CurationSettingsFragment()).commit();

//        TextView title = v.findViewById(R.id.discoverTitle);
//        title.setOnClickListener(view -> dismiss());
        v.findViewById(R.id.closeDiscover).setOnClickListener(view -> dismiss());
        TextInputEditText editText = v.findViewById(R.id.discoverLocationEditText);
        TextInputLayout textInputLayout = v.findViewById(R.id.discoverLocationInputLayout);
        Button button = v.findViewById(R.id.discover_button);

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        button.setOnClickListener(view1 -> {
            try {
                List<Address> addressList = geocoder.getFromLocationName(String.valueOf(editText.getText()), 1);
                String mCountryName = addressList.get(0).getCountryName();
                SharedPreferences ss = Objects.requireNonNull(getContext()).getSharedPreferences("unlocked_countries_721", 0);
                Set<String> hs = ss.getStringSet("set", new HashSet<>());
                if (!hs.contains(mCountryName)) {
                    if (mCountryName.equals("null"))
                        throw new Exception();
                    textInputLayout.setError("You have not yet unlocked 721 in " + mCountryName);
                    Log.v("BTN", "Blocked Discover");
                    return;
                }


                discovering = true;

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(getContext());
                final int radius = sharedPreferences.getInt("radius", testRadius);
                final int daysFromNow = sharedPreferences.getInt("daysFromNow", testDaysFromNow);

                LoadingDiscoverFragment loadingFragment = LoadingDiscoverFragment.newInstance(accessToken, addressList.get(0).getFeatureName(), String.valueOf(radius), String.valueOf(daysFromNow), IID);
                Objects.requireNonNull(getFragmentManager()).beginTransaction()
                        .replace(R.id.fragmentContainer, loadingFragment).commit();

                dismiss();

            } catch (Exception e) {
                textInputLayout.setError("Ambiguous or invalid place, try 'Sheffield, UK' for example");
            }

        });


        ImageButton listUnlockedCountries = v.findViewById(R.id.listUnlockedCountriesButton);
        TextView textView = v.findViewById(R.id.unlocked_countries_label);
        textView.setOnClickListener(v1 -> {
            Intent i = new Intent(getContext(), UnlockedCountriesActivity.class);
            startActivity(i);
        });
        listUnlockedCountries.setOnClickListener(v1 -> {
            Intent i = new Intent(getContext(), UnlockedCountriesActivity.class);
            startActivity(i);
        });
        // get the views and attach the listener


        if (prefill) {
            editText.setText(searchLocation);
        }

        return v;

    }

    /**
     * This inner class is similar to FilterBottomSheetFragment's mechanism except there is
     * no need to detect settings changes or re-load an existing UI. Therefore the functionality
     * is reduced.
     */
    public static class CurationSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.event_curation_preferences, rootKey);
        }
    }

}
