package com.travel721;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.travel721.Constants.API_ROOT_URL;

public class EventCuratorAsyncTask extends AsyncTask<Void, Void, List<EventCard>> {
    final String LOADING_NEAR_ME_REQUEST_TAG = "LOADING_NEAR_ME_REQUEST_TAG";

    public EventCuratorAsyncTask(String accessToken, String IID, String longitude, String latitude, String radius, String daysFromNow) {
        this.accessToken = accessToken;
        this.IID = IID;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.daysFromNow = daysFromNow;
    }

    private String accessToken;
    private String IID;
    private String longitude;
    private String latitude;
    private String radius;
    private String daysFromNow;

    @Override
    protected List<EventCard> doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        try {
            FormBody formBody = new FormBody.Builder()
                    .add("latitude", latitude)
                    .add("longitude", longitude)
                    .add("explore", "false")
                    .add("radius", String.valueOf(radius))
                    .add("daysFromNow", String.valueOf(daysFromNow))
                    .build();
            Request request = new Request.Builder()
                    .url(API_ROOT_URL + "profiles?access_token=" + accessToken)
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                response.body().string();

            }


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        return null;
    }
}
