package com.travel721.analytics;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.travel721.analytics.AnalyticsHelper.DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION_NEGATIVE_RESPONSE;
import static com.travel721.analytics.AnalyticsHelper.DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION_POSITIVE_RESPONSE;
import static com.travel721.analytics.AnalyticsHelper.FINDING_EXPERIENCES_YOU_LIKE_QUESTION_NEGATIVE_RESPONSE;
import static com.travel721.analytics.AnalyticsHelper.FINDING_EXPERIENCES_YOU_LIKE_QUESTION_POSITIVE_RESPONSE;
import static com.travel721.analytics.AnalyticsHelper.HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION_NEGATIVE_RESPONSE;
import static com.travel721.analytics.AnalyticsHelper.HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION_POSITIVE_RESPONSE;
import static com.travel721.analytics.AnalyticsHelper.HAVING_FUN_QUESTION_NEGATIVE_RESPONSE;
import static com.travel721.analytics.AnalyticsHelper.HAVING_FUN_QUESTION_POSITIVE_RESPONSE;
import static com.travel721.analytics.AnalyticsHelper.NEED_MORE_HELP_QUESTION_NEGATIVE_RESPONSE;
import static com.travel721.analytics.AnalyticsHelper.NEED_MORE_HELP_QUESTION_POSITIVE_RESPONSE;
import static com.travel721.analytics.AnalyticsHelper.SETTINGS_OPENED;
import static com.travel721.analytics.AnalyticsHelper.TEST_RELEASE_ANALYTICS_EVENT;
import static com.travel721.analytics.AnalyticsHelper.USER_CLICKS_EVENT_IN_LIKED_EVENT_LIST;
import static com.travel721.analytics.AnalyticsHelper.USER_CONVERSION_EVENT_AFF_LINK_CLICK;
import static com.travel721.analytics.AnalyticsHelper.USER_NEGATIVE_FEEDBACK;
import static com.travel721.analytics.AnalyticsHelper.USER_OPENED_LIST_EVENTS_ACTIVITY;
import static com.travel721.analytics.AnalyticsHelper.USER_POSITIVE_FEEDBACK;
import static com.travel721.analytics.AnalyticsHelper.USER_SWIPED_DOWN;
import static com.travel721.analytics.AnalyticsHelper.USER_SWIPED_LEFT;
import static com.travel721.analytics.AnalyticsHelper.USER_SWIPED_RIGHT;

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
        USER_NEGATIVE_FEEDBACK,
        HAVING_FUN_QUESTION_POSITIVE_RESPONSE,
        HAVING_FUN_QUESTION_NEGATIVE_RESPONSE,
        FINDING_EXPERIENCES_YOU_LIKE_QUESTION_POSITIVE_RESPONSE,
        FINDING_EXPERIENCES_YOU_LIKE_QUESTION_NEGATIVE_RESPONSE,
        HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION_POSITIVE_RESPONSE,
        HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION_NEGATIVE_RESPONSE,
        DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION_POSITIVE_RESPONSE,
        DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION_NEGATIVE_RESPONSE,
        NEED_MORE_HELP_QUESTION_POSITIVE_RESPONSE,
        NEED_MORE_HELP_QUESTION_NEGATIVE_RESPONSE
})
public @interface ReleaseAnalyticsEvent {
}
