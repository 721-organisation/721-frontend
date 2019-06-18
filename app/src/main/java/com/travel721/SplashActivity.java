package com.travel721;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.profileSearchURL;
import static com.travel721.Constants.testDaysFromNow;
import static com.travel721.Constants.testLat;
import static com.travel721.Constants.testLong;
import static com.travel721.Constants.testRadius;

public class SplashActivity extends Activity {

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Ask for permission
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder
                        .setMessage("721 needs your location to show you events near you")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);

                            }
                        });
                dialogBuilder.create().show();
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
            } else {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(final Location location) {
                                if (location != null) {
                                    // Got last known location. In some rare situations this can be null.
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
                                                final String finalIID = task.getResult().getToken();

                                                // Get an access token for the API

                                                // Auth params
                                                final Map<String, String> loginPOST = new HashMap<String, String>();
                                                loginPOST.put("email", u);
                                                loginPOST.put("password", p);
                                                // Get access token
                                                // POST REQUEST: Authenticate
                                                final StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ROOT_URL + "Users/login",
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    // Parse JSON and get access token
                                                                    JSONObject jo = new JSONObject(response);
                                                                    final String accessToken = String.valueOf(jo.get("id"));
                                                                    // Encoded URL for profile search

                                                                    Log.v("Token ", accessToken);
                                                                    Log.v("IID", finalIID);
                                                                    // GET REQUEST: Does profile exist?
                                                                    queue.add(new StringRequest(Request.Method.GET, API_ROOT_URL + "profiles?" + profileSearchURL(finalIID) + "&access_token=" + accessToken, new Response.Listener<String>() {
                                                                        @Override
                                                                        public void onResponse(String response) {
                                                                            try {
                                                                                JSONArray profilesResponse = new JSONArray(response);
                                                                                if (profilesResponse.isNull(0)) {
                                                                                    // User does not exist. This condition definitely needs testing
                                                                                    Log.v("USERS", "User not found, creating...");
                                                                                    queue.add(new StringRequest(Request.Method.POST, API_ROOT_URL + "profiles?access_token=" + accessToken, new Response.Listener<String>() {
                                                                                        @Override
                                                                                        public void onResponse(String response) {
                                                                                            Log.v("USERS", "User created");

                                                                                        }
                                                                                    }, new Response.ErrorListener() {
                                                                                        @Override
                                                                                        public void onErrorResponse(VolleyError error) {

                                                                                        }
                                                                                    }) {
                                                                                        @Override
                                                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                                                            Map<String, String> map = new HashMap<>();
                                                                                            map.put("profileId", finalIID);
                                                                                            return map;
                                                                                        }
                                                                                    });

                                                                                } else {
                                                                                    // User exists, don't need to do anything yet
                                                                                }
                                                                                // PUT REQUEST: Update events on server
                                                                                queue.add(new StringRequest(Request.Method.PUT, API_ROOT_URL + "events/updateNew?access_token=" + accessToken, new Response.Listener<String>() {
                                                                                    @Override
                                                                                    public void onResponse(String response) {
                                                                                        // GET REQUEST: Get events from the server
                                                                                        queue.add(new StringRequest(Request.Method.GET, API_ROOT_URL + "events/getWithinDistance?latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude() + "&radius=" + 4 + "&daysFromNow=" + 4 + "&access_token=" + accessToken, new Response.Listener<String>() {
                                                                                            @Override
                                                                                            public void onResponse(String response) {
                                                                                                Log.v("RES", response);
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
                                                                                    }
                                                                                }, new Response.ErrorListener() {
                                                                                    @Override
                                                                                    public void onErrorResponse(VolleyError error) {

                                                                                    }
                                                                                }) {
                                                                                    @Override
                                                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                                                        SharedPreferences sharedPreferences =
                                                                                                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                                                                        int radius = sharedPreferences.getInt("radius", testRadius);
                                                                                        int daysFromNow = sharedPreferences.getInt("daysFromNow", testDaysFromNow);
                                                                                        Map<String, String> params = new HashMap<>();
                                                                                        params.put("latitude", String.valueOf(testLat));
                                                                                        params.put("longitude", String.valueOf(testLong));

                                                                                        params.put("radius", String.valueOf(radius));
                                                                                        params.put("daysFromNow", String.valueOf(daysFromNow));
                                                                                        return params;
                                                                                    }
                                                                                });


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
                                                    protected Map<String, String> getParams() {
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
                                } else {
                                    // Logic to handle location object
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getBaseContext());
                                    dialogBuilder
                                            .setMessage("We couldn't get your location, please enable location and try again.")
                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    finish();

                                                }
                                            });
                                    dialogBuilder.create().show();
                                }


                            }
                        });

            }

        }

    }
}