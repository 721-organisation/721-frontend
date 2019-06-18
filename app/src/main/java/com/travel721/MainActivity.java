package com.travel721;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

import static com.travel721.Constants.GLOBAL_ANIMATION_DURATION;
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
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

    @Override
    public void onCardDragging(Direction direction, float ratio) {
        Log.d("DRAG", "DRAGGING");
    }

    @Override
    public void onCardSwiped(Direction direction) {
        Log.v("SWIPE", "SWIPED " + direction.name());
        switch (direction) {
            case Left:
                // Dislikes
                Toast.makeText(this, "Dislikes", Toast.LENGTH_LONG).show();
                break;
            case Right:
                // Likes
                Toast.makeText(this, "Likes", Toast.LENGTH_LONG).show();
                break;
            case Bottom:
                // Inflate more info pulldown
                LayoutInflater layoutInflater =
                        (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                //Inflate the Pulldown Info Drawer
                final View moreInfoCard = layoutInflater.inflate(R.layout.event_more_info_card, null);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

                // Hide buttons which display above all Views
                findViewById(R.id.thumbdownButton).setVisibility(View.GONE);
                findViewById(R.id.thumbupButton).setVisibility(View.GONE);

                moreInfoCard.setLayoutParams(layoutParams);
                moreInfoCard.setTag(rootConstraintLayout.getChildCount() + 1);
                moreInfoCard.setId(rootConstraintLayout.getChildCount() + 1);
                // Slide in from bottom animation
                moreInfoCard.setY(-windowHeight);
                moreInfoCard.animate()
                        .translationY(0)
                        .setDuration(SLIDE_ANIMATION_DURATION).start();
                // Add the card
                rootConstraintLayout.addView(moreInfoCard);


                // Add the swipe away functionality
                moreInfoCard.setOnTouchListener(new SwipeUpToDismissCardTouchController(moreInfoCard, cardStackView, windowHeight) {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        super.onTouch(v, event);
                        if (dismissed) {
                            removeEventInfoCard();
                        }
                        v.performClick();
                        return true;
                    }
                });

                // Initialise maps
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(eventCards.get(cardStackLayoutManager.getTopPosition() - 1).getLocation())     // Sets the center of the map to Mountain View
                                    .zoom(17)                   // Sets the zoom
                                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                    .build();                   // Creates a CameraPosition from the builder
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    });
                }
                TextView desc = moreInfoCard.findViewById(R.id.eventLongDescription);
                desc.setText(eventCards.get(cardStackLayoutManager.getTopPosition() - 1).getDescription());

                break;
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

    private void removeEventInfoCard() {

        final View moreInfoCard = rootConstraintLayout.findViewWithTag(rootConstraintLayout.getChildCount());
        moreInfoCard.animate()
                .translationY(-windowHeight)
                .setDuration(GLOBAL_ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        SupportMapFragment f = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.mapView);
                        if (f != null)
                            getSupportFragmentManager().beginTransaction().remove(f).commit();
                        rootConstraintLayout.removeView(moreInfoCard);
                        findViewById(R.id.thumbdownButton).setVisibility(View.VISIBLE);
                        findViewById(R.id.thumbupButton).setVisibility(View.VISIBLE);
                        findViewById(R.id.thumbupButton).setAlpha(0f);
                        findViewById(R.id.thumbupButton).setAlpha(0f);
                        findViewById(R.id.thumbdownButton)
                                .animate()
                                .alpha(1f)
                                .setDuration(GLOBAL_ANIMATION_DURATION)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        findViewById(R.id.thumbdownButton).setVisibility(View.VISIBLE);
                                    }
                                })
                                .start();
                        findViewById(R.id.thumbupButton)
                                .animate()
                                .alpha(1f)
                                .setDuration(GLOBAL_ANIMATION_DURATION)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        findViewById(R.id.thumbupButton).setVisibility(View.VISIBLE);
                                    }
                                }).start();
                    }

                }).start();
        cardStackView.rewind();
    }

    public void moreInfoButtonClicked(View view) {

    }

    public void settingsButtonClicked(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void previouslySelectedClicked(View view) {
        Intent i = new Intent(this, ListEventsActivity.class);
        i.putParcelableArrayListExtra("events", eventCards);
        startActivity(i);
    }
}
