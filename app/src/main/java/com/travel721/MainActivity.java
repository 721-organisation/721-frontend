package com.travel721;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.SLIDE_ANIMATION_DURATION;

public class MainActivity extends AppCompatActivity implements CardStackListener {
    private CardStackView cardStackView;
    private CardStackLayoutManager cardStackLayoutManager;
    private ViewGroup rootConstraintLayout;
    private int windowHeight;
    ArrayList<EventCard> eventCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        eventCards = getIntent().getParcelableArrayListExtra("events");
        rootConstraintLayout = findViewById(R.id.rootConstraintLayout);

        // Window mathematics
        windowHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        cardStackView = findViewById(R.id.card_stack_view);
        initialise();

    }


    void initialise() {
        // Create card stack manager
        cardStackLayoutManager = new CardStackLayoutManager(this, this);
        cardStackLayoutManager.setStackFrom(StackFrom.Top);
        cardStackLayoutManager.setVisibleCount(5);
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        List<Direction> directions = new ArrayList<>();
        directions.add(Direction.Right);
        directions.add(Direction.Left);
        directions.add(Direction.Bottom);
        cardStackLayoutManager.setDirections(directions);

        // Create card stack adapter
        CardStackAdapter cardStackAdapter = new CardStackAdapter(eventCards);

        cardStackView.setLayoutManager(cardStackLayoutManager);
        cardStackView.setAdapter(cardStackAdapter);
    }

    private boolean snackShown = false;

    @Override
    public void onCardDragging(Direction direction, float ratio) {
        if (!snackShown) {
            snackShown = true;
            Snackbar.make(cardStackView, "Pulldown on an event to show more information", Snackbar.LENGTH_LONG).show();
        }
    }

    private boolean showingInfo = false;


    @Override
    public void onCardSwiped(Direction direction) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest;
        String url = API_ROOT_URL + "eventProfiles?access_token=" + getIntent().getStringExtra("accessToken");
        switch (direction) {
            case Left:
                Log.v("URL", url);
                // Dislikes
                // Request a string response from the provided URL.
                stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("swipe", "false");
                        map.put("eventSourceId", eventCards.get(cardStackLayoutManager.getTopPosition() - 1).getEventSourceID());
                        map.put("profileId",getIntent().getStringExtra("fiid"));
                        return map;
                    }
                };

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                // TODO make a request to the API
                break;
            case Right:
                // Likes
                // Request a string response from the provided URL.
                stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("swipe", "true");
                        map.put("eventSourceId", eventCards.get(cardStackLayoutManager.getTopPosition() - 1).getEventSourceID());
                        map.put("profileId",getIntent().getStringExtra("fiid"));
                        return map;
                    }
                };

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                // TODO make a request to the API
                break;
            case Bottom:
                Intent i = new Intent(this, EventMoreInfoActivity.class);
                i.putExtra("lat", eventCards.get(cardStackLayoutManager.getTopPosition() - 1).getLocationLatitude());
                i.putExtra("lon", eventCards.get(cardStackLayoutManager.getTopPosition() - 1).getLocationLongitude());
                i.putExtra("desc", eventCards.get(cardStackLayoutManager.getTopPosition() - 1).getDescription());
                i.putExtra("URL", eventCards.get(cardStackLayoutManager.getTopPosition() - 1).getEventHyperLink());
                startActivity(i);
                showingInfo = true;
                break;
        }

    }

    @Override
    protected void onPostResume() {
        RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new DecelerateInterpolator())
                .build();
        cardStackLayoutManager.setRewindAnimationSetting(setting);
        cardStackView.rewind();
        showingInfo = false;
        super.onPostResume();
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }

    public void dislikesTopCard(View view) {
        cardStackLayoutManager.setSwipeAnimationSetting(new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(SLIDE_ANIMATION_DURATION)
                .setInterpolator(new AccelerateInterpolator())
                .build());
        cardStackView.swipe();
        onCardSwiped(Direction.Left);

    }

    public void likesTopCard(View view) {
        cardStackLayoutManager.setSwipeAnimationSetting(new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(SLIDE_ANIMATION_DURATION)
                .setInterpolator(new AccelerateInterpolator())
                .build());
        cardStackView.swipe();
        onCardSwiped(Direction.Right);
    }


    public void settingsButtonClicked(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void previouslySelectedClicked(View view) {
        Intent i = new Intent(this, ListEventsActivity.class);
        i.putExtra("access_token", getIntent().getStringExtra("accessToken"));
        startActivity(i);
    }
}
