package com.travel721;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.eventProfileAllSearchFilter;
import static com.travel721.Constants.profileSearchURL;

public class LoadingNearMeFragment extends LoadingFragment {
    public static final String LOADING_NEAR_ME_REQUEST_TAG = "LOADING_NEAR_ME_REQUEST_TAG";
    String accessToken;
    String IID;
    String longitude;
    String latitude;
    String radius;
    String daysFromNow;
    private DefaultRetryPolicy splashRetryPolicy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);


    // This is where to make the bundle info
    public static LoadingNearMeFragment newInstance(String accessToken, String IID, String longitude, String latitude, String radius, String daysFromNow) {
        LoadingNearMeFragment fragment = new LoadingNearMeFragment();
        fragment.accessToken = accessToken;
        fragment.IID = IID;
        fragment.longitude = longitude;
        fragment.latitude = latitude;
        fragment.radius = radius;
        fragment.daysFromNow = daysFromNow;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.blank_layout, null);
        queue = Volley.newRequestQueue(getContext());
        // set its background to our AnimationDrawable XML resource.
        ImageView img = view.findViewById(R.id.loading_dots_anim);
        img.setBackgroundResource(R.drawable.loading_dots_animation);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();

        doLoad();
        TextView statusText = view.findViewById(R.id.status_text);
        statusText.setText("Waiting for geocoder..");
        return view;
    }

    RequestQueue queue;

    private void doLoad() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {

            final ArrayList<EventCard> eventsFound = new ArrayList<>();

            final String finalIID = Objects.requireNonNull(task.getResult()).getToken();
            Log.v("FIID", finalIID);


            // Encoded URL for profile search

            Log.v("API access Token ", accessToken);
            Log.v("IID", finalIID);
            Log.v("REQUEST", "Checking profile");
            // GET REQUEST: Does profile exist?
            StringRequest stringRequest1 = new StringRequest(Request.Method.GET, API_ROOT_URL + "profiles" + profileSearchURL(finalIID) + "&access_token=" + accessToken, response15 -> {
                if (isDetached()) {
                    return;
                }
                try {
                    JSONArray profilesResponse = new JSONArray(response15);
                    boolean userExists = false;
                    if (profilesResponse.isNull(0)) {
                        // User does not exist. This condition definitely needs testing
                        Log.v("USERS", "User not found, creating...");
                        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, API_ROOT_URL + "profiles?access_token=" + accessToken, response14 -> {
                            if (isDetached()) {
                                return;
                            }
                            Log.v("USERS", "User created");
                        }, error -> {

                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                map.put("profileId", finalIID);
                                return map;
                            }
                        };

                        stringRequest2.setRetryPolicy(splashRetryPolicy);
                        stringRequest2.setTag(LOADING_NEAR_ME_REQUEST_TAG);
                        queue.add(stringRequest2);
                    } else {
                        userExists = true;
                    }
                    if (isDetached()) {
                        return;
                    }
                    TextView statusTextView = getView().findViewById(R.id.status_text);
                    final Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> address = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                        String city = address.get(0).getSubAdminArea();
                        statusTextView.setText(getContext().getString(R.string.geocoded_welcome, city));
                    } catch (IOException e) {
                        statusTextView.setText(getContext().getString(R.string.failed_geocoding_welcome));
                    }
                    // PUT REQUEST: Update events on server
                    Log.v("Requests", "Updating events on server...");
                    StringRequest stringRequest3 = new StringRequest(Request.Method.PUT, API_ROOT_URL + "events/updateNew?access_token=" + accessToken, (String response13) -> {
                        if (isDetached()) {
                            return;
                        }
                        // GET REQUEST: Get events from the server
                        StringRequest stringRequest4 = new StringRequest(Request.Method.GET, API_ROOT_URL + "events/getWithinDistance?latitude=" + latitude + "&longitude=" + longitude + "&radius=" + radius + "&daysFromNow=" + daysFromNow + "&access_token=" + accessToken + "&explore=false", response12 -> {
                            if (isDetached()) {
                                return;
                            }
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
                                            if (isDetached()) {
                                                return;
                                            }
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
                                                    statusTextView.setText(getString(R.string.nno_events_near_you));
                                                    statusTextView.setOnClickListener(view -> {
                                                        FilterBottomSheetFragment filterBottomSheetFragment = FilterBottomSheetFragment.newInstance(this);
                                                        filterBottomSheetFragment.show(getFragmentManager(),
                                                                "filter_sheet_fragment");
                                                    });
                                                    ImageView iv = getView().findViewById(R.id.loading_dots_anim);
                                                    iv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_warning));
                                                    iv.setBackground(null);
                                                    return;
                                                }
                                                Bundle bundle = new Bundle();
                                                bundle.putParcelableArrayList("events", filteredCards);
                                                bundle.putString("accessToken", accessToken);
                                                bundle.putString("fiid", finalIID);

                                                Log.v("TEST", "Swapping fragments... ");
                                                getFragmentManager().beginTransaction().replace(getId(), CardSwipeFragment.newInstance(bundle, this)).commit();
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
                                stringRequest5.setTag(LOADING_NEAR_ME_REQUEST_TAG);
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
                        stringRequest4.setTag(LOADING_NEAR_ME_REQUEST_TAG);
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
                            params.put("latitude", latitude);
                            params.put("longitude", longitude);
                            params.put("explore", "false");
                            params.put("radius", String.valueOf(radius));
                            params.put("daysFromNow", String.valueOf(daysFromNow));
                            return params;
                        }
                    };
                    stringRequest3.setRetryPolicy(splashRetryPolicy);
                    stringRequest3.setTag(LOADING_NEAR_ME_REQUEST_TAG);
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
            stringRequest1.setTag(LOADING_NEAR_ME_REQUEST_TAG);
            queue.add(stringRequest1);


        });
        if (isDetached()) {
            queue.cancelAll(LOADING_NEAR_ME_REQUEST_TAG);
            queue.stop();
        }
    }

    static void splashErrorHandler(String error) {
        Log.v("HANDLEDERROR", error);
    }

    public static LoadingNearMeFragment clone(LoadingNearMeFragment toClone) {
        return LoadingNearMeFragment.newInstance(toClone.accessToken, toClone.IID, toClone.longitude, toClone.latitude, toClone.radius, toClone.daysFromNow);
    }

    String TAG = "DBUGGING DETACH";

    @Override
    public void onDetach() {
        super.onDetach();
        queue.cancelAll(LOADING_NEAR_ME_REQUEST_TAG);
        queue.stop();
        Log.d(TAG, "onDetach() called in " + this.getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        queue.cancelAll(LOADING_NEAR_ME_REQUEST_TAG);
        queue.stop();
        Log.d(TAG, "onDestroyView() called" + this.getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        queue.cancelAll(LOADING_NEAR_ME_REQUEST_TAG);
        queue.stop();
        Log.d(TAG, "onDestroy() called" + this.getClass().getSimpleName());
    }


}
