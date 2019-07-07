package com.travel721;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidadvance.topsnackbar.TSnackbar;
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
import java.util.Random;

import uk.co.markormesher.android_fab.FloatingActionButton;
import uk.co.markormesher.android_fab.SpeedDialMenuAdapter;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;

import static com.travel721.Constants.API_ROOT_URL;
import static com.travel721.Constants.SLIDE_ANIMATION_DURATION;

/**
 * This class is the main layout which contains logic
 * for the CardStackView and the on screen buttons.
 *
 * @author Bhav
 */
public class MainActivity extends AppCompatActivity implements CardStackListener {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    // Keep a track of the CardView and it's adapter
    private CardStackView cardStackView;
    private CardStackLayoutManager cardStackLayoutManager;
    ArrayList<EventCard> eventCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AnalyticsHelper.logEvent(this, AnalyticsHelper.TEST_RELEASE_ANALYTICS_EVENT, null);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.overflowFab);
        fab.findViewById(R.id.fab_icon_wrapper).setBackground(ContextCompat.getDrawable(this, R.drawable.ic_more_vert));
        final com.google.android.material.floatingactionbutton.FloatingActionButton likeButton = findViewById(R.id.thumbupButton);
        final com.google.android.material.floatingactionbutton.FloatingActionButton dislikeButton = findViewById(R.id.thumbdownButton);
        fab.setSpeedDialMenuAdapter(new MainActivitySpeedDialAdapter());
        fab.setOnSpeedDialMenuOpenListener(floatingActionButton -> {
            floatingActionButton.setContentCoverColour(0xeefe6060);
            floatingActionButton.openSpeedDialMenu();
            floatingActionButton.setContentCoverEnabled(true);
            likeButton.setVisibility(View.INVISIBLE);
            dislikeButton.setVisibility(View.INVISIBLE);
            floatingActionButton.getContentCoverView().setVisibility(View.VISIBLE);
        });
        fab.setOnSpeedDialMenuCloseListener(floatingActionButton -> {
            floatingActionButton.setContentCoverColour(0x99fe6060);

            floatingActionButton.closeSpeedDialMenu();
            floatingActionButton.setContentCoverEnabled(false);
            likeButton.setVisibility(View.VISIBLE);
            dislikeButton.setVisibility(View.VISIBLE);
            floatingActionButton.getContentCoverView().setVisibility(View.GONE);
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
        int index = buttonPushed ? cardStackLayoutManager.getTopPosition() : cardStackLayoutManager.getTopPosition() - 1;
        if (buttonPushed) buttonPushed = false;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest;
        String url = API_ROOT_URL + "eventProfiles?access_token=" + getIntent().getStringExtra("accessToken");
        // Each case makes a call to the Analytics API
        //vectordrawable
        TSnackbar snackbar = TSnackbar
                .make(findViewById(R.id.rootConstraintLayout), "", TSnackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.WHITE);
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(0, 50, 0, 50);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        switch (direction) {
            case Left:
                snackbar.dismiss();
                String[] negative_terms = getResources().getStringArray(R.array.negative_terms);
                textView.setText(getRandom(negative_terms));
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                snackbar.show();
                AnalyticsHelper.logEvent(this, AnalyticsHelper.USER_SWIPED_LEFT, null);
                // Dislikes
                // Request a string response from the provided URL.
                stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            // Display the first 500 characters of the response string.
                            Log.v("SWIPE", "Successfully registered dislike");
                        }, error -> Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("swipe", "false");
                        map.put("eventSourceId", eventCards.get(index).getEventSourceID());
                        map.put("profileId", getIntent().getStringExtra("fiid"));
                        return map;
                    }
                };

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                // TODO make a request to the API
                break;
            case Right:
                snackbar.dismiss();
                String[] positive_terms = getResources().getStringArray(R.array.positive_terms);
                textView.setText(getRandom(positive_terms));
                textView.setTextColor(Color.rgb(0, 230, 118));
                snackbar.show();
                AnalyticsHelper.logEvent(this, AnalyticsHelper.USER_SWIPED_RIGHT, null);

                // Likes
                // Request a string response from the provided URL.
                stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            // Display the first 500 characters of the response string.
                            Log.v("SWIPE", "Successfully registered like");
                        }, error -> Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("swipe", "true");
                        map.put("eventSourceId", eventCards.get(index).getEventSourceID());
                        map.put("profileId", getIntent().getStringExtra("fiid"));
                        return map;
                    }
                };

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                // TODO make a request to the API
                break;
            case Bottom:
                AnalyticsHelper.logEvent(this, AnalyticsHelper.USER_SWIPED_DOWN, null);
                Intent i = new Intent(this, EventMoreInfoActivity.class);
                i.putExtra("eventCard", (Parcelable) eventCards.get(cardStackLayoutManager.getTopPosition() - 1));
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

    boolean buttonPushed = false;

    public void dislikesTopCard(View view) {
        buttonPushed = true;
        cardStackLayoutManager.setSwipeAnimationSetting(new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(SLIDE_ANIMATION_DURATION)
                .setInterpolator(new AccelerateInterpolator())
                .build());
        cardStackView.swipe();

    }

    public void likesTopCard(View view) {
        buttonPushed = true;
        cardStackLayoutManager.setSwipeAnimationSetting(new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(SLIDE_ANIMATION_DURATION)
                .setInterpolator(new AccelerateInterpolator())
                .build());
        cardStackView.swipe();
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
                    AnalyticsHelper.logEvent(MainActivity.this, AnalyticsHelper.SETTINGS_OPENED, null);
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

    public static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }
}
