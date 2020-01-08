package com.travel721.analytics;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.SOURCE)
// Strictly enforce analytics events to be of these preset analytics values
@StringDef(value = {
        ReleaseAnalyticsEvent.USER_SWIPED_RIGHT,
        ReleaseAnalyticsEvent.USER_SWIPED_LEFT,
        ReleaseAnalyticsEvent.USER_SWIPED_DOWN,
        ReleaseAnalyticsEvent.SETTINGS_OPENED,
        ReleaseAnalyticsEvent.USER_OPENED_LIST_EVENTS_ACTIVITY,
        ReleaseAnalyticsEvent.USER_CLICKS_EVENT_IN_LIKED_EVENT_LIST,
        ReleaseAnalyticsEvent.TEST_RELEASE_ANALYTICS_EVENT,
        ReleaseAnalyticsEvent.USER_CONVERSION_EVENT_AFF_LINK_CLICK,
        ReleaseAnalyticsEvent.USER_POSITIVE_FEEDBACK,
        ReleaseAnalyticsEvent.USER_NEGATIVE_FEEDBACK,
        ReleaseAnalyticsEvent.HAVING_FUN_QUESTION_POSITIVE_RESPONSE,
        ReleaseAnalyticsEvent.HAVING_FUN_QUESTION_NEGATIVE_RESPONSE,
        ReleaseAnalyticsEvent.FINDING_EXPERIENCES_YOU_LIKE_QUESTION_POSITIVE_RESPONSE,
        ReleaseAnalyticsEvent.FINDING_EXPERIENCES_YOU_LIKE_QUESTION_NEGATIVE_RESPONSE,
        ReleaseAnalyticsEvent.HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION_POSITIVE_RESPONSE,
        ReleaseAnalyticsEvent.HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION_NEGATIVE_RESPONSE,
        ReleaseAnalyticsEvent.DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION_POSITIVE_RESPONSE,
        ReleaseAnalyticsEvent.DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION_NEGATIVE_RESPONSE,
        ReleaseAnalyticsEvent.NEED_MORE_HELP_QUESTION_POSITIVE_RESPONSE,
        ReleaseAnalyticsEvent.NEED_MORE_HELP_QUESTION_NEGATIVE_RESPONSE,

        ReleaseAnalyticsEvent.SHARE_CLICKED,
        ReleaseAnalyticsEvent.FILTER_CLICKED_IN_DISCOVER,
        ReleaseAnalyticsEvent.FILTER_CLICKED_IN_NEAR_ME,
        ReleaseAnalyticsEvent.EVENT_DELETED
})
public @interface ReleaseAnalyticsEvent {
    String USER_SWIPED_RIGHT = "User_Liked_Event";
    String USER_SWIPED_LEFT = "User_Disliked_Event";
    String USER_SWIPED_DOWN = "User_Viewed_More_Event_Info";
    String SETTINGS_OPENED = "User_Opened_App_Settings";
    String USER_OPENED_LIST_EVENTS_ACTIVITY = "User_Saw_Their_Liked_Events";
    String USER_CLICKS_EVENT_IN_LIKED_EVENT_LIST = "User_Reviewed_Liked_Event_Info";
    String USER_CONVERSION_EVENT_AFF_LINK_CLICK = "User_Affiliate_Link_Click";

    String TEST_RELEASE_ANALYTICS_EVENT = "PleaseIgnoreThisStatistic";
    String USER_POSITIVE_FEEDBACK = "FeedbackCardPositiveFeedback";
    String USER_NEGATIVE_FEEDBACK = "FeedbackCardNegativeFeedback";


    // Feedback card text
    String HAVING_FUN_QUESTION = "Having fun?";
    String HAVING_FUN_QUESTION_POSITIVE_RESPONSE = "HavingFunPositiveResponse";
    String HAVING_FUN_QUESTION_NEGATIVE_RESPONSE = "HavingFunNegativeResponse";
    String FINDING_EXPERIENCES_YOU_LIKE_QUESTION = "Finding experiences you like?";
    String FINDING_EXPERIENCES_YOU_LIKE_QUESTION_POSITIVE_RESPONSE = "FindingExperiencesYouLikePositiveResponse";
    String FINDING_EXPERIENCES_YOU_LIKE_QUESTION_NEGATIVE_RESPONSE = "FindingExperiencesYouLikeNegativeResponse";
    String HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION = "Have you been to an event yet?";
    String HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION_POSITIVE_RESPONSE = "HaveYouBeenToAnEventYetPositiveResponse";
    String HAVE_YOU_BEEN_TO_AN_EVENT_YET_QUESTION_NEGATIVE_RESPONSE = "HaveYouBeenToAnEventYetNegativeResponse";
    String DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION = "Discovered something you didn't know?";
    String DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION_POSITIVE_RESPONSE = "DiscoveredSomethingYouDidntKnowPositiveResponse";
    String DISCOVERED_SOMETHING_YOU_DIDNT_KNOW_QUESTION_NEGATIVE_RESPONSE = "DiscoveredSomethingYouDidntKnowNegativeResponse";
    String NEED_MORE_HELP_QUESTION = "Need more help to find cool experiences?";
    String NEED_MORE_HELP_QUESTION_POSITIVE_RESPONSE = "NeedMoreHelpToFindCoolExperiencesPositiveResponse";
    String NEED_MORE_HELP_QUESTION_NEGATIVE_RESPONSE = "NeedMoreHelpToFindCoolExperiencesNegativeResponse";

    // 2020 new event metrics
    String EVENT_DELETED = "Event Deleted from My721";
    String SHARE_CLICKED = "Share Event Clicked";
    String FILTER_CLICKED_IN_DISCOVER = "Filter Clicked in Discover";
    String FILTER_CLICKED_IN_NEAR_ME = "Filter Clicked in Near Me";
}
