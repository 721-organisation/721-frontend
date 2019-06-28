package com.travel721;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.eventProfileSearchFilter;
import static com.travel721.Constants.eventSearchFilter;

public class ListEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_list_events);

        final String api_access_token = getIntent().getStringExtra("access_token");
        final Context c = this;
        final LinearLayout linearLayout = findViewById(R.id.eventListCardHolder);
        Snackbar.make(linearLayout, "Loading your picks...", Snackbar.LENGTH_LONG).show();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                // Instantiate the RequestQueue.
                final RequestQueue queue = Volley.newRequestQueue(c);
                String fiid = task.getResult().getToken();
                String url = API_ROOT_URL + "eventProfiles?access_token=" + api_access_token + "&filter=" + eventProfileSearchFilter(fiid);
                // Request a string response from the provided URL.
                final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                Log.v("RES", response);
//                                List<EventCard> eventCards = getIntent().getParcelableArrayListExtra("events");

                                try {
                                    JSONArray eventProfileArray = new JSONArray(response);
                                    JSONObject jsonObject;
                                    for (int i = 0; i < eventProfileArray.length(); i++) {
                                        jsonObject = eventProfileArray.getJSONObject(i);
                                        String eventID = jsonObject.getString("eventSourceId");
                                        String getEventUrl = API_ROOT_URL + "events?access_token=" + api_access_token + "&filter=" + eventSearchFilter(eventID);
                                        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, getEventUrl,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONArray eventsFound = new JSONArray(response);
                                                            if (eventsFound.length() == 1) {
                                                                JSONObject eventToShow = eventsFound.getJSONObject(0);
                                                                EventCard eventCard = EventCard.unpackFromJson(eventToShow);

                                                                View card;
                                                                card = getLayoutInflater().inflate(R.layout.event_list_card, null);
                                                                ImageView imageView = card.findViewById(R.id.eventCardImage);
                                                                CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(imageView.getContext());
                                                                circularProgressDrawable.setStrokeWidth(5f);
                                                                circularProgressDrawable.setCenterRadius(30f);
                                                                circularProgressDrawable.start();
                                                                Glide.with(c)
                                                                        .load(eventCard.getImgURL())
                                                                        .placeholder(circularProgressDrawable)
                                                                        .into(imageView);
                                                                imageView.setHorizontalFadingEdgeEnabled(true);
                                                                imageView.setFadingEdgeLength(40);

                                                                TextView tv = card.findViewById(R.id.eventCardName);
                                                                tv.setText(eventCard.getName());
                                                                tv = card.findViewById(R.id.eventCardDateTime);
                                                                tv.setText(eventCard.getFormattedDate() + " " + eventCard.getTime());
                                                                tv = card.findViewById(R.id.eventCardVenue);
                                                                tv.setText(eventCard.getVenueName());
                                                                card.setOnClickListener(view -> {
                                                                    Intent i = new Intent(ListEventsActivity.this, EventMoreInfoActivity.class);
                                                                    i.putExtra("lat", eventCard.getLocationLatitude());
                                                                    i.putExtra("lon", eventCard.getLocationLongitude());
                                                                    i.putExtra("desc", eventCard.getDescription());
                                                                    i.putExtra("URL", eventCard.getEventHyperLink());
                                                                    startActivity(i);
                                                                    overridePendingTransition(R.anim.slide_in_from_top, 0);
                                                                });
                                                                AnalyticsHelper.logEvent(ListEventsActivity.this, AnalyticsHelper.USER_CLICKS_EVENT_IN_LIKED_EVENT_LIST, null);
                                                                linearLayout.addView(card);

                                                            }
                                                            Log.v("Inner Request", response);
                                                        } catch (JSONException je) {
                                                            Log.e("OOPS", je.getLocalizedMessage());
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });
                                        queue.add(stringRequest1);
                                    }
                                    ;
//TODO reimplement this part
//                                    LinearLayout linearLayout = findViewById(R.id.eventListCardHolder);
//                                    View card;
//                                        //LinearLayout layout = (LinearLayout) findViewById(R.id.info);
//                                        card = getLayoutInflater().inflate(R.layout.event_list_card, null);
//                                        ImageView imageView = card.findViewById(R.id.eventCardImage);
//                                        Glide.with(c)
//                                                .load(e.getImgURL())
//                                                .placeholder(R.drawable.loading_spinner)
//                                                .into(imageView);
//                                        imageView.setHorizontalFadingEdgeEnabled(true);
//                                        imageView.setFadingEdgeLength(40);
//
//                                        TextView tv = card.findViewById(R.id.eventCardName);
//                                        tv.setText(e.getName());
//                                        tv = card.findViewById(R.id.eventCardDateTime);
//                                        tv.setText(e.getFormattedDate() + " " + e.getTime());
//                                        tv = card.findViewById(R.id.eventCardVenue);
//                                        tv.setText(e.getVenueName());
//                                        linearLayout.addView(card);
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


    }
}
