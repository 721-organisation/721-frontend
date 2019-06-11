package com.travel721;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;

import static com.travel721.Constants.CARD_SWIPING_STICKINESS;
import static com.travel721.Constants.GLOBAL_ANIMATION_DURATION;
import static com.travel721.Constants.SLIDE_ANIMATION_DURATION;

public class MainActivity extends AppCompatActivity {
    // Variables necessary for moving cards
    private int windowWidth;
    private int windowHeight;
    private int actionIndicator = 0;
    private FrameLayout parentRelativeLayout;
    private ArrayList<EventCard> eventArrayList;

    // Firebase Instance ID
    String iid = "";

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

        // Get Instance ID
        FirebaseApp.initializeApp(getApplicationContext());
//        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//            @Override
//            public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                iid = task.getResult().getToken();
//            }
//        });

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
            eventTitle.setText(eventArrayList.get(i).getName());
            TextView eventLine1 = nextCard.findViewById(R.id.eventLine1);
            eventLine1.setText(eventArrayList.get(i).getFormattedDate().toString());
            TextView eventLine2 = nextCard.findViewById(R.id.eventLine2);
            eventLine2.setText(eventArrayList.get(i).getTime());
            TextView eventLine3 = nextCard.findViewById(R.id.eventLine3);
            eventLine3.setText(eventArrayList.get(i).getVenueName());
            TextView eventLine4 = nextCard.findViewById(R.id.eventLine4);
            eventLine4.setText(eventArrayList.get(i).getPrice());


            // Finalise views that need to be updated in anonymous classes
            final ImageView iv = nextCard.findViewById(R.id.eventImage);
            final View finalContainerView = nextCard;
            // Downloads images and adds cards once fully ready to be shown
            new DownloadFileFromURL(new IOnFileDownloadedListener() {
                @Override
                public void onFileDownloaded(final Bitmap bmp) {
                    // Slide in the next Event Card from bottom
                    finalContainerView.setY(windowHeight);
                    finalContainerView.animate()
                            .translationY(0)
                            .setDuration(GLOBAL_ANIMATION_DURATION)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    // Update image
                                    iv.setImageBitmap(bmp);
                                }
                            });

                    // Remove the loading card if it's still in the layout
                    if (parentRelativeLayout.findViewWithTag(99) != null)
                        parentRelativeLayout.removeView(parentRelativeLayout.findViewWithTag(99));

                    parentRelativeLayout.addView(finalContainerView);
                }
            }).execute(eventArrayList.get(i).getImgURL());
            // Set the card's listener to the movingcardlistener
            nextCard.setOnTouchListener(new MovingCardListener(finalContainerView));

        }
    }

    //TODO sample data at the moment
    private void getArrayData() {
        EventCard test = new EventCard("Foals Afterparty",
                "13553006",
                "42nd Street Nightclub",
                "53.4785216",
                "-2.2477315",
                "https://d31fr2pwly4c4s.cloudfront.net/3/9/3/1179935_1_foals-afterparty.jpg",
                "https://d31fr2pwly4c4s.cloudfront.net/3/9/3/1179935_1_foals-afterparty.jpg",
                "2019-06-11",
                "23:00",
                "18",
                "£3");
        eventArrayList.add(test);
        eventArrayList.add(test);
        eventArrayList.add(test);
        eventArrayList.add(test);
        eventArrayList.add(test);
        eventArrayList.add(test);
        eventArrayList.add(test);
        eventArrayList.add(test);
        eventArrayList.add(test);
        eventArrayList.add(test);
        eventArrayList.add(test);
    }

    // Dismisses the top card to the right
    public void likesTopCard(View view) {
        if (parentRelativeLayout.getChildCount() > 0 && parentRelativeLayout.getChildAt(parentRelativeLayout.getChildCount() - 1) instanceof CardView) {
            view.setClickable(false);

            // Use this to get info!
            View card = parentRelativeLayout.getChildAt(parentRelativeLayout.getChildCount() - 1);
            autoSwipeTopCard(true, view);
        }
    }

    // Dismisses the top card to the left
    public void dislikesTopCard(View view) {
        if (parentRelativeLayout.getChildCount() > 0 && parentRelativeLayout.getChildAt(parentRelativeLayout.getChildCount() - 1) instanceof CardView) {
            view.setClickable(false);
            // Use this to get info!
            View card = parentRelativeLayout.getChildAt(parentRelativeLayout.getChildCount() - 1);
            autoSwipeTopCard(false, view);
        }
    }

    void autoSwipeTopCard(boolean likes, final View buttonPushed) {
        int destination = likes ? windowWidth : -windowHeight;
        final int index = parentRelativeLayout.getChildCount() - 1;
        parentRelativeLayout.getChildAt(index).animate()
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(GLOBAL_ANIMATION_DURATION)
                .translationX(destination)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        parentRelativeLayout.removeViewAt(index);
                        buttonPushed.setClickable(true);
                    }
                }).start();
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
                    // Move the card
                    x_cord = (int) event.getRawX();
                    y_cord = (int) event.getRawY();

                    finalContainerView.setX(x_cord - x); // Move in X Direction
                    // Do not allow moving further up the screen
                    if (y_cord > y) finalContainerView.setY(y_cord - y);


                    if (x_cord > x) {
                        if (x_cord > (1 + CARD_SWIPING_STICKINESS) * x) {
                            actionIndicator = 2;
                        } else {
                            actionIndicator = 0;
                        }
                    } else {
                        if (x_cord < (1 - CARD_SWIPING_STICKINESS) * x) {
                            actionIndicator = 1;
                        } else {
                            actionIndicator = 0;
                        }
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    x_cord = (int) event.getRawX();
                    y_cord = (int) event.getRawY();
                    if (actionIndicator == 0) {

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
                                    moreInfoCard.setOnTouchListener(new SwipeUpToDismissCardTouchController(moreInfoCard, parentRelativeLayout, windowHeight) {
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
                                                        .target(eventArrayList.get((int) parentRelativeLayout.getChildAt(parentRelativeLayout.getChildCount() - 2).getTag()).getLocation())     // Sets the center of the map to Mountain View
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

    @Override
    public void onBackPressed() {
        if (parentRelativeLayout.findViewWithTag(parentRelativeLayout.getChildCount()) instanceof ConstraintLayout) {
            removeEventInfoCard();
        } else {
            super.onBackPressed();
        }
    }

    private void removeEventInfoCard() {
        final View moreInfoCard = parentRelativeLayout.findViewWithTag(parentRelativeLayout.getChildCount());
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
                        parentRelativeLayout.removeView(moreInfoCard);
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
    }
}