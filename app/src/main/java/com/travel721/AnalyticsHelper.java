package com.travel721;


import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringDef;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AnalyticsHelper {
    // Don't show the debug warning more than once
    private static boolean warn_user_analytics_disabled = false;

    // These constants provide a 'translation' between in-app events and meaningful analytics
    public static final String USER_SWIPED_RIGHT = "UserLikedEvent";
    public static final String USER_SWIPED_LEFT = "UserDislikedEvent";
    public static final String USER_SWIPED_DOWN = "UserViewedMoreEventInfo";
    public static final String SETTINGS_OPENED = "UserOpenedAppSettings";
    public static final String USER_OPENED_LIST_EVENTS_ACTIVITY = "UserSawTheirLikedEvents";
    public static final String USER_CLICKS_EVENT_IN_LIKED_EVENT_LIST = "UserReviewedLikedEventInfo";
    public static final String TEST_RELEASE_ANALYTICS_EVENT = "PleaseIgnoreThisStatistic";

    @Retention(RetentionPolicy.SOURCE)
    // Strictly enforce analytics events to be of the the preset analytics values
    @StringDef({
            USER_SWIPED_RIGHT,
            USER_SWIPED_LEFT,
            USER_SWIPED_DOWN,
            SETTINGS_OPENED,
            USER_OPENED_LIST_EVENTS_ACTIVITY,
            USER_CLICKS_EVENT_IN_LIKED_EVENT_LIST,
            TEST_RELEASE_ANALYTICS_EVENT
    })
    public @interface AnalyticsEvent {
    }


    /**
     * This is the custom Analytics logger for 721.
     * Analytics are NOT reported in debug mode.
     *
     * @param context The Activity that an event is being reported in.
     * @param event   A
     * @param bundle
     */
    public static void logEvent(Context context, @AnalyticsEvent String event, @Nullable Bundle bundle) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

        // Do not report RELEASE information in debug build
        if (BuildConfig.ENABLE_FIREBASE_ANALYTICS) {
            mFirebaseAnalytics.logEvent(event, bundle);
        } else if (!warn_user_analytics_disabled) {
            warn_user_analytics_disabled = true;
            Toast.makeText(context, "Analytics: Disabled in debug mode. Do not distribute this version of the app.", Toast.LENGTH_SHORT).show();
        }
    }
}
