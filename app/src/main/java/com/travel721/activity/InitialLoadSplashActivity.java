package com.travel721.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.travel721.R;

/**
 * This is the Activity that first loads.
 * Snackbars (the small messages) are enabled.
 *
 * @author Bhav
 */
public class InitialLoadSplashActivity extends SplashActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash_screen);
//        Snackbar.make(findViewById(R.id.loading_spinner_view), getResources().getString(R.string.loading_app_tooltip), Snackbar.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
// Load the ImageView that will host the animation and
        // set its background to our AnimationDrawable XML resource.
        ImageView img = (ImageView) findViewById(R.id.loading_dots);
        img.setBackgroundResource(R.drawable.loading_dots_animation);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
    }
}
