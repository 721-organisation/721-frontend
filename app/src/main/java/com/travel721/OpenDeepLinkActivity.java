package com.travel721;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class OpenDeepLinkActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        setContentView(R.layout.blank_layout);
        String eventIdToDeepLink = appLinkData.getPathSegments().get(appLinkData.getPathSegments().size() - 1);
        Toast.makeText(this, "Opening event id: " + eventIdToDeepLink, Toast.LENGTH_LONG).show();

    }
}
