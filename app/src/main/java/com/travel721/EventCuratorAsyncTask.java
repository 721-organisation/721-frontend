package com.travel721;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.eventProfileAllSearchFilter;

public class EventCuratorAsyncTask extends AsyncTask<Void, String, List<EventCard>> {
    private static final int HTTP_STATUS_OK = 200;
    private String accessToken;
    private String IID;
    private String longitude;
    private String latitude;
    private String radius;
    private String daysFromNow;
    private boolean discoverMode;
    private String searchLocation;

    /**
     * This constructor is for the 'Near Me' mode only
     *
     * @param accessToken the API access token
     * @param IID         the user's IID
     * @param longitude   user's longitude
     * @param latitude    user's latitude
     * @param radius      user's search radius
     * @param daysFromNow days from now to search
     */
    public EventCuratorAsyncTask(String accessToken, String IID, String longitude, String latitude, String radius, String daysFromNow) {
        this.accessToken = accessToken;
        this.IID = IID;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.daysFromNow = daysFromNow;
        discoverMode = false;
    }

    /**
     * This constructor is for 'Discover' mode only
     *
     * @param accessToken    the API access token
     * @param IID            the user's IID
     * @param radius         user's search radius
     * @param daysFromNow    days from now to search
     * @param searchLocation the location to discover
     */
    public EventCuratorAsyncTask(String accessToken, String IID, String radius, String daysFromNow, String searchLocation) {
        this.accessToken = accessToken;
        this.IID = IID;
        this.longitude = String.valueOf(0);
        this.latitude = String.valueOf(0);
        this.radius = radius;
        this.daysFromNow = daysFromNow;
        this.searchLocation = searchLocation;
        discoverMode = true;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.v("ECAST", values[0]);
    }

    @Override
    protected List<EventCard> doInBackground(Void... voids) {
        ArrayList<EventCard> eventsFound = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        FormBody formBody;
        if (discoverMode) {
            formBody = new FormBody.Builder()
                    .add("latitude", latitude)
                    .add("longitude", longitude)
                    .add("location", searchLocation)
                    .add("explore", "false")
                    .add("radius", String.valueOf(radius))
                    .add("daysFromNow", String.valueOf(daysFromNow))
                    .build();
        } else {
            formBody = new FormBody.Builder()
                    .add("latitude", latitude)
                    .add("longitude", longitude)
                    .add("explore", "false")
                    .add("radius", String.valueOf(radius))
                    .add("daysFromNow", String.valueOf(daysFromNow))
                    .build();
        }
        Request updateEventsOnServer = new Request.Builder()
                .url(API_ROOT_URL + "events/updateNew?access_token=" + accessToken)
                .put(formBody)
                .build();
        publishProgress("Updating events on server");
        try (Response updateEventsOnServerResponse = client.newCall(updateEventsOnServer).execute()) {
            // We don't need to look at the response, just check it returned 200
            publishProgress("UEOS", "" + Objects.requireNonNull(updateEventsOnServerResponse.body()).string());


            Request getEventsFromServerRequest = new Request.Builder()
                    .url(discoverMode ?
                            API_ROOT_URL + "events/getWithinDistance?latitude=0&longitude=0&radius=" + radius + "&daysFromNow=" + daysFromNow + "&access_token=" + accessToken + "&explore=true&location=" + searchLocation :

                            API_ROOT_URL + "events/getWithinDistance?latitude=" + latitude + "&longitude=" + longitude + "&radius=" + radius + "&daysFromNow=" + daysFromNow + "&access_token=" + accessToken + "&explore=false")
                    .build();


            try (Response getEventsFromServerResponse = client.newCall(getEventsFromServerRequest).execute()) {
                publishProgress(Objects.requireNonNull(getEventsFromServerResponse.body()).string());
                // Extract events from JSON response
                JSONObject jo1 = new JSONObject(Objects.requireNonNull(getEventsFromServerResponse.body()).string());
                JSONArray events = jo1.getJSONArray("getWithinDistance");

                // Add events to temporary list
                for (int i = 0; i < events.length(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    eventsFound.add(EventCard.unpackFromJson(event));

                }
                // Filter events already swiped through
                Request getSwipedEventIDsRequest = new Request.Builder()
                        .url(API_ROOT_URL + "eventProfiles?access_token=" + accessToken + "&filter=" + eventProfileAllSearchFilter(IID))
                        .build();

                try (Response getSwipedEventIDsResponse = client.newCall(getSwipedEventIDsRequest).execute()) {
                    publishProgress(Objects.requireNonNull(getSwipedEventIDsResponse.body()).string());
                    final JSONArray eventProfileArray = new JSONArray(Objects.requireNonNull(getSwipedEventIDsResponse.body()).string());
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
                    // TODO cache events
                    Log.v("ECAST", "Returned" + filteredCards.size() + " events");
                    return filteredCards;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.v("ECAST UEOS", "ERROR");
        }
        return null;
    }
}
