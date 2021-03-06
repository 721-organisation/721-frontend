package com.travel721.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Email721TeamRedirectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","721journeys@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact 721 Team");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi 721 Team! \n");
        startActivity(Intent.createChooser(emailIntent, "Send an email..."));
        finish();
    }
}
