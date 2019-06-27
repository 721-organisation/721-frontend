package com.travel721;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.markormesher.android_fab.FloatingActionButton;
import uk.co.markormesher.android_fab.SpeedDialMenuAdapter;
import uk.co.markormesher.android_fab.SpeedDialMenuCloseListener;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;
import uk.co.markormesher.android_fab.SpeedDialMenuOpenListener;

import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.SLIDE_ANIMATION_DURATION;

/**
 * This class is the main layout which contains logic
 * for the CardStackView and the on screen buttons.
 *
 * @author Bhav
 */
public class MainActivity extends AppCompatActivity implements CardStackListener {

    // Keep a track of the CardView and it's adapter
    private CardStackView cardStackView;
    private CardStackLayoutManager cardStackLayoutManager;
    ArrayList<EventCard> eventCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.overflowFab);
        final com.google.android.material.floatingactionbutton.FloatingActionButton likeButton = findViewById(R.id.thumbupButton);
        final com.google.android.material.floatingactionbutton.FloatingActionButton dislikeButton = findViewById(R.id.thumbdownButton);
        fab.setSpeedDialMenuAdapter(new MainActivitySpeedDialAdapter());
        fab.setOnSpeedDialMenuOpenListener(new SpeedDialMenuOpenListener() {
            @Override
            public void onOpen(@NotNull FloatingActionButton floatingActionButton) {
                floatingActionButton.setContentCoverColour(0x99ff9900);
                floatingActionButton.openSpeedDialMenu();
                floatingActionButton.setContentCoverEnabled(true);
                likeButton.setVisibility(View.INVISIBLE);
                dislikeButton.setVisibility(View.INVISIBLE);
                floatingActionButton.getContentCoverView().setVisibility(View.VISIBLE);
            }
        });
        fab.setOnSpeedDialMenuCloseListener(new SpeedDialMenuCloseListener() {
            @Override
            public void onClose(@NotNull FloatingActionButton floatingActionButton) {
                floatingActionButton.setContentCoverColour(0x99ff9900);

                floatingActionButton.closeSpeedDialMenu();
                floatingActionButton.setContentCoverEnabled(false);
                likeButton.setVisibility(View.VISIBLE);
                dislikeButton.setVisibility(View.VISIBLE);
                floatingActionButton.getContentCoverView().setVisibility(View.GONE);
            }
        });
        // Gets the event info obtained from the splash activity
        eventCards = getIntent().getParcelableArrayListExtra("events");

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
                                Log.v("SWIPE", "Successfully registered dislike");
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
                        map.put("profileId", getIntent().getStringExtra("fiid"));
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
                                Log.v("SWIPE", "Successfully registered like");
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
                        map.put("profileId", getIntent().getStringExtra("fiid"));
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
                overridePendingTransition(R.anim.slide_in_from_top, 0);
                showingInfo = true;
                break;
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (showingInfo) {
            RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                    .setDirection(Direction.Bottom)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(new DecelerateInterpolator())
                    .build();
            cardStackLayoutManager.setRewindAnimationSetting(setting);
            cardStackView.rewind();
            showingInfo = false;

        }
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
        finish();
    }

    public void overflowFabClicked(View view) {
        if (view instanceof FloatingActionButton) {
            if (!((FloatingActionButton) view).isSpeedDialMenuOpen()) {
                ((FloatingActionButton) view).closeSpeedDialMenu();

            } else {

                ((FloatingActionButton) view).openSpeedDialMenu();
            }
        }
    }


    //    No longer used
//    public void previouslySelectedClicked(View view) {
//        Intent i = new Intent(this, ListEventsActivity.class);
//        i.putExtra("access_token", getIntent().getStringExtra("accessToken"));
//        startActivity(i);
//    }
    class MainActivitySpeedDialAdapter extends SpeedDialMenuAdapter {
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean onMenuItemClick(int position) {
            switch (position) {
                case 0:
                    Intent i = new Intent(getBaseContext(), SettingsActivity.class);
                    startActivity(i);
                    finish();
                    break;
                case 1:
                    Intent j = new Intent(getBaseContext(), ListEventsActivity.class);
                    j.putExtra("access_token", getIntent().getStringExtra("accessToken"));
                    startActivity(j);
                    break;
            }
            return super.onMenuItemClick(position);
        }

        @Override
        public int getBackgroundColour(int position) {
            return Color.rgb(255, 255, 255);
        }

        @Override
        public void onPrepareItemLabel(@NotNull Context context, int position, @NotNull TextView label) {
            super.onPrepareItemLabel(context, position, label);
        }

        @Override
        public float fabRotationDegrees() {
            return 90f;
        }

        @NotNull
        @Override
        public SpeedDialMenuItem getMenuItem(@NotNull Context context, int i) {
            switch (i) {
                // Settings
                case 0:
                    return new SpeedDialMenuItem(context, ContextCompat.getDrawable(context, R.drawable.ic_settings), "Settings");
                case 1:
                    return new SpeedDialMenuItem(context, ContextCompat.getDrawable(context, R.drawable.ic_subject), "Like History");
                default:
                    return null;
            }

        }
    }

}
