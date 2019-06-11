package com.travel721;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

import static com.travel721.Constants.*;

public class SwipeUpToDismissCardTouchController implements View.OnTouchListener {

    private final FragmentManager fragmentManager;
    protected View card;
    protected FrameLayout parentLayout;
    protected int windowHeight;
    protected boolean dismissed = false;

    public SwipeUpToDismissCardTouchController(View card, FrameLayout parentLayout, int windowHeight, FragmentManager fragmentManager) {
        this.card = card;
        this.parentLayout = parentLayout;
        this.windowHeight = windowHeight;
        this.fragmentManager = fragmentManager;
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
                    card.animate().translationY(-windowHeight).setDuration(GLOBAL_ANIMATION_DURATION).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            parentLayout.removeView(card);
                            card = null;
                            parentLayout = null;
                            SupportMapFragment f = (SupportMapFragment) fragmentManager
                                    .findFragmentById(R.id.mapView);
                            if (f != null)
                                fragmentManager.beginTransaction().remove(f).commit();
                        }
                    });
                    dismissed = true;
                }
                break;
            default:
                break;
        }
        v.performClick();
        return true;
    }

}