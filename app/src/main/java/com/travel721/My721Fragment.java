package com.travel721;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.eventProfileLikedSearchFilter;
import static com.travel721.Constants.eventSearchFilter;

public class My721Fragment extends Fragment {
    String api_access_token;

    // This is where to make the bundle info
    public static My721Fragment newInstance(Bundle bundle) {
        My721Fragment lef = new My721Fragment();
        lef.setArguments(bundle);
        return lef;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api_access_token = getArguments().getString("accessToken");

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_721, container, false);


        final LinearLayout linearLayout = root.findViewById(R.id.eventListCardHolder);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                // Instantiate the RequestQueue.
                final RequestQueue queue = Volley.newRequestQueue(getContext());
                String fiid = task.getResult().getToken();
                String url = API_ROOT_URL + "eventProfiles?access_token=" + api_access_token + "&filter=" + eventProfileLikedSearchFilter(fiid);
                // Request a string response from the provided URL.
                final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                Log.v("RES", response);
//                                List<EventCard> cardArrayList = getIntent().getParcelableArrayListExtra("events");

                                try {
                                    JSONArray eventProfileArray = new JSONArray(response);
                                    int arrSize = eventProfileArray.length();
                                    JSONObject jsonObject;
                                    queue.stop();
                                    ArrayList<EventCard> eventCardArrayList = new ArrayList<>();
                                    for (int i = 0; i < eventProfileArray.length(); i++) {
                                        jsonObject = eventProfileArray.getJSONObject(i);
                                        String eventID = jsonObject.getString("eventSourceId");
                                        String getEventUrl = API_ROOT_URL + "events?access_token=" + api_access_token + "&filter=" + eventSearchFilter(eventID);
                                        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, getEventUrl,
                                                response1 -> {
                                                    try {
                                                        JSONArray eventsFound = new JSONArray(response1);
                                                        if (eventsFound.length() == 1) {
                                                            JSONObject eventToShow = eventsFound.getJSONObject(0);
                                                            EventCard eventCard = EventCard.unpackFromJson(eventToShow);
                                                            eventCardArrayList.add(eventCard);

                                                        }
                                                        Log.v("Inner Request", response1);
                                                    } catch (JSONException je) {
                                                        Log.e("OOPS", je.getLocalizedMessage());
                                                    }
                                                }, error -> {

                                        });
                                        queue.add(stringRequest1);
                                    }
                                    AtomicInteger dealtSize = new AtomicInteger();
                                    queue.addRequestFinishedListener(request -> {
                                        dealtSize.getAndIncrement();
                                        if (dealtSize.get() < arrSize + 1 || isDetached()) {
                                            return;
                                        }
                                        Collections.sort(eventCardArrayList);
                                        if (!PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("eventListOrder", false))
                                            Collections.reverse(eventCardArrayList);
                                        String previousDateTag = "";

                                        for (int i = 0; i < eventCardArrayList.size(); i++) {
                                            boolean requireDateTag = false;
                                            if (i == 0) requireDateTag = true;

                                            View card;
                                            card = getLayoutInflater().inflate(R.layout.event_list_card, null);
                                            ImageView imageView = card.findViewById(R.id.eventCardImage);
                                            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(imageView.getContext());
                                            circularProgressDrawable.setStrokeWidth(5f);
                                            circularProgressDrawable.setCenterRadius(30f);
                                            circularProgressDrawable.start();
                                            Glide.with(getContext())
                                                    .load(eventCardArrayList.get(i).getImgURL())
                                                    .placeholder(circularProgressDrawable)
                                                    .into(imageView);
                                            imageView.setHorizontalFadingEdgeEnabled(true);
                                            imageView.setFadingEdgeLength(40);

                                            TextView tv = card.findViewById(R.id.eventCardName);
                                            tv.setText(eventCardArrayList.get(i).getName());
                                            tv = card.findViewById(R.id.eventCardDateTime);
                                            tv.setText(eventCardArrayList.get(i).getTime());
                                            tv = card.findViewById(R.id.eventCardVenue);
                                            tv.setText(eventCardArrayList.get(i).getVenueName());
                                            int finalI = i;
                                            card.setOnClickListener(view -> {
                                                Intent intent = new Intent(getContext(), EventMoreInfoActivity.class);
                                                intent.putExtra("eventCard", (Parcelable) eventCardArrayList.get(finalI));
                                                startActivity(intent);
                                            });
                                            AnalyticsHelper.logEvent(getContext(), AnalyticsHelper.USER_CLICKS_EVENT_IN_LIKED_EVENT_LIST, null);

                                            if (requireDateTag || !eventCardArrayList.get(i).getPrettyDate().equals(previousDateTag)) {
                                                View dateTag = getLayoutInflater().inflate(R.layout.date_tag, null);
                                                TextView dateTagTextView = dateTag.findViewById(R.id.dateTag);
                                                dateTagTextView.setText(eventCardArrayList.get(i).getPrettyDate());
                                                linearLayout.addView(dateTag);
                                            }
                                            linearLayout.addView(card);
                                            previousDateTag = eventCardArrayList.get(i).getPrettyDate();
                                        }
                                    });
                                    queue.start();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);


            }
        });
        root.findViewById(R.id.settingsButton).setOnClickListener(v -> {
            Intent i = new Intent(getContext(), SettingsActivity.class);
            startActivity(i);

        });
        return root;
    }
}
