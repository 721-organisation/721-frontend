package com.instant721;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.travel721.CardSwipeFragment;
import com.travel721.EventCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.travel721.Constants.API_ROOT_URL;

public class Instant721 extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.travel721.R.layout.app_link_event_fragment);
        RequestQueue requestQueue;
        DefaultRetryPolicy splashRetryPolicy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        setContentView(com.travel721.R.layout.blank_layout);
        String eventIdToDeepLink = appLinkData.getPathSegments().get(appLinkData.getPathSegments().size() - 1);
        Log.d("EID", "onCreate: EIDTDL + " + eventIdToDeepLink);
        requestQueue = Volley.newRequestQueue(this);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            final String finalIID = task.getResult().getToken();

            try {
                InputStream uis = Instant721.this.getResources().openRawResource(com.travel721.R.raw.gravestones);
                InputStream pis = Instant721.this.getResources().openRawResource(com.travel721.R.raw.mouthpiece);
                BufferedReader ubr = new BufferedReader(new InputStreamReader(uis));
                BufferedReader pbr = new BufferedReader(new InputStreamReader(pis));
                String u = ubr.readLine();
                String p = pbr.readLine();
                StringRequest getAccessTokenStringRequest = new StringRequest(Request.Method.POST, API_ROOT_URL + "Users/login",
                        response -> {
                            try {
                                // Parse JSON and get access token
                                JSONObject jo = new JSONObject(response);
                                final String accessToken = String.valueOf(jo.get("id"));

                                StringRequest getEventInfoStringRequest = new StringRequest(Request.Method.GET, API_ROOT_URL + "events?filter=%7B%22where%22%3A%7B%22eventSourceId%22%3A%22" + eventIdToDeepLink + "%22%7D%7D&access_token=" + accessToken, response1 -> {
                                    try {
                                        Log.v("RESPONSE", response1);

                                        JSONArray searchResult = new JSONArray(response1);

                                        JSONObject event = searchResult.getJSONObject(0);
                                        EventCard eventLinked = EventCard.unpackFromJson(event);
                                        ArrayList<EventCard> eventCardList = new ArrayList<>();
                                        eventCardList.add(eventLinked);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("accessToken", accessToken);
                                        bundle.putParcelableArrayList("events", eventCardList);
                                        bundle.putString("mode", "applink");
                                        bundle.putString("fiid", finalIID);

                                        Instant721.this.setContentView(com.travel721.R.layout.app_link_event_fragment);
                                        Instant721.this.getSupportFragmentManager().beginTransaction().replace(com.travel721.R.id.app_link_cardswipefragment, CardSwipeFragment.newNonBoundInstance(bundle)).commit();


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }, error -> {

                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        HashMap<String, String> reqHeaders = new HashMap<>();
                                        reqHeaders.put("content-type", "application/json; charset=utf-8");
                                        return reqHeaders;
                                    }
                                };
                                requestQueue.add(getEventInfoStringRequest);


                                Log.v("API access Token ", accessToken);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, response -> {
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        // Auth params
                        final Map<String, String> loginPOST = new HashMap<>();
                        loginPOST.put("email", u);
                        loginPOST.put("password", p);
                        return loginPOST;
                    }
                };
                getAccessTokenStringRequest.setRetryPolicy(splashRetryPolicy);
                requestQueue.add(getAccessTokenStringRequest);
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        });


    }
}
