package com.travel721;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import static com.travel721.Constants.REQUEST_CHECK_LOCATION_SETTINGS;
import static com.travel721.Constants.REQUEST_LOCATION_PERMISSIONS;
import static com.travel721.Constants.eventProfileSearchFilter;
import static com.travel721.Constants.profileSearchURL;
import static com.travel721.Constants.testDaysFromNow;
import static com.travel721.Constants.testRadius;

public class SplashActivity extends Activity {

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    doLoad();
                } else {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    LocationRequest locationRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        createLocationRequest();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        final Activity activity = this;

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                doLoad();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity,
                                REQUEST_CHECK_LOCATION_SETTINGS);

                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        Toast.makeText(activity, "An unresolvable error occured", Toast.LENGTH_SHORT).show();
                        Log.e("ERR", "unresolvable");
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("Result received", "" + requestCode + " " + resultCode);
        if (requestCode == REQUEST_CHECK_LOCATION_SETTINGS) {
            if (resultCode == LocationSettingsStatusCodes.SUCCESS || resultCode == LocationSettingsStatusCodes.SUCCESS_CACHE) {
                // Carry on with the loading
                doLoad();
            } else if (resultCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                // Well that didn't work
                Toast.makeText(this, "721 requires your location to continue", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    void doLoad() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final int radius = sharedPreferences.getInt("radius", testRadius);
        final int daysFromNow = sharedPreferences.getInt("daysFromNow", testDaysFromNow);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder
                    .setMessage("721 needs your location to show you events near you")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS);
                            }

                        }
                    });
            dialogBuilder.create().show();
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
                                            Log.v("FIID", finalIID);
                                            // Get an access token for the API

                                            // Auth params
                                            final Map<String, String> loginPOST = new HashMap<String, String>();
                                            loginPOST.put("email", u);
                                            loginPOST.put("password", p);
                                            // Get access token
                                            // POST REQUEST: Authenticate
                                            queue.add(new StringRequest(Request.Method.POST, API_ROOT_URL + "Users/login",
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {
                                                                // Parse JSON and get access token
                                                                JSONObject jo = new JSONObject(response);
                                                                final String accessToken = String.valueOf(jo.get("id"));
                                                                // Encoded URL for profile search

                                                                Log.v("API access Token ", accessToken);
                                                                Log.v("IID", finalIID);
                                                                Log.v("REQUEST", "Checking profile");
                                                                // GET REQUEST: Does profile exist?
                                                                queue.add(new StringRequest(Request.Method.GET, API_ROOT_URL + "profiles" + profileSearchURL(finalIID) + "&access_token=" + accessToken, new Response.Listener<String>() {
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
                                                                            Log.v("Requests", "Updating events on server...");
                                                                            queue.add(new StringRequest(Request.Method.PUT, API_ROOT_URL + "events/updateNew?access_token=" + accessToken, new Response.Listener<String>() {
                                                                                @Override
                                                                                public void onResponse(String response) {
                                                                                    // GET REQUEST: Get events from the server
                                                                                    queue.add(new StringRequest(Request.Method.GET, API_ROOT_URL + "events/getWithinDistance?latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude() + "&radius=" + radius + "&daysFromNow=" + daysFromNow + "&access_token=" + accessToken, new Response.Listener<String>() {
                                                                                        @Override
                                                                                        public void onResponse(String response) {
                                                                                            try {
                                                                                                JSONObject jo = new JSONObject(response);
                                                                                                JSONArray events = jo.getJSONArray("getWithinDistance");

                                                                                                for (int i = 0; i < events.length(); i++) {
                                                                                                    JSONObject event = events.getJSONObject(i);
                                                                                                    eventsFound.add(EventCard.unpackFromJson(event));

                                                                                                }

                                                                                                // Filter events already swiped through
                                                                                                // Request a string response from the provided URL.
                                                                                                Log.v("Requests", "Filtering through events already swiped through");
                                                                                                queue.add(new StringRequest(Request.Method.GET, API_ROOT_URL + "eventProfiles?access_token=" + accessToken + "&filter=" + eventProfileSearchFilter(finalIID),
                                                                                                        new Response.Listener<String>() {
                                                                                                            @Override
                                                                                                            public void onResponse(String response) {
                                                                                                                try {
                                                                                                                    final JSONArray eventProfileArray = new JSONArray(response);
                                                                                                                    JSONObject jsonObject;
                                                                                                                    ArrayList<String> alreadySwipedIDs = new ArrayList<>();
                                                                                                                    for (int i = 0; i < eventProfileArray.length(); i++) {
                                                                                                                        jsonObject = eventProfileArray.getJSONObject(i);
                                                                                                                        String eventID = jsonObject.getString("eventSourceId");
                                                                                                                        alreadySwipedIDs.add(eventID);
                                                                                                                    }
                                                                                                                    ArrayList<EventCard> filteredCards = new ArrayList<>();
                                                                                                                    for (EventCard e : eventsFound) {
                                                                                                                        if (!alreadySwipedIDs.contains(e.getEventSourceID())) {
                                                                                                                            filteredCards.add(e);
                                                                                                                        }
                                                                                                                    }

                                                                                                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                                                                                                    intent.putParcelableArrayListExtra("events", filteredCards);
                                                                                                                    intent.putExtra("accessToken", accessToken);
                                                                                                                    intent.putExtra("fiid", finalIID);
                                                                                                                    startActivity(intent);
                                                                                                                    finish();

                                                                                                                } catch (JSONException je) {
                                                                                                                    je.printStackTrace();
                                                                                                                }
                                                                                                            }


                                                                                                        },
                                                                                                        new Response.ErrorListener() {
                                                                                                            @Override
                                                                                                            public void onErrorResponse(VolleyError error) {

                                                                                                            }
                                                                                                        }));
                                                                                            } catch (
                                                                                                    JSONException e) {
                                                                                                e.printStackTrace();
                                                                                            }


                                                                                        }
                                                                                    }, new Response.ErrorListener() {
                                                                                        @Override
                                                                                        public void onErrorResponse
                                                                                                (VolleyError
                                                                                                         error) {
                                                                                            // TODO create error activity
                                                                                        }
                                                                                    }));
                                                                                }
                                                                            },
                                                                                    new Response.ErrorListener() {
                                                                                        @Override
                                                                                        public void onErrorResponse(VolleyError error) {

                                                                                        }
                                                                                    }) {
                                                                                @Override
                                                                                protected Map<String, String> getParams
                                                                                        () throws
                                                                                        AuthFailureError {

                                                                                    Map<String, String> params = new HashMap<>();
                                                                                    params.put("latitude", String.valueOf(location.getLatitude()));
                                                                                    params.put("longitude", String.valueOf(location.getLongitude()));

                                                                                    params.put("radius", String.valueOf(radius));
                                                                                    params.put("daysFromNow", String.valueOf(daysFromNow));
                                                                                    return params;
                                                                                }
                                                                            });


                                                                        } catch (
                                                                                JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }, new Response.ErrorListener() {
                                                                    @Override
                                                                    public void onErrorResponse(VolleyError error) {

                                                                    }
                                                                }));


                                                            } catch (
                                                                    JSONException e) {
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
                                            });

                                        } catch (
                                                IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                // Logic to handle null location object
                                Toast.makeText(getBaseContext(), "We couldn't get your location, please enable location and try again.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });

        }

    }
}