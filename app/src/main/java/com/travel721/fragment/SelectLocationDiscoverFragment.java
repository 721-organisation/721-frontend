package com.travel721.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.travel721.R;

import java.util.Objects;

public class SelectLocationDiscoverFragment extends RoundedBottomSheetDialogFragment {
    boolean discovering = false;
    private String accessToken;
    private String IID;

    public static SelectLocationDiscoverFragment newInstance(int discoverFragment, String accessToken, String IID) {
        SelectLocationDiscoverFragment discoverFragmentSelectLocation = new SelectLocationDiscoverFragment();
        discoverFragmentSelectLocation.accessToken = accessToken;
        discoverFragmentSelectLocation.IID = IID;
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
        SeekBar daysSeekBar = v.findViewById(R.id.daysSeekBar);
        SeekBar radiusSeekBar = v.findViewById(R.id.radiusSeekBar);
        TextView radTextView = v.findViewById(R.id.radiusTextView);
        EditText editText = v.findViewById(R.id.editText);
        TextView textView = v.findViewById(R.id.textView5);
        textView.setText(getResources().getQuantityString(R.plurals.up_to_days_from_today, 5, 5));
        daysSeekBar.setProgress(4);
        radiusSeekBar.setProgress(4);
        radTextView.setText(getResources().getQuantityString(R.plurals.search_x_miles, 5, 5));
        Button button = v.findViewById(R.id.discover_button);
        daysSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText(getResources().getQuantityString(R.plurals.up_to_days_from_today, i + 1, i + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radTextView.setText(getResources().getQuantityString(R.plurals.search_x_miles, i + 1, i + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        button.setOnClickListener(view1 -> {
            discovering = true;
            LoadingDiscoverFragment loadingFragment = LoadingDiscoverFragment.newInstance(accessToken, editText.getText().toString(), String.valueOf(radiusSeekBar.getProgress() + 1), String.valueOf(daysSeekBar.getProgress() + 1), IID);
            Objects.requireNonNull(getFragmentManager()).beginTransaction()
                    .replace(R.id.fragmentContainer, loadingFragment).commit();
            dismiss();
        });
        // get the views and attach the listener

        return v;

    }
}
