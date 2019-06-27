package com.travel721;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.REQUEST_CHECK_LOCATION_SETTINGS;
import static com.travel721.Constants.REQUEST_LOCATION_PERMISSIONS;
import static com.travel721.Constants.eventProfileSearchFilter;
import static com.travel721.Constants.profileSearchURL;
import static com.travel721.Constants.testDaysFromNow;
import static com.travel721.Constants.testRadius;

/**
 * This splash activity does all loading (location, api requests)
 * for the MainActivity
 * <p>
 * It is not designed to be instantiated by itself as setContentView is not called.
 * See Initial App Loader and UpdatedSettingsSplashActivity to see why.
 *
 * @author Bhav
 */

public abstract class SplashActivity extends Activity {

    LocationRequest mLocationRequestHighAccuracy;
    // Private fields
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    /**
     * This is called when permissions are changed on the app's recommendation
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // This needs to be kept as a switch statement in case the API changes
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Location permission was granted, yay!
                    //Snackbar.make(findViewById(R.id.loading_spinner_view), "Thanks! Getting your location now...", Snackbar.LENGTH_SHORT).show();
                    statusText.setText("Thanks! Getting your location now...");
                    Log.v("DOLOAD", "Called from onRPR");
                    doLoad();
                } else {
                    // Location permission denied, boo!
                    // Lazy way of asking again
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
            // other 'case' lines to check for other permissions this app might request go here.
        }
    }

    TextSwitcher statusText;

    // TODO permission first, then change settings - not the other way around
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusText = findViewById(R.id.statusText);

        statusText.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(SplashActivity.this);
                tv.setTextColor(getResources().getColor(android.R.color.white));
                return tv;
            }
        });
        statusText.setInAnimation(this, R.anim.fade_in_text_switch);
        statusText.setOutAnimation(this, R.anim.fade_out_text_switch);
        // Initialise Firebase
        FirebaseApp.initializeApp(this);
        //Initialise FLP
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Review Location Settings
        mLocationRequestHighAccuracy = LocationRequest.create();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestHighAccuracy).setAlwaysShow(true);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        // If settings are correct
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All settings required are set correctly, proceed
                Log.d("doLoad", "called from OnSuccessListener");
                doLoad();
            }
        });
        // If settings need to be changed
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull final Exception e) {
                if (e instanceof ResolvableApiException) {
                    final ResolvableApiException resolvable = (ResolvableApiException) e;
                    // Location settings are not satisfied, show dialog
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SplashActivity.this);
                    dialogBuilder
                            .setTitle(getResources().getString(R.string.location_is_off_title))
                            .setMessage(getResources().getString(R.string.location_is_off_message))
                            .setPositiveButton("Change Settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    try {
                                        resolvable.startResolutionForResult(SplashActivity.this,
                                                REQUEST_CHECK_LOCATION_SETTINGS);
                                    } catch (IntentSender.SendIntentException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });
                    dialogBuilder.create().show();
                } else {
                    e.printStackTrace(); // This error is nearly impossible
                }
            }
        });
    }

    private void doLoad() {
        statusText.setText("Checking permissions...");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v("PERMS","FAIL");
            // Ask for permission
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder
                    .setTitle(getResources().getString(R.string.location_permission_required_title))
                    .setMessage(getResources().getString(R.string.location_permission_required_message))
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
            Log.v("PERMS","SUCCESS");
            // All permissions and settings satisfied, begin loading location
            // Sets the location callback
            locationCallback = new LocationCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null || locationResult.getLastLocation() == null) {
                        // No possible way to get location
                        // Show the user an error message
                        statusText.setVisibility(View.GONE);
                        Snackbar.make(findViewById(R.id.loading_spinner_view), getResources().getString(R.string.no_location_error_message), Snackbar.LENGTH_INDEFINITE)
                                .setAction(android.R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        finish();
                                    }
                                }).show();

//                        return;
                        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        final LocationListener locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                locationManager.removeUpdates(this);
                                Log.v("LOCGET","From LM");
                                registerAndGetEvents(location);
                            }

                            @Override
                            public void onStatusChanged(String s, int i, Bundle bundle) {

                            }

                            @Override
                            public void onProviderEnabled(String s) {

                            }

                            @Override
                            public void onProviderDisabled(String s) {

                            }
                        };
                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,locationListener,null);

                    } else {
                        Log.v("LOCGET","From FLP");

                        registerAndGetEvents(locationResult.getLastLocation());
                    }

                }
            };
            // Request the location update
            fusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy,
                    locationCallback,
                    null);
            Log.v("LOGGER","From UGH");
        }
    }

    void registerAndGetEvents(final Location location) {
        // Get settings from storage
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final int radius = sharedPreferences.getInt("radius", testRadius);
        final int daysFromNow = sharedPreferences.getInt("daysFromNow", testDaysFromNow);
        // We've got a location so stop receiving updates
        fusedLocationClient.removeLocationUpdates(locationCallback);

        // Extract location and show a nice message with their city/county name
        final Geocoder geocoder = new Geocoder(SplashActivity.this, Locale.getDefault());


        // Got last known location. In some rare situations this can be null.
        // NB: I've mitigated most of these rare cases.
        statusText.setText("Checking in with 721");
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
                                                        statusText.setText("Registering with 721...");
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
                                                        try {
                                                            List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                                            String city = address.get(0).getSubAdminArea();
                                                            statusText.setText("Welcome back! Loading the latest experiences in " + city + "...");
                                                        } catch (IOException e) {
                                                            statusText.setText("Welcome back! Loading the latest experiences...");
                                                        }
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

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}