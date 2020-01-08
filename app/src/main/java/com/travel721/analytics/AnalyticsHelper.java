package com.travel721.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.travel721.BuildConfig;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class is designed to provide compile-time safety
 * for the types of events we like to be reported to
 * Firebase Analytics
 *
 * @author Bhav
 * @see ReleaseAnalyticsEvent Events reportable in release mode only
 * @see DebugAnalyticsEvent Events reportable in either debug or release mode
 */
public class AnalyticsHelper {
    // Don't show the debug warning more than once
    private static boolean warn_user_analytics_disabled = false;
    /*
     These constants provide a 'translation' between in-app events and meaningful analytics
    */
    // Release Constants


    // Fragment Screen Name Constants
//    public static final String

    public static final ArrayList<String> feedbackQuestions() {
        ArrayList<String> questions = new ArrayList<>();
        questions.add(ReleaseAnalyticsEvent.HAVING_FUN_QUESTION);
        questions.add(ReleaseAnalyticsEvent.FINDING_EXPERIENCES_YOU_LIKE_QUESTION);
        questions.add(ReleaseAnalyticsEvent.HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION);
        questions.add(ReleaseAnalyticsEvent.DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION);
        questions.add(ReleaseAnalyticsEvent.NEED_MORE_HELP_QUESTION);
        return questions;
    }

    public static String getRandomQuestion() {
        Random rand = new Random();
        ArrayList<String> feedbackQs = feedbackQuestions();
        return feedbackQs.get(rand.nextInt(feedbackQs.size()));
    }

    /**
     * This is the custom Analytics logger for 721.
     * Analytics are NOT reported to Firebase in debug mode.
     *
     * @param context The Activity that an event is being reported in.
     * @param event   Must be one of the @ReleaseAnalyticsEvent constants
     * @param bundle  (optional) extra info to bundle in
     * @see ReleaseAnalyticsEvent for permissible events
     */
    public static void logEvent(Context context, @ReleaseAnalyticsEvent String event, @Nullable Bundle bundle) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        // Do not report RELEASE information in debug build
        if (BuildConfig.ENABLE_FIREBASE_ANALYTICS) {
            mFirebaseAnalytics.logEvent(event, bundle);
        } else if (!warn_user_analytics_disabled) {
            warn_user_analytics_disabled = true;
            Toast.makeText(context, "Analytics: Disabled in this build mode. Do not distribute this version of the app.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * WARNING: Do not use this to log events that concern only genuine users.
     * This method is designed especially for debugging events that could genuinely crop up
     * in either internal or external testing. As such, these are always reported to Firebase.
     *
     * @param context The Activity that an event is being reported in.
     * @param event   Must be one of the @DebugAnalyticsEvent constants
     * @param bundle  (optional) extra info to bundle in
     * @see DebugAnalyticsEvent for permissible events
     */
    public static void debugLogEvent(Context context, @DebugAnalyticsEvent String event, @Nullable Bundle bundle) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.logEvent(event, bundle);
    }

    public static void setScreenNameAnalytic(@NonNull Context context, @NonNull Activity activity, @ReleaseScreenNameAnalytic String event, @NonNull String className) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.setCurrentScreen(activity, event, className);
    }
}
