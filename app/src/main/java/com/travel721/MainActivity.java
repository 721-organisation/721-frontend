package com.travel721;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.transition.Slide;
import androidx.transition.Transition;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int windowwidth;
    int screenCenter;
    int x_cord, y_cord, x, y;
    int Likes = 0;
    public RelativeLayout parentView;
    private Context context;
    private ArrayList<EventCard> eventArrayList;
    private View topCard;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        parentView = (RelativeLayout) findViewById(R.id.main_layoutview);
        windowwidth = getWindowManager().getDefaultDisplay().getWidth();
        screenCenter = windowwidth / 2;
        eventArrayList = new ArrayList<>();
        getArrayData();

        LayoutInflater inflate =
                (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View containerView = inflate.inflate(R.layout.loading_card, null);
        RelativeLayout relativeLayoutContainer = containerView.findViewById(R.id.relative_container);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        containerView.setLayoutParams(layoutParams);
        containerView.setTag(99);
        parentView.addView(containerView);

        for (int i = 0; i < eventArrayList.size(); i++) {
            final Transition t = new Slide(Gravity.BOTTOM);
            inflate =
                    (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            containerView = inflate.inflate(R.layout.layout, null);
            relativeLayoutContainer = containerView.findViewById(R.id.relative_container);
            layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            containerView.setLayoutParams(layoutParams);
            containerView.setTag(i);
            containerView.setId(i);
            topCard = parentView.findViewWithTag(parentView.getChildCount());

            // Unpack event data
            TextView eventTitle = containerView.findViewById(R.id.eventTitle);
            eventTitle.setText(eventArrayList.get(i).getEventName());

            TextView eventLine1 = (TextView) containerView.findViewById(R.id.eventLine1);
            eventLine1.setText(eventArrayList.get(i).getEventImgURL());

            final View finalContainerView = containerView;
            new DownloadImageTask((ImageView) finalContainerView.findViewById(R.id.eventImage)) {
                @Override
                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);

                    ObjectAnimator animation = ObjectAnimator.ofFloat(finalContainerView, "translationY", Resources.getSystem().getDisplayMetrics().heightPixels, 0);
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    animation.setDuration(200);
                    animation.start();
                    parentView.addView(finalContainerView);
                    try {
                        parentView.removeView(parentView.findViewWithTag(99));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute(eventArrayList.get(i).getEventImgURL());

            relativeLayoutContainer.setOnTouchListener(new MovingCardListener(finalContainerView));

        }
    }

    private void getArrayData() {
        EventCard popTarts = new EventCard("https://foundrysu.com/asset/Event/6005/logo-transparent.png", "Pop Tarts", "Foundry, Studio & Fusion", "01/01/1970");
        EventCard soulJam = new EventCard("https://630427f7704d93fc82a1-a98418e8880457b4440872c557a55550.ssl.cf3.rackcdn.com/brands/souljam_3.jpg", "SoulJam", "Foundry, Studio & Fusion", "01/01/1970");
        EventCard applebum = new EventCard("https://mixmag.net/assets/uploads/images/_facebook/Applebum-DJs.jpg", "AppleBum", "CODE Sheffield", "01/01/1970");
        eventArrayList.add(popTarts);
        eventArrayList.add(soulJam);
        eventArrayList.add(applebum);
    }

    public void likesTopCard(View view) {
        final View view1 = view;
        view.setClickable(false);
//        parentView.removeView(parentView.findViewById(parentView.getChildCount()-2));
        if(parentView.getChildCount() > 2 && parentView.getChildAt(parentView.getChildCount()-1) instanceof CardView) {
            // Use this to get info!
            View card = parentView.getChildAt(parentView.getChildCount()-1);
            ObjectAnimator animation = ObjectAnimator.ofFloat(card, "translationX", 0, Resources.getSystem().getDisplayMetrics().widthPixels);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setDuration(200);
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    parentView.removeViewAt(parentView.getChildCount() - 1);
                    view1.setClickable(true);
                }
            });
            animation.start();

        }
    }

    public void dislikesTopCard(View view) {
        final View view1 = view;
        view.setClickable(false);
//        parentView.removeView(parentView.findViewById(parentView.getChildCount()-2));
        if(parentView.getChildCount() > 2 && parentView.getChildAt(parentView.getChildCount()-1) instanceof CardView) {
            // Use this to get info!
            View card = parentView.getChildAt(parentView.getChildCount()-1);
            ObjectAnimator animation = ObjectAnimator.ofFloat(card, "translationX", 0, -Resources.getSystem().getDisplayMetrics().widthPixels);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setDuration(200);
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    parentView.removeViewAt(parentView.getChildCount() - 1);
                    view1.setClickable(true);
                }
            });
            animation.start();

        }
    }

    class MovingCardListener implements View.OnTouchListener {

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

                    x_cord = (int) event.getRawX();
                    // smoother animation.
                    y_cord = (int) event.getRawY();

                    finalContainerView.setX(x_cord - x);
                    finalContainerView.setY(y_cord - y);


                    if (x_cord >= screenCenter) {
                        finalContainerView.setRotation((float) ((x_cord - screenCenter) * (Math.PI / 32)));
                        if (x_cord > (screenCenter + (screenCenter / 2))) {
                            if (x_cord > (windowwidth - (screenCenter / 4))) {
                                Likes = 2;
                            } else {
                                Likes = 0;
                            }
                        } else {
                            Likes = 0;
                        }
                    } else {
                        // rotate image while moving
                        finalContainerView.setRotation((float) ((x_cord - screenCenter) * (Math.PI / 32)));
                        if (x_cord < (screenCenter / 2)) {
                            if (x_cord < screenCenter / 4) {
                                Likes = 1;
                            } else {
                                Likes = 0;
                            }
                        } else {
                            Likes = 0;
                        }
                    }

                    break;
                case MotionEvent.ACTION_UP:

                    x_cord = (int) event.getRawX();
                    y_cord = (int) event.getRawY();


                    if (Likes == 0) {
                        ObjectAnimator animationX = ObjectAnimator.ofFloat(finalContainerView, "translationX", 0);
                        ObjectAnimator animationR = ObjectAnimator.ofFloat(finalContainerView, "rotation", 0);
                        ObjectAnimator animationY = ObjectAnimator.ofFloat(finalContainerView, "translationY", 0);
                        animationX.setInterpolator(new AccelerateDecelerateInterpolator());
                        animationR.setInterpolator(new AccelerateDecelerateInterpolator());
                        animationY.setInterpolator(new AccelerateDecelerateInterpolator());
                        animationX.setDuration(500);
                        animationR.setDuration(500);
                        animationY.setDuration(500);
                        animationX.start();
                        animationR.start();
                        animationY.start();
                        topCard = parentView.getChildAt(parentView.getChildCount());
                    } else if (Likes == 1) {
                        finalContainerView.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                parentView.removeView(finalContainerView);
                                topCard = parentView.getChildAt(parentView.getChildCount());
                            }
                        });
                    } else if (Likes == 2) {
                        finalContainerView.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                parentView.removeView(finalContainerView);
                                topCard = parentView.getChildAt(parentView.getChildCount());
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}