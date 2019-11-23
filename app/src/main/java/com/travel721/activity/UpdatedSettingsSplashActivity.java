package com.travel721.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.travel721.R;

public class UpdatedSettingsSplashActivity extends SplashActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash_screen);
        Snackbar.make(findViewById(R.id.loading_spinner_view), getResources().getString(R.string.reloading_app), Snackbar.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
    }
}
