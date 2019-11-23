package com.travel721.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.travel721.eventcaching.CacheDatabase;
import com.travel721.R;

import java.util.ArrayList;

public class LoadingNearMeFragment extends LoadingFragment {
    public static final String LOADING_NEAR_ME_REQUEST_TAG = "LOADING_NEAR_ME_REQUEST_TAG";
    public volatile ArrayList<String> tagsToFilterBy;
    String accessToken;
    String IID;
    String longitude;
    String latitude;
    String radius;
    String daysFromNow;
    ArrayList<? extends Parcelable> eventCardList = new ArrayList<>();

    // This is where to make the bundle info
    public static LoadingNearMeFragment newInstance(String accessToken, String IID, String longitude, String latitude, String radius, String daysFromNow, ArrayList<String> tagsToFilterBy) {
        LoadingNearMeFragment fragment = new LoadingNearMeFragment();
        fragment.accessToken = accessToken;
        fragment.IID = IID;
        fragment.longitude = longitude;
        fragment.latitude = latitude;
        fragment.radius = radius;
        fragment.daysFromNow = daysFromNow;
        fragment.tagsToFilterBy = tagsToFilterBy;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.blank, null);

        // set its background to our AnimationDrawable XML resource.
        ImageView img = view.findViewById(R.id.loading_dots_anim);
        img.setBackgroundResource(R.drawable.loading_dots_animation);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();


        TextView statusText = view.findViewById(R.id.status_text);
        statusText.setText("Loading cached events..");
        new Thread(() -> {
            eventCardList = (ArrayList<? extends Parcelable>) CacheDatabase.getInstance(getContext()).eventCardDao().getAll();

            Bundle bundle = new Bundle();
            bundle.putString("mode", "nearme");
            bundle.putStringArrayList("tagsToFilterBy", tagsToFilterBy);
            bundle.putString("accessToken", accessToken);
            bundle.putString("IID", IID);
            bundle.putString("longitude", longitude);
            bundle.putString("latitude", latitude);
            bundle.putString("radius", radius);
            bundle.putString("daysFromNow", daysFromNow);
            bundle.putParcelableArrayList("events", eventCardList);
            Log.v("TEST", "Swapping fragments... ");
            getFragmentManager().beginTransaction().replace(getId(), CardSwipeFragment.newInstance(bundle, this)).commit();
        }).start();
        return view;
    }


    static void splashErrorHandler(String error) {
        Log.v("HANDLEDERROR", error);
    }

    public static LoadingNearMeFragment clone(LoadingNearMeFragment toClone) {
        return LoadingNearMeFragment.newInstance(toClone.accessToken, toClone.IID, toClone.longitude, toClone.latitude, toClone.radius, toClone.daysFromNow, toClone.tagsToFilterBy);
    }


}
