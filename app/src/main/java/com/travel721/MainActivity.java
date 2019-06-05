package com.travel721;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
                    //finalContainerView.setAlpha(0f);
                    //finalContainerView.animate().alpha(1f).setDuration(1000);
                    ObjectAnimator animation = ObjectAnimator.ofFloat(finalContainerView, "translationY", Resources.getSystem().getDisplayMetrics().heightPixels, 0);
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    animation.setDuration(500);
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
        eventArrayList.add(new EventCard().setEventNameBuilder("SoulJam").setPhotoBuilder("https://www.plantnet.com.au/wp-content/uploads/plantnet-category-blueberries.jpg"));
        eventArrayList.add(new EventCard().setEventNameBuilder("SoulJam").setPhotoBuilder("https://static.wixstatic.com/media/365e99_13a928d25d764e50ade790b527026509~mv2_d_4533_1613_s_2.png/v1/fill/w_393,h_140,al_c,q_80,usm_0.66_1.00_0.01/sj_logo.webp"));
        eventArrayList.add(new EventCard().setEventNameBuilder("SoulJam").setPhotoBuilder("https://www.gstatic.com/devrel-devsite/va102c01c814e4ead6c193f728a0d2922d54f825147aa20062195823f08abef76/android/images/lockup.svg"));
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
                        ObjectAnimator animationX = ObjectAnimator.ofFloat(finalContainerView, "translationX",  0);
                        ObjectAnimator animationR = ObjectAnimator.ofFloat(finalContainerView, "rotation",  0);
                        ObjectAnimator animationY = ObjectAnimator.ofFloat(finalContainerView, "translationY",  0);
                        animationX.setInterpolator(new AccelerateDecelerateInterpolator());
                        animationR.setInterpolator(new AccelerateDecelerateInterpolator());
                        animationY.setInterpolator(new AccelerateDecelerateInterpolator());
                        animationX.setDuration(500);
                        animationR.setDuration(500);
                        animationY.setDuration(500);
                        animationX.start();
                        animationR.start();
                        animationY.start();
                    } else if (Likes == 1) {
                        finalContainerView.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                parentView.removeView(finalContainerView);
                            }
                        });
                    } else if (Likes == 2) {
                        finalContainerView.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                parentView.removeView(finalContainerView);
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


