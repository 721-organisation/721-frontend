package com.travel721;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

public class InitialAppLoader extends SplashActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.loading_spinner_layout);
        getWindow().setBackgroundDrawable(null);
        Snackbar.make(findViewById(R.id.loading_spinner_view), getResources().getString(R.string.loading_app_tooltip), Snackbar.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
    }
}
