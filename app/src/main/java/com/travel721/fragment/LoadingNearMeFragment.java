package com.travel721.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.travel721.R;
import com.travel721.card.Card;
import com.travel721.card.CardComparator;
import com.travel721.card.EventCard;
import com.travel721.eventcaching.CacheDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public class LoadingNearMeFragment extends LoadingFragment {
    public static final String LOADING_NEAR_ME_REQUEST_TAG = "LOADING_NEAR_ME_REQUEST_TAG";
    public volatile ArrayList<String> tagsToFilterBy;
    String accessToken;
    String IID;
    String longitude;
    String latitude;
    String radius;
    String daysFromNow;
    ArrayList<? extends Card> eventCardList = new ArrayList<>();


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

        try {
            // set its background to our AnimationDrawable XML resource.
            ImageView img = view.findViewById(R.id.loading_dots_anim);
            img.setBackgroundResource(R.drawable.loading_dots_animation);

            // Get the background, which has been compiled to an AnimationDrawable object.
            AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

            // Start the animation (looped playback by default).
            frameAnimation.start();


            TextView statusText = view.findViewById(R.id.status_text);
            statusText.setText(getString(R.string.loading_cached_events_hint));
            new Thread(() -> {

                try {
                    eventCardList = (ArrayList<? extends EventCard>) CacheDatabase.getInstance(getContext()).eventCardDao().getAll();
                    String eventCardDateFormatString = "EEEE dd MMM";
                    SimpleDateFormat sdf = new SimpleDateFormat(eventCardDateFormatString);
                    ArrayList<Card> cardsToRemoveList = new ArrayList<>();
                    for (Card c : eventCardList) {
                        if (c instanceof EventCard) {
                            Date currDate = new Date();
                            try {
                                Date formattedEventDate = sdf.parse(((EventCard) c).getPrettyDate());
                                if (currDate.compareTo(formattedEventDate) > 0) {
                                    cardsToRemoveList.add(c);
                                }
                            } catch (ParseException ignored) {

                            }

                        }
                    }
                    if (!cardsToRemoveList.isEmpty()) //noinspection SuspiciousMethodCalls
                        eventCardList.removeAll(cardsToRemoveList);
                    for (Card c : cardsToRemoveList)
                        CacheDatabase.getInstance(getContext()).eventCardDao().delete((EventCard) c);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Bundle bundle = new Bundle();
                bundle.putString("mode", "nearme");
                bundle.putStringArrayList("tagsToFilterBy", tagsToFilterBy);
                bundle.putString("accessToken", accessToken);
                bundle.putString("IID", IID);
                bundle.putString("longitude", longitude);
                bundle.putString("latitude", latitude);
                bundle.putString("radius", radius);
                bundle.putString("daysFromNow", daysFromNow);
                Collections.sort(eventCardList, new CardComparator<>());
                bundle.putParcelableArrayList("events", eventCardList);
                Log.v("TEST", "Swapping fragments... ");
                try {
                    Objects.requireNonNull(getFragmentManager()).beginTransaction().replace(getId(), CardSwipeFragment.newInstance(bundle, this)).commit();
                } catch (NullPointerException ignored) {

                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public static LoadingNearMeFragment clone(LoadingNearMeFragment toClone) {
        return LoadingNearMeFragment.newInstance(toClone.accessToken, toClone.IID, toClone.longitude, toClone.latitude, toClone.radius, toClone.daysFromNow, toClone.tagsToFilterBy);
    }


    public void beginTutorial() {
    }
}
