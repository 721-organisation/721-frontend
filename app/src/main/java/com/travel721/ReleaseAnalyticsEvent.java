package com.travel721;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.travel721.AnalyticsHelper.SETTINGS_OPENED;
import static com.travel721.AnalyticsHelper.TEST_RELEASE_ANALYTICS_EVENT;
import static com.travel721.AnalyticsHelper.USER_CLICKS_EVENT_IN_LIKED_EVENT_LIST;
import static com.travel721.AnalyticsHelper.USER_CONVERSION_EVENT_AFF_LINK_CLICK;
import static com.travel721.AnalyticsHelper.USER_NEGATIVE_FEEDBACK;
import static com.travel721.AnalyticsHelper.USER_OPENED_LIST_EVENTS_ACTIVITY;
import static com.travel721.AnalyticsHelper.USER_POSITIVE_FEEDBACK;
import static com.travel721.AnalyticsHelper.USER_SWIPED_DOWN;
import static com.travel721.AnalyticsHelper.USER_SWIPED_LEFT;
import static com.travel721.AnalyticsHelper.USER_SWIPED_RIGHT;

@Retention(RetentionPolicy.SOURCE)
// Strictly enforce analytics events to be of these preset analytics values
@StringDef({
        USER_SWIPED_RIGHT,
        USER_SWIPED_LEFT,
        USER_SWIPED_DOWN,
        SETTINGS_OPENED,
        USER_OPENED_LIST_EVENTS_ACTIVITY,
        USER_CLICKS_EVENT_IN_LIKED_EVENT_LIST,
        TEST_RELEASE_ANALYTICS_EVENT,
        USER_CONVERSION_EVENT_AFF_LINK_CLICK,
        USER_POSITIVE_FEEDBACK,
        USER_NEGATIVE_FEEDBACK
})
public @interface ReleaseAnalyticsEvent {
}
