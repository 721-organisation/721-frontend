package com.travel721;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.eventProfileAllSearchFilter;
import static com.travel721.Constants.profileSearchURL;

public class LoadingDiscoverFragment extends Fragment {
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
        doLoad();
        return view;
    }

    void doLoad() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {

            final ArrayList<EventCard> eventsFound = new ArrayList<>();
            final RequestQueue queue = Volley.newRequestQueue(getContext());
            final String finalIID = task.getResult().getToken();
            Log.v("FIID", finalIID);


            // Encoded URL for profile search

            Log.v("API access Token ", accessToken);
            Log.v("IID", finalIID);
            Log.v("REQUEST", "Checking profile");
            // GET REQUEST: Does profile exist?
            StringRequest stringRequest1 = new StringRequest(Request.Method.GET, API_ROOT_URL + "profiles" + profileSearchURL(finalIID) + "&access_token=" + accessToken, response15 -> {
                try {
                    JSONArray profilesResponse = new JSONArray(response15);
                    boolean userExists = false;
                    if (profilesResponse.isNull(0)) {
                        // User does not exist. This condition definitely needs testing
                        Log.v("USERS", "User not found, creating...");
                        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, API_ROOT_URL + "profiles?access_token=" + accessToken, response14 -> Log.v("USERS", "User created"), error -> {

                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                map.put("profileId", finalIID);
                                return map;
                            }
                        };
                        stringRequest2.setRetryPolicy(splashRetryPolicy);
                        queue.add(stringRequest2);
                    } else {
                        userExists = true;
                    }

                    // PUT REQUEST: Update events on server
                    Log.v("Requests", "Updating events on server...");
                    StringRequest stringRequest3 = new StringRequest(Request.Method.PUT, API_ROOT_URL + "events/updateNew?access_token=" + accessToken, response13 -> {
                        // GET REQUEST: Get events from the server
                        StringRequest stringRequest4 = new StringRequest(Request.Method.GET, API_ROOT_URL + "events/getWithinDistance?latitude=0&longitude=0&radius=" + radius + "&daysFromNow=" + daysFromNow + "&access_token=" + accessToken + "&explore=true&location=" + searchLocation, response12 -> {
                            try {
                                JSONObject jo1 = new JSONObject(response12);
                                JSONArray events = jo1.getJSONArray("getWithinDistance");

                                for (int i = 0; i < events.length(); i++) {
                                    JSONObject event = events.getJSONObject(i);
                                    eventsFound.add(EventCard.unpackFromJson(event));

                                }

                                // Filter events already swiped through
                                // Request a string response from the provided URL.
                                Log.v("Requests", "Filtering through events already swiped through");
                                StringRequest stringRequest5 = new StringRequest(Request.Method.GET, API_ROOT_URL + "eventProfiles?access_token=" + accessToken + "&filter=" + eventProfileAllSearchFilter(finalIID),
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
                                                } else {
                                                    TextView textView = getView().findViewById(R.id.status_text);
                                                    textView.setText(getString(R.string.no_events_discovered));
                                                    ImageView iv = getView().findViewById(R.id.loading_dots_anim);
                                                    iv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_warning));
                                                    iv.setBackground(null);
                                                    return;
                                                }
                                                Bundle bundle = new Bundle();
                                                bundle.putParcelableArrayList("events", filteredCards);
                                                bundle.putString("accessToken", accessToken);
                                                bundle.putString("fiid", finalIID);
//                                              startActivity(intent);
//                                                                        finish();
                                                Log.v("TEST", "Swapping fragments... ");
                                                getFragmentManager().beginTransaction().replace(getId(), CardSwipeFragment.newInstance(bundle)).commit();
                                            } catch (JSONException je) {
                                                je.printStackTrace();
                                                splashErrorHandler(je.getLocalizedMessage());

                                            }
                                        },
                                        error -> {
                                            error.printStackTrace();
                                            splashErrorHandler(error.toString());
                                        });
                                stringRequest5.setRetryPolicy(splashRetryPolicy);
                                queue.add(stringRequest5);
                            } catch (
                                    JSONException e) {
                                e.printStackTrace();
                                splashErrorHandler(e.getLocalizedMessage());
                            }


                        }, error -> {
                            error.printStackTrace();
                            splashErrorHandler(error.toString());
                        });
                        stringRequest4.setRetryPolicy(splashRetryPolicy);
                        queue.add(stringRequest4);
                    },
                            error -> {
                                error.printStackTrace();
                                splashErrorHandler(error.toString());
                            }) {
                        @Override
                        protected Map<String, String> getParams
                                () throws
                                AuthFailureError {

                            Map<String, String> params = new HashMap<>();
                            params.put("latitude", String.valueOf(0));
                            params.put("longitude", String.valueOf(0));
                            params.put("explore", "true");
                            params.put("location", searchLocation);
                            params.put("radius", String.valueOf(radius));
                            params.put("daysFromNow", String.valueOf(daysFromNow));
                            return params;
                        }
                    };
                    stringRequest3.setRetryPolicy(splashRetryPolicy);
                    queue.add(stringRequest3);
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                    splashErrorHandler(e.getLocalizedMessage());
                }
            }, error -> {
                error.printStackTrace();
                splashErrorHandler(error.toString());
            });
            stringRequest1.setRetryPolicy(splashRetryPolicy);
            queue.add(stringRequest1);


        });
    }

    static void splashErrorHandler(String error) {
        Log.v("HANDLEDERROR", error);
    }
}

