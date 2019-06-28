package com.travel721;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
        setContentView(R.layout.event_more_info_card);
        _root = findViewById(R.id.moreInfoConstraintLayout);
        _root.setOnTouchListener(this);
        // Initialise maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    double latitude = Double.parseDouble(eventCard.getLocationLatitude());
                    double longitude = Double.parseDouble(eventCard.getLocationLongitude());
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title("Marker"));
                    // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17));
                    // Sets the center of the map to Mountain View
                }
            });
        }
        TextView desc = findViewById(R.id.eventLongDescription);
        desc.setText(eventCard.getDescription());
        TextView source = findViewById(R.id.eventProvidedBy);
        source.setText(getString(R.string.event_provided_by_placeholder, eventCard.getSourceTag().toLowerCase()));
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
                break;
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
        AnalyticsHelper.logEvent(this, AnalyticsHelper.USER_CONVERSION_EVENT_AFF_LINK_CLICK, null);
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setShowTitle(true)
                .build();

        customTabsIntent.launchUrl(this, Uri.parse(eventCard.getImgURL()));
    }
}