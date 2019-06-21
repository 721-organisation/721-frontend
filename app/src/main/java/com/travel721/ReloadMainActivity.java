package com.travel721;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

public class ReloadMainActivity extends SplashActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.loading_spinner_layout);
        Snackbar.make(findViewById(R.id.loading_spinner_view), "We're reloading the latest experiences for you...", Snackbar.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
    }
}
