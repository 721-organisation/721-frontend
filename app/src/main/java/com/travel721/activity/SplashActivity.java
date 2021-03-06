package com.travel721.activity;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.travel721.R;
import com.travel721.analytics.AnalyticsHelper;
import com.travel721.card.EventCard;
import com.travel721.error.SplashScreenLoadFailure;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.REQUEST_CHECK_LOCATION_SETTINGS;
import static com.travel721.Constants.REQUEST_LOCATION_PERMISSIONS;
import static com.travel721.Constants.profileSearchURL;
import static com.travel721.Constants.testDaysFromNow;
import static com.travel721.Constants.testRadius;
import static com.travel721.analytics.DebugAnalyticsEvent.DEBUG_USED_FUSED_LOCATION_PROVIDER;
import static com.travel721.analytics.DebugAnalyticsEvent.DEBUG_USED_NATIVE_LOCATION_MANAGER;

/**
 * Splash Activity to load SOME but not all information
 * required by the MainActivity in order to load
 * <p>
 * No events curation is done here.
 * <p>
 * This class is not designed to be instantiated by itself as setContentView is not called.
 * See Initial App Loader to see why.
 *
 * @author Bhav
 */

public abstract class SplashActivity extends Activity {
    private static final int APP_UPDATE_REQUEST_CODE = 420;
    private boolean UPDATE_FAIL = false;
    LocationRequest mLocationRequestHighAccuracy;
    //TextSwitcher statusText;
    // Private fields
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean executed = false;
    private DefaultRetryPolicy splashRetryPolicy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    /**
     * This is called when permissions are changed on the app's recommendation
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // This needs to be kept as a switch statement in case the API changes
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Location permission was granted, yay!
//                loadingTextView.setText("Thanks! Getting your location now...");
                Log.v("DOLOAD", "Called from onRPR");
                doLoad();
            } else {
                // Location permission denied, boo!
                // Lazy way of asking again
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
            // other 'case' lines to check for other permissions this app might request go here.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check for the integer request code originally supplied to startResolutionForResult().
        if (requestCode == REQUEST_CHECK_LOCATION_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Intent i = new Intent(this, InitialLoadSplashActivity.class);
                    startActivity(i);
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "Cannot proceed without location", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                UPDATE_FAIL = true;
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }

    // TODO permission first, then change settings - not the other way around
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        loadingTextView = findViewById(R.id.loading_text_view);

//        loadingTextView.setFactory(() -> {
//            TextView tv = new TextView(SplashActivity.this);
//            tv.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
//            tv.setGravity(Gravity.CENTER);
//             Add this
//            tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//
//            tv.setTextColor(ContextCompat.getColor(this, android.R.color.white));
//            return tv;
//        });
//        loadingTextView.setInAnimation(this, R.anim.fade_in_text_switch);
//        loadingTextView.setOutAnimation(this, R.anim.fade_out_text_switch);

        // In-app updates

        // Creates instance of the manager.
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update.
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                    && !UPDATE_FAIL) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            APP_UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
        // Initialise Firebase
        FirebaseApp.initializeApp(this);
        MobileAds.initialize(this, initializationStatus -> {
        });
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        if (sharedPreferences.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
            finish();
        }

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
        task.addOnSuccessListener(locationSettingsResponse -> {
            // All settings required are set correctly, proceed
            Log.d("doLoad", "called from OnSuccessListener");
            SplashActivity.this.doLoad();
        });
        // If settings need to be changed
        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                final ResolvableApiException resolvable = (ResolvableApiException) e;
                // Location settings are not satisfied, show dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder
                        .setTitle(getResources().getString(R.string.location_is_off_title))
                        .setMessage(getResources().getString(R.string.location_is_off_message))
                        .setPositiveButton("Change Settings", (dialogInterface, i) -> {
                            try {
                                resolvable.startResolutionForResult(SplashActivity.this,
                                        REQUEST_CHECK_LOCATION_SETTINGS);
                            } catch (IntentSender.SendIntentException e1) {
                                e1.printStackTrace();
                            }
                        });
                dialogBuilder.create().show();
            } else {
                Snackbar.make(findViewById(android.R.id.content), "721 could not attempt to get location settings. Check your settings.", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction(getString(android.R.string.ok), v -> {
                    Intent i = new Intent(this, InitialLoadSplashActivity.class);
                    startActivity(i);
                    finish();
                }).show();
            }
        });

    }

//    TextSwitcher loadingTextView;

    private void doLoad() {
//        loadingTextView.setText("Checking permissions...");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v("PERMS", "FAIL");
            // Ask for permission
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder
                    .setTitle(getResources().getString(R.string.location_permission_required_title))
                    .setMessage(getResources().getString(R.string.location_permission_required_message))
                    .setPositiveButton("Okay", (dialogInterface, i) -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS);
                        }
                    });
            dialogBuilder.create().show();
        } else {
            Log.v("PERMS", "SUCCESS");
            // All permissions and settings satisfied, begin loading location
            // Sets the location callback
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null || locationResult.getLastLocation() == null) {
                        // No possible way to get location
                        // Show the user an error message
//                        loadingTextView.setVisibility(View.GONE);
                        Snackbar.make(findViewById(R.id.loading_spinner_view), getResources().getString(R.string.no_location_error_message), Snackbar.LENGTH_INDEFINITE)
                                .setAction(android.R.string.ok, view -> finish()).show();
                    } else {
                        AnalyticsHelper.debugLogEvent(SplashActivity.this, DEBUG_USED_FUSED_LOCATION_PROVIDER, null);
                        registrationSingleExecutor(locationResult.getLastLocation());
                    }

                }
            };
            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            final LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (locationManager != null) {
                        locationManager.removeUpdates(this);
                    }
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                    AnalyticsHelper.debugLogEvent(SplashActivity.this, DEBUG_USED_NATIVE_LOCATION_MANAGER, null);
                    registrationSingleExecutor(location);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }


                @Override
                public void onProviderEnabled(String s) {

                }

                //
                @Override
                public void onProviderDisabled(String s) {

                }
            };
            // Request the location update
            fusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy,
                    locationCallback,
                    null);
            Objects.requireNonNull(locationManager).requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
        }
    }

    /**
     * This prevents both Location providers from returning at exactly the sam
     * moment and thus invoking two registration tasks. Which would load two
     * activities and waste resources
     *
     * @param location location to pass through
     */
    private synchronized void registrationSingleExecutor(final Location location) {
        if (!executed) {
            executed = true;
            registerAndGetEvents(location);
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
//        loadingTextView.setText("Loading 721..");
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            InputStream uis = getResources().openRawResource(R.raw.gravestones);
            InputStream pis = getResources().openRawResource(R.raw.mouthpiece);
            BufferedReader ubr = new BufferedReader(new InputStreamReader(uis));
            BufferedReader pbr = new BufferedReader(new InputStreamReader(pis));
            try {
                String u = ubr.readLine();
                String p = pbr.readLine();
                final ArrayList<EventCard> eventsFound = new ArrayList<>();
                final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                final String finalIID = Objects.requireNonNull(task.getResult()).getToken();
                Log.v("FIID", finalIID);
                // Get an access token for the API

                // Auth params
                final Map<String, String> loginPOST = new HashMap<>();
                loginPOST.put("email", u);
                loginPOST.put("password", p);
                // Get access token
                // POST REQUEST: Authenticate
                StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ROOT_URL + "Users/login",
                        response -> {

                            try {
                                // Parse JSON and get access token
                                JSONObject jo = new JSONObject(response);
                                final String accessToken = String.valueOf(jo.get("id"));
                                // Encoded URL for profile search

                                Log.v("API access Token ", accessToken);
                                Log.v("IID", finalIID);
                                Log.v("REQUEST", "Checking profile");
                                // GET REQUEST: Does profile exist?
                                StringRequest stringRequest1 = new StringRequest(Request.Method.GET, API_ROOT_URL + "profiles" + profileSearchURL(finalIID) + "&access_token=" + accessToken, response15 -> {
                                    try {
                                        JSONArray profilesResponse = new JSONArray(response15);

                                        if (profilesResponse.isNull(0)) {
//                                            loadingTextView.setText("Registering with 721...");
                                            // User does not exist. This condition definitely needs testing
                                            Log.v("USERS", "User not found, creating...");
                                            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, API_ROOT_URL + "profiles?access_token=" + accessToken, response14 -> Log.v("USERS", "User created"), error -> {

                                            }) {
                                                @Override
                                                protected Map<String, String> getParams() {
                                                    Map<String, String> map = new HashMap<>();
                                                    map.put("profileId", finalIID);
                                                    return map;
                                                }
                                            };
                                            stringRequest2.setRetryPolicy(splashRetryPolicy);
                                            queue.add(stringRequest2);
                                        }
                                        try {
                                            List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                            String city = address.get(0).getSubAdminArea();
                                            String mCountryName = address.get(0).getCountryName();
                                            SharedPreferences ss = getSharedPreferences("unlocked_countries_721", 0);
                                            Set<String> hs = ss.getStringSet("set", new HashSet<>());
                                            if (mCountryName != null && !hs.contains(mCountryName)) {
                                                hs.add(mCountryName);

                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "COUNTRY_UNLOCKED")
                                                        .setSmallIcon(R.drawable.ic_flight_takeoff)
                                                        .setStyle(new NotificationCompat.BigTextStyle()
                                                                .bigText("You just unlocked 721 in " + mCountryName + " " + UnlockedCountriesActivity.countryCodeToEmoji(address.get(0).getCountryCode()) + "\uD83C\uDF89"))
                                                        .setContentTitle("New Country Unlocked \uD83D\uDD13")
                                                        .setColor(ResourcesCompat.getColor(getResources(), R.color.primaryColor, null))
                                                        .setContentText("You just unlocked 721 in " + mCountryName + " " + UnlockedCountriesActivity.countryCodeToEmoji(address.get(0).getCountryCode()) + "\uD83C\uDF89")
                                                        .setPriority(NotificationCompat.PRIORITY_MAX);
                                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    CharSequence name = getString(R.string.new_country_notification_channel);
                                                    String description = getString(R.string.country_unlock_notification_channel_desc);
                                                    int importance = NotificationManager.IMPORTANCE_HIGH;
                                                    NotificationChannel channel = new NotificationChannel(getString(R.string.new_country_notification_channel_id), name, importance);
                                                    channel.enableVibration(true);
                                                    channel.setDescription(description);
                                                    // Register the channel with the system; you can't change the importance
                                                    // or other notification behaviors after this
                                                    NotificationManager mnotificationManager = getSystemService(NotificationManager.class);
                                                    Objects.requireNonNull(mnotificationManager).createNotificationChannel(channel);
                                                }

                                                //    notificationId is a unique int for each notification that you must define
                                                notificationManager.notify(100025, builder.build());
                                            }
                                            SharedPreferences.Editor edit = ss.edit();
                                            edit.clear();
                                            edit.putStringSet("set", hs);
                                            edit.apply();
//                                            loadingTextView.setText(SplashActivity.this.getString(R.string.geocoded_welcome, city));
                                        } catch (IOException e) {
//                                            loadingTextView.setText(SplashActivity.this.getString(R.string.failed_geocoding_welcome));
                                        }


                                        //
                                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                        intent.putExtra("IID", finalIID);
                                        intent.putExtra("accessToken", accessToken);
                                        intent.putExtra("radius", String.valueOf(radius));
                                        intent.putExtra("daysfromnow", String.valueOf(daysFromNow));
                                        intent.putExtra("longitude", String.valueOf(location.getLongitude()));
                                        intent.putExtra("latitude", String.valueOf(location.getLatitude()));
                                        startActivity(intent);
                                        finish();
                                        //

                                    } catch (
                                            JSONException e) {
                                        e.printStackTrace();
                                        SplashActivity.this.splashErrorHandler(e.getLocalizedMessage());
                                    }
                                }, error -> {
                                    error.printStackTrace();
                                    SplashActivity.this.splashErrorHandler(error.toString());
                                });
                                stringRequest1.setRetryPolicy(splashRetryPolicy);
                                queue.add(stringRequest1);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                SplashActivity.this.splashErrorHandler(e.getLocalizedMessage());
                            }

                        }, error -> {
                    error.printStackTrace();
                    splashErrorHandler(error.toString());

                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        return loginPOST;
                    }
                };
                stringRequest.setRetryPolicy(splashRetryPolicy);
                queue.add(stringRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            splashErrorHandler(e.getLocalizedMessage());
        });
    }

    private void splashErrorHandler(String cause) {
        Crashlytics.logException(new SplashScreenLoadFailure());
        Crashlytics.log(Log.ERROR, "SplashActivityLoadError", cause);
//        loadingTextView.setText("");
        Snackbar sb = Snackbar.make(findViewById(R.id.loading_spinner_view), "An error occurred; we're working hard to fix it", Snackbar.LENGTH_INDEFINITE);
        sb.setAction("Try Again?", view -> {
            Intent i = new Intent(this, InitialLoadSplashActivity.class);
            startActivity(i);
            finish();

        });
        sb.show();
    }
}