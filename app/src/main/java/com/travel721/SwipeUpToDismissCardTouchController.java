package com.travel721;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class SwipeUpToDismissCardTouchController implements View.OnTouchListener {

    private final View card;
    private final FrameLayout parentLayout;
    private final int windowHeight;

    public SwipeUpToDismissCardTouchController(View card, FrameLayout parentLayout, int windowHeight) {
        this.card = card;
        this.parentLayout = parentLayout;
        this.windowHeight = windowHeight;
    }

    private int x_cord;
    private int y_cord;
    private int x;
    private int y;

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

                if (y_cord < y) {
                    card.setY(y_cord - y);
                }
                break;
            case MotionEvent.ACTION_UP:
                x_cord = (int) event.getRawX();
                y_cord = (int) event.getRawY();
                if (y_cord < 1.5 * y) {
                    card.animate().translationY(-windowHeight).setDuration(200).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            parentLayout.removeView(card);
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