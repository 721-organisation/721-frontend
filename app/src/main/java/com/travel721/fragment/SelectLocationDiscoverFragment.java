package com.travel721.fragment;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.travel721.R;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class SelectLocationDiscoverFragment extends RoundedBottomSheetDialogFragment {
    boolean discovering = false;
    private String accessToken;
    private String IID;
    boolean prefill = false;
    //    int daysFromNow;
    int selectedChipResId;
    int milesFromSL;
    String searchLocation;
    private int minDays = 0;
    private int maxDays = 1;

    public static SelectLocationDiscoverFragment newInstance(int discoverFragment, String accessToken, String IID) {
        SelectLocationDiscoverFragment discoverFragmentSelectLocation = new SelectLocationDiscoverFragment();
        discoverFragmentSelectLocation.accessToken = accessToken;
        discoverFragmentSelectLocation.IID = IID;
        return discoverFragmentSelectLocation;
    }

    // Prefill mode
    public static SelectLocationDiscoverFragment newInstance(int discoverFragment, String accessToken, String IID, String searchLocation, int chipSelectedRedID, int milesFromSL) {
        SelectLocationDiscoverFragment discoverFragmentSelectLocation = new SelectLocationDiscoverFragment();
        discoverFragmentSelectLocation.prefill = true;
        discoverFragmentSelectLocation.accessToken = accessToken;
        discoverFragmentSelectLocation.IID = IID;
        discoverFragmentSelectLocation.selectedChipResId = chipSelectedRedID;
        discoverFragmentSelectLocation.milesFromSL = milesFromSL;
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
        TextView title = v.findViewById(R.id.discoverTitle);
        title.setOnClickListener(view -> dismiss());
        v.findViewById(R.id.closeDiscover).setOnClickListener(view -> dismiss());
        SeekBar radiusSeekBar = v.findViewById(R.id.radiusSeekBar);
        TextView radTextView = v.findViewById(R.id.radiusTextView);
        EditText editText = v.findViewById(R.id.editText);
        TextView textView = v.findViewById(R.id.textView5);
        textView.setText(getString(R.string.days_away_hint));
        radiusSeekBar.setProgress(4);
        radTextView.setText(getString(R.string.miles_away_discover_hint));
        TextView radValTV = v.findViewById(R.id.radValTv);
        radValTV.setText(String.valueOf(5));
        Button button = v.findViewById(R.id.discover_button);

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radValTV.setText(String.valueOf(i + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        ChipGroup daysChipGroup = v.findViewById(R.id.daysChipGroup);
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        button.setOnClickListener(view1 -> {
            Log.v("BTN", "CLICKED");
            try {
                List<Address> addressList = geocoder.getFromLocationName(String.valueOf(editText.getText()), 1);
                String mCountryName = addressList.get(0).getCountryName();
                SharedPreferences ss = getContext().getSharedPreferences("unlocked_countries_721", 0);
                Set<String> hs = ss.getStringSet("set", new HashSet<String>());
                if (!hs.contains(mCountryName)) {
                    if (mCountryName.equals("null"))
                        throw new Exception();
                    Snackbar.make(getView().getRootView(), "You have not yet unlocked 721 in " + mCountryName, Snackbar.LENGTH_LONG).show();
                    Log.v("BTN", "Blocked Discover");
                    return;
                }
            } catch (Exception e) {
                Snackbar.make(getView().getRootView(), "Invalid place", Snackbar.LENGTH_LONG).show();
                return;
            }


            discovering = true;
            LoadingDiscoverFragment loadingFragment = LoadingDiscoverFragment.newInstance(accessToken, editText.getText().toString(), String.valueOf(radiusSeekBar.getProgress() + 1), daysChipGroup.getCheckedChipId(), String.valueOf(minDays), String.valueOf(maxDays), IID);
            Objects.requireNonNull(getFragmentManager()).beginTransaction()
                    .replace(R.id.fragmentContainer, loadingFragment).commit();
            dismiss();
        });
        // get the views and attach the listener


        if (prefill) {
            editText.setText(searchLocation);
            radiusSeekBar.setProgress(milesFromSL - 1);
            radValTV.setText(String.valueOf(milesFromSL));
            Chip chipToSelect = v.findViewById(selectedChipResId);
            chipToSelect.setChecked(true);

        }

        daysChipGroup.setOnCheckedChangeListener((ChipGroup group, int checkedId) -> {
            Calendar cal = Calendar.getInstance();
            int currentDay = cal.get(Calendar.DAY_OF_WEEK);
            int leftDays = Calendar.SATURDAY - currentDay;
            switch (checkedId) {
                case R.id.today_chip:
                    minDays = 0;
                    maxDays = 0;
                    break;
                case R.id.tomorrow_chip:
                    minDays = 0;
                    maxDays = 2;
                    break;
                case R.id.this_week_chip:
                    minDays = 0;
                    maxDays = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_WEEK);
                    break;
                case R.id.next_week_chip:
                    minDays = leftDays;
                    maxDays = leftDays + 7;
                    break;
                case R.id.this_month_chip:
                    minDays = 0;
                    maxDays = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
                    break;
                case R.id.next_month_chip:
                    minDays = leftDays;
                    maxDays = leftDays + 31;

                    break;

            }
        });


        return v;

    }
}
