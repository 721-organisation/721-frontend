package com.travel721;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.List;

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
            snackShown=true;
            Snackbar.make(getCurrentFocus(),"Pulldown on an event to show more information",Snackbar.LENGTH_LONG).show();
        }
    }
    private boolean showingInfo = false;
    @Override
    public void onCardSwiped(Direction direction) {
        switch (direction) {
            case Left:
                // Dislikes
                // TODO make a request to the API
                break;
            case Right:
                // Likes
                // TODO make a request to the API
                break;
            case Bottom:
                Intent i = new Intent(this, EventMoreInfoActivity.class);
                i.putExtra("lat", eventCards.get(cardStackLayoutManager.getTopPosition()-1).getLocationLatitude());
                i.putExtra("lon", eventCards.get(cardStackLayoutManager.getTopPosition()-1).getLocationLongitude());
                i.putExtra("desc", eventCards.get(cardStackLayoutManager.getTopPosition()-1).getDescription());
                i.putExtra("URL",eventCards.get(cardStackLayoutManager.getTopPosition()-1).getEventHyperLink());
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
        showingInfo=false;
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
        i.putParcelableArrayListExtra("events", eventCards);
        startActivity(i);
    }
}
