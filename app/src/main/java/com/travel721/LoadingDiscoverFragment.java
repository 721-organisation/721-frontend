package com.travel721;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;

public class LoadingDiscoverFragment extends LoadingFragment {
    String accessToken;
    String searchLocation;
    String radius;
    String daysFromNow;
    private DefaultRetryPolicy splashRetryPolicy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    public static LoadingDiscoverFragment newInstance(String accessToken, String searchLocation, String radius, String daysFromNow) {
        LoadingDiscoverFragment fragment = new LoadingDiscoverFragment();
        fragment.accessToken = accessToken;
        fragment.searchLocation = searchLocation;
        fragment.radius = radius;
        fragment.daysFromNow = daysFromNow;
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = getLayoutInflater().inflate(R.layout.blank_layout, null);
        TextView textView = view.findViewById(R.id.status_text);
        textView.setText("Finding events near " + searchLocation);
        // set its background to our AnimationDrawable XML resource.
        ImageView img = (ImageView) view.findViewById(R.id.loading_dots_anim);
        img.setBackgroundResource(R.drawable.loading_dots_animation);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();

        return view;
    }

    public static LoadingDiscoverFragment clone(LoadingDiscoverFragment toClone) {
        return LoadingDiscoverFragment.newInstance(toClone.accessToken, toClone.searchLocation, toClone.radius, toClone.daysFromNow);
    }
}

