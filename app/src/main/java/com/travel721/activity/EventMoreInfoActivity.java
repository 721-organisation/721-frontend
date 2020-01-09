package com.travel721.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.travel721.R;
import com.travel721.analytics.AnalyticsHelper;
import com.travel721.analytics.ReleaseAnalyticsEvent;
import com.travel721.analytics.ReleaseScreenNameAnalytic;
import com.travel721.card.EventCard;

import static com.travel721.analytics.ReleaseAnalyticsEvent.USER_CONVERSION_EVENT_AFF_LINK_CLICK;

public class EventMoreInfoActivity extends AppCompatActivity implements View.OnTouchListener {

    EventCard eventCard;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventCard = getIntent().getParcelableExtra("eventCard");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_event_more_info);
        _root = findViewById(R.id.moreInfoConstraintLayout);
        _root.setOnTouchListener(this);
        // Initialise maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                double latitude = Double.parseDouble(eventCard.getLocationLatitude());
                double longitude = Double.parseDouble(eventCard.getLocationLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(eventCard.getName()));
                // Construct a CameraPosition focusing on event location and animate the camera to that position.
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17));
            });
        }
        Button button = findViewById(R.id.eventHyperlink);
        if (eventCard.getSourceTag().toUpperCase().equals("BUSINESS")) {
            button.setText(getString(R.string.event_hyperlink_text, getResources().getString(R.string.partner_721), eventCard.getName()));
        } else {
            button.setText(getString(R.string.event_hyperlink_text, eventCard.getSourceTag(), eventCard.getName()));
        }
        TextView tv = findViewById(R.id.eventLongDescription);
        tv.setText(eventCard.getDescription());
        tv = findViewById(R.id.eventProvidedBy);
        if (eventCard.getSourceTag().toUpperCase().equals("BUSINESS")) {
            tv.setText(getString(R.string.event_provided_by_placeholder, getResources().getString(R.string.partner_721)));
        } else {
            tv.setText(getString(R.string.event_provided_by_placeholder, eventCard.getSourceTag().toLowerCase()));
        }
        tv = findViewById(R.id.eventMoreInfoDate);
        tv.setText(eventCard.getFormattedDate());
        tv = findViewById(R.id.eventMoreInfoTime);
        tv.setText(eventCard.getTime());
        tv = findViewById(R.id.eventMoreInfoLocation);
        tv.setText(eventCard.getVenueName());
        tv = findViewById(R.id.eventMoreInfoName);
        tv.setText(eventCard.getName());
        tv = findViewById(R.id.eventMoreInfoPrice);
        tv.setText(eventCard.getPrice());
        ChipGroup tagChipGroup = findViewById(R.id.eventTagChipGroup);
        for (String s : eventCard.tags) {
            Chip chip = new Chip(this);
            chip.setText(s);
            tagChipGroup.addView(chip);
        }
        AnalyticsHelper.setScreenNameAnalytic(this, this, ReleaseScreenNameAnalytic.DISCOVER_LAUNCHED, getClass().getName());
    }

    private int _yDelta;
    ViewGroup _root;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int Y = (int) motionEvent.getRawY();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                if (Y > 0.9 * _yDelta) {
                    // Reposition activity
                    FrameLayout.LayoutParams uplayoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                    uplayoutParams.topMargin = 0;
                    uplayoutParams.bottomMargin = 0;
                    view.setLayoutParams(uplayoutParams);
                } else {
                    finish();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if (Y - _yDelta < 0) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.topMargin = Y - _yDelta;
                    layoutParams.bottomMargin = _yDelta - Y;
                    view.setLayoutParams(layoutParams);
                }
                break;
        }
        _root.invalidate();
        return true;
    }

    /**
     * Opens a Chrome Custom Tab with the event's webpage
     *
     * @param view
     */
    public void openEventInChromeCustomTab(View view) {
        AnalyticsHelper.logEvent(this, USER_CONVERSION_EVENT_AFF_LINK_CLICK, null);
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setShowTitle(true)
                .build();

        customTabsIntent.launchUrl(this, Uri.parse(eventCard.getEventHyperLink()));
    }

    public void shareEvent(View view) {
        AnalyticsHelper.logEvent(this, ReleaseAnalyticsEvent.SHARE_CLICKED, null);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, "share721.appspot.com/event/" + eventCard.getEventSourceID());
        i.setType("text/plain");
        Intent shareIntent = Intent.createChooser(i, getString(R.string.share_721_experience, eventCard.getName()));
        startActivity(shareIntent);
    }
}