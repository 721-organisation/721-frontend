package com.travel721;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

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

import static com.travel721.Constants.*;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.splash_layout);
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                InputStream uis = getResources().openRawResource(R.raw.gravestones);
                InputStream pis = getResources().openRawResource(R.raw.mouthpiece);
                BufferedReader ubr = new BufferedReader(new InputStreamReader(uis));
                BufferedReader pbr = new BufferedReader(new InputStreamReader(pis));
                try {
                    String u = ubr.readLine();
                    String p = pbr.readLine();
                    final ArrayList<EventCard> eventsFound = new ArrayList<>();
                    final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    final String finalS = FirebaseInstanceId.getInstance().getId();
                    // Get an access token for the API


                    final Map<String, String> loginPOST = new HashMap<String, String>();
                    loginPOST.put("email", u);
                    loginPOST.put("password", p);
                    final StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ROOT_URL + "Users/login",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jo = new JSONObject(response);
                                        // SOMETHING get profiles where id = id
                                        final String accessToken = String.valueOf(jo.get("id"));
                                        final String profileIdSearch = "?filter=%7B%22where%22%3A%7B%22profileId%22%3A%22" + finalS + "%22%7D%7D";
                                        queue.add(new StringRequest(Request.Method.GET, API_ROOT_URL + "profiles" + profileIdSearch + "&access_token=" + accessToken, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                // Check there's actual profiles
                                                try {
                                                    JSONArray profilesResponse = new JSONArray(response);
                                                    if (profilesResponse.isNull(0)) {
                                                        // User doesn't exist? This condition definitely needs testing
                                                        // TODO get events

                                                    } else {
                                                        // Make a POST request to add them to the database
                                                        queue.add(new StringRequest(Request.Method.POST, API_ROOT_URL + "profiles?access_token=" + accessToken, new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                Log.v("USERS", "New user profile created");
                                                                // TODO Get events

                                                            }
                                                        }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {

                                                            }
                                                        }) {
                                                            @Override
                                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                                Map<String, String> map = new HashMap<>();
                                                                map.put("profileId", finalS);
                                                                return map;
                                                            }
                                                        });
                                                    }


                                                    queue.add(new StringRequest(Request.Method.GET, API_ROOT_URL + "events/getWithinDistance?latitude=" + testLat + "&longitude=" + testLong + "&radius=" + 4 + "&daysFromNow=" + 4 + "&access_token=" + accessToken, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            Log.v("RES", response);
                                                            // TODO create new MainActivity, bundling in the events received
                                                            try {
                                                                JSONObject jo = new JSONObject(response);
                                                                JSONArray events = jo.getJSONArray("getWithinDistance");
                                                                Log.v("ImTRYING", events.toString());
                                                                for (int i = 0; i < events.length(); i++) {
                                                                    JSONObject event = events.getJSONObject(i);
                                                                    Log.v("RES", "Added an event to the list");
                                                                    eventsFound.add(EventCard.unpackFromJson(event));

                                                                }

                                                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                                                Log.v("FINISHED", "Added" + eventsFound.size() + "events");
                                                                intent.putParcelableArrayListExtra("events", eventsFound);
                                                                startActivity(intent);
                                                                finish();
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }


                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            // TODO create error activity
                                                        }
                                                    }));


                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }));


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // OOPSIE WOOPSIE
                            Log.v("OOPS", "Something something");
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            return loginPOST;
                        }
                    };

                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
