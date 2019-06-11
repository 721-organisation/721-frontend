package com.travel721;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static com.travel721.Constants.*;

public class MainActivity extends AppCompatActivity {
    // Variables necessary for moving cards
    private int windowWidth;
    private int windowHeight;
    private int screenCenter;
    private int actionIndicator = 0;
    private FrameLayout parentRelativeLayout;
    private ArrayList<EventCard> eventArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        // Sets root layout
        parentRelativeLayout = findViewById(R.id.main_layoutview);
        // Window mathematics
        windowWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        windowHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenCenter = windowWidth / 2;

        // Load event data
        eventArrayList = new ArrayList<>();
        getArrayData();
        // Get layout inflater
        LayoutInflater layoutInflater =
                (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Add the loading card to the stack
        View loadingCard = layoutInflater.inflate(R.layout.loading_card, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        loadingCard.setLayoutParams(layoutParams);
        // Give unique tag and add to the stack
        loadingCard.setTag(99);
        parentRelativeLayout.addView(loadingCard);
        View nextCard; // The next Event Card to add
        RelativeLayout relativeLayoutContainer; // The next Event Card's layout

        // Iterate through event arraylist
        for (int i = 0; i < eventArrayList.size(); i++) {
            // Inflate next card
            nextCard = layoutInflater.inflate(R.layout.event_card_layout, null);
            // Set card metadata
            nextCard.setLayoutParams(layoutParams);
            nextCard.setTag(i);
            nextCard.setId(i);

            // Unpack event data and update TextViews on the Card
            //TODO update all TextViews

            TextView eventTitle = nextCard.findViewById(R.id.eventTitle);
            eventTitle.setText(eventArrayList.get(i).getEventName());
            TextView eventLine1 = nextCard.findViewById(R.id.eventLine1);
            eventLine1.setText(eventArrayList.get(i).getEventImgURL());

            // Finalise views that need to be updated in anonymous classes
            final ImageView iv = nextCard.findViewById(R.id.eventImage);
            final View finalContainerView = nextCard;
            // Downloads images and adds cards once fully ready to be shown
            new DownloadFileFromURL(new IOnFileDownloadedListener() {
                @Override
                public void onFileDownloaded(Bitmap bmp) {
                    // Update image
                    iv.setImageBitmap(bmp);
                    // Slide in the next Event Card from bottom
                    ObjectAnimator animation = ObjectAnimator.ofFloat(finalContainerView, "translationY", windowHeight, 0);
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    animation.setDuration(GLOBAL_ANIMATION_DURATION);
                    animation.start();
                    parentRelativeLayout.addView(finalContainerView);
                    try {
                        // Remove the loading card
                        parentRelativeLayout.removeView(parentRelativeLayout.findViewWithTag(99));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute(eventArrayList.get(i).getEventImgURL());
            // Set the card's listener to the movingcardlistener
            nextCard.setOnTouchListener(new MovingCardListener(finalContainerView));

        }
    }

    //TODO sample data at the moment
    private void getArrayData() {
        EventCard popTarts = new EventCard("https://foundrysu.com/asset/Event/6005/logo-transparent.png", "Pop Tarts", 53.380626, -1.487348, "01/01/1970");
        EventCard soulJam = new EventCard("https://630427f7704d93fc82a1-a98418e8880457b4440872c557a55550.ssl.cf3.rackcdn.com/brands/souljam_3.jpg", "SoulJam", 39.018428, 125.753089, "01/01/1970");
        EventCard applebum = new EventCard("https://mixmag.net/assets/uploads/images/_facebook/Applebum-DJs.jpg", "AppleBum", 53.376533, -1.470913, "01/01/1970");
        eventArrayList.add(popTarts);
        eventArrayList.add(soulJam);
        eventArrayList.add(applebum);
    }

    // Dismisses the top card to the right
    public void likesTopCard(View view) {
        final View view1 = view;
        if (parentRelativeLayout.getChildCount() > 0 && parentRelativeLayout.getChildAt(parentRelativeLayout.getChildCount() - 1) instanceof CardView) {
            view.setClickable(false);

            // Use this to get info!
            View card = parentRelativeLayout.getChildAt(parentRelativeLayout.getChildCount() - 1);
            ObjectAnimator animation = ObjectAnimator.ofFloat(card, "translationX", 0, windowWidth);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setDuration(GLOBAL_ANIMATION_DURATION);
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    parentRelativeLayout.removeViewAt(parentRelativeLayout.getChildCount() - 1);
                    view1.setClickable(true);
                }
            });
            animation.start();
        }
    }

    // Dismisses the top card to the left
    public void dislikesTopCard(View view) {
        final View view1 = view;
        if (parentRelativeLayout.getChildCount() > 0 && parentRelativeLayout.getChildAt(parentRelativeLayout.getChildCount() - 1) instanceof CardView) {
            view.setClickable(false);
            // Use this to get info!
            View card = parentRelativeLayout.getChildAt(parentRelativeLayout.getChildCount() - 1);
            ObjectAnimator animation = ObjectAnimator.ofFloat(card, "translationX", 0, -windowWidth);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setDuration(GLOBAL_ANIMATION_DURATION);
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    parentRelativeLayout.removeViewAt(parentRelativeLayout.getChildCount() - 1);
                    view1.setClickable(true);
                }
            });
            animation.start();

        }
    }

    class MovingCardListener implements View.OnTouchListener {
        private int x_cord;
        private int y_cord;
        private int x;
        private int y;
        private View finalContainerView;

        MovingCardListener(View finalContainerView) {
            this.finalContainerView = finalContainerView;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            x_cord = (int) event.getRawX();
            y_cord = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    x = (int) event.getX();
                    y = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // smoother animation.

                    x_cord = (int) event.getRawX();
                    y_cord = (int) event.getRawY();
                    finalContainerView.setX(x_cord - x);

                    if (y_cord > y) {
                        finalContainerView.setY(y_cord - y);
                    }

                    if (x_cord >= screenCenter) {
                        if (x_cord > (screenCenter + (screenCenter / 2))) {
                            if (x_cord > (windowWidth - (screenCenter / 4))) {
                                actionIndicator = 2;
                            } else {
                                actionIndicator = 0;
                            }
                        } else {
                            actionIndicator = 0;
                        }
                    } else {
                        if (x_cord < (screenCenter / 2)) {
                            if (x_cord < screenCenter / 4) {
                                actionIndicator = 1;
                            } else {
                                actionIndicator = 0;
                            }
                        } else {
                            actionIndicator = 0;
                        }
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    x_cord = (int) event.getRawX();
                    y_cord = (int) event.getRawY();
                    if (actionIndicator == 0) {
//                        ObjectAnimator animationX = ObjectAnimator.ofFloat(finalContainerView, "translationX", 0);
//                        ObjectAnimator animationY = ObjectAnimator.ofFloat(finalContainerView, "translationY", 0);
//                        animationX.setInterpolator(new AccelerateDecelerateInterpolator());
//                        animationY.setInterpolator(new AccelerateDecelerateInterpolator());
//                        animationX.setDuration(GLOBAL_ANIMATION_DURATION);
//                        animationY.setDuration(GLOBAL_ANIMATION_DURATION);
//                        animationX.start();
//                        animationY.start();

                        finalContainerView.animate().translationY(0).translationX(0).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (1.5 * y < y_cord) {
                                    LayoutInflater layoutInflater =
                                            (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                    // Initialise with the Loading Card
                                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                    findViewById(R.id.thumbdownButton).animate().alpha(0f).setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            findViewById(R.id.thumbdownButton).setVisibility(View.GONE);
                                        }
                                    });
                                    findViewById(R.id.thumbupButton).animate().alpha(0f).setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            findViewById(R.id.thumbupButton).setVisibility(View.GONE);
                                        }
                                    });
                                    // Inflate the Pulldown Info Drawer
                                    final View moreInfoCard = layoutInflater.inflate(R.layout.event_more_info_card, null);
                                    // Add the swipe away functionality
                                    moreInfoCard.setOnTouchListener(new SwipeUpToDismissCardTouchController(moreInfoCard, parentRelativeLayout, windowHeight, getSupportFragmentManager()){
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            super.onTouch(v, event);
                                            if (dismissed){
                                                findViewById(R.id.thumbdownButton).setVisibility(View.VISIBLE);
                                                findViewById(R.id.thumbupButton).setVisibility(View.VISIBLE);
                                                findViewById(R.id.thumbdownButton).animate().alpha(1f).setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        super.onAnimationEnd(animation);
                                                    }
                                                });
                                                findViewById(R.id.thumbupButton).animate().alpha(1f).setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        super.onAnimationEnd(animation);
                                                    }
                                                });
                                            }
                                            v.performClick();
                                            return true;
                                        }
                                    });
                                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
                                    if (mapFragment != null) {
                                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(GoogleMap googleMap) {
                                                // Zoom in, animating the camera
                                                googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                                                // Zoom out to zoom level 10, animating with the global animation duration.
                                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), GLOBAL_ANIMATION_DURATION, null);
                                                // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                                        .target(eventArrayList.get((int) parentRelativeLayout.getChildAt(parentRelativeLayout.getChildCount() - 2).getTag()).getEventLocation())      // Sets the center of the map to Mountain View
                                                        .zoom(17)                   // Sets the zoom
                                                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                                        .build();                   // Creates a CameraPosition from the builder
                                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            }
                                        });
                                    }
                                    moreInfoCard.setLayoutParams(layoutParams);
                                    moreInfoCard.setTag(parentRelativeLayout.getChildCount() + 1);
                                    moreInfoCard.setId(parentRelativeLayout.getChildCount() + 1);
                                    // Slide in from bottom animation
                                    moreInfoCard.setY(-windowHeight);
                                    moreInfoCard.animate()
                                            .translationY(0)
                                            .setInterpolator(new AccelerateDecelerateInterpolator())
                                            .setDuration(SLIDE_ANIMATION_DURATION).start();
                                    parentRelativeLayout.addView(moreInfoCard);
                                }
                            }
                        });
                    } else if (actionIndicator == 1) {
                        finalContainerView.animate().alpha(0f).setDuration(GLOBAL_ANIMATION_DURATION).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                parentRelativeLayout.removeView(finalContainerView);
                            }
                        });
                    } else if (actionIndicator == 2) {
                        finalContainerView.animate().alpha(0f).setDuration(GLOBAL_ANIMATION_DURATION).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                parentRelativeLayout.removeView(finalContainerView);
                            }
                        });
                    }

                    break;
                default:
                    break;
            }
            v.performClick();
            return true;
        }
    }
}