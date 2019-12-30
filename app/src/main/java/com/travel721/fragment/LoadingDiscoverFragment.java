package com.travel721.fragment;

import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.travel721.R;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class LoadingDiscoverFragment extends LoadingFragment {
    private String accessToken;
    private String searchLocation;
    String radius;
    String daysFromNow;
    private String IID;
    private String minDays;
    private int selectedChipResID;


    public static LoadingDiscoverFragment newInstance(String accessToken, String searchLocation, String radius, int selectedChipResID, String minDays, String daysFromNow, String IID) {

        LoadingDiscoverFragment fragment = new LoadingDiscoverFragment();
        fragment.accessToken = accessToken;
        fragment.searchLocation = searchLocation;
        fragment.radius = radius;
        fragment.daysFromNow = daysFromNow;
        fragment.selectedChipResID = selectedChipResID;
        fragment.minDays = minDays;
        fragment.IID = IID;
        return fragment;


    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.blank, null);
        TextView textView = view.findViewById(R.id.status_text);
        textView.setText("Finding events near " + searchLocation);
        // set its background to our AnimationDrawable XML resource.
        ImageView img = view.findViewById(R.id.loading_dots_anim);
        img.setBackgroundResource(R.drawable.loading_dots_animation);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
        Bundle bundle = new Bundle();
        bundle.putString("mode", "discover");
        bundle.putString("accessToken", accessToken);
        bundle.putString("searchLocation", searchLocation);
        bundle.putString("IID", IID);
        bundle.putInt("selectedChip", selectedChipResID);
        bundle.putString("radius", radius);
        bundle.putString("daysFromNow", daysFromNow);
        bundle.putString("minDays", minDays);
        Log.v("TEST", "Swapping fragments... ");
        Objects.requireNonNull(getFragmentManager()).beginTransaction().replace(getId(), CardSwipeFragment.newInstance(bundle, this)).commit();
        return view;
    }

    public static LoadingDiscoverFragment clone(LoadingDiscoverFragment toClone) {
        return LoadingDiscoverFragment.newInstance(toClone.accessToken, toClone.searchLocation, toClone.radius, toClone.selectedChipResID, toClone.minDays, toClone.daysFromNow, toClone.IID);
    }
}

