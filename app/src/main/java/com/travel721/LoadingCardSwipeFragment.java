package com.travel721;

import android.content.Intent;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.eventProfileAllSearchFilter;

public class LoadingCardSwipeFragment extends Fragment {
    Bundle bundle;
    String accessToken;
    String IID;
    private DefaultRetryPolicy splashRetryPolicy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    // This is where to make the bundle info
    public static LoadingCardSwipeFragment newInstance(String accessToken, String IID, Bundle bundle) {
        LoadingCardSwipeFragment fragment = new LoadingCardSwipeFragment();
        fragment.accessToken = accessToken;
        fragment.IID = IID;
        fragment.bundle = bundle;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.blank_layout, null);
        // set its background to our AnimationDrawable XML resource.
        ImageView img = (ImageView) view.findViewById(R.id.loading_dots_anim);
        img.setBackgroundResource(R.drawable.loading_dots_animation);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
        doLoad();
        return view;
    }

    private void doLoad() {
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        // Filter events already swiped through
        // Request a string response from the provided URL.
        Log.v("Requests", "Filtering through events already swiped through");
        StringRequest stringRequest5 = new StringRequest(Request.Method.GET, API_ROOT_URL + "eventProfiles?access_token=" + accessToken + "&filter=" + eventProfileAllSearchFilter(IID),
                response1 -> {
                    try {
                        final JSONArray eventProfileArray = new JSONArray(response1);
                        JSONObject jsonObject;
                        ArrayList<String> alreadySwipedIDs = new ArrayList<>();
                        for (int i = 0; i < eventProfileArray.length(); i++) {
                            jsonObject = eventProfileArray.getJSONObject(i);
                            String eventID = jsonObject.getString("eventSourceId");
                            alreadySwipedIDs.add(eventID);
                        }
                        ArrayList<EventCard> eventsFound = bundle.getParcelableArrayList("events");
                        ArrayList<Card> filteredCards = new ArrayList<>();
                        for (EventCard e : eventsFound) {
                            if (!alreadySwipedIDs.contains(e.getEventSourceID())) {
                                filteredCards.add(e);
                            }
                        }
                        if (filteredCards.size() > 0) {
                            filteredCards.add(new FeedbackCard());
                            filteredCards.add((filteredCards.size() / 2) + 1, new AdCard());
                            filteredCards.add((filteredCards.size() / 2) + 1, new AdCard());
                        }
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        bundle.putParcelableArrayList("events", filteredCards);
                        bundle.putString("accessToken", accessToken);
                        bundle.putString("fiid", IID);
                        intent.putExtra("fragment_bundle", bundle);

                        getFragmentManager().beginTransaction().replace(getId(), CardSwipeFragment.newInstance(bundle)).commit();

//                        startActivity(intent);


                    } catch (JSONException je) {
                        je.printStackTrace();
//                        splashErrorHandler(je.getLocalizedMessage());
                    }
                },
                error -> {
                    error.printStackTrace();
//                    splashErrorHandler(error.toString());
                });
        stringRequest5.setRetryPolicy(splashRetryPolicy);
        queue.add(stringRequest5);
    }
}
