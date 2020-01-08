package com.travel721.analytics;


import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {
        ReleaseScreenNameAnalytic.NEAR_ME_VIEWED,
        ReleaseScreenNameAnalytic.DISCOVER_SETTINGS_VIEWED,
        ReleaseScreenNameAnalytic.MY_721_VIEWED,
        ReleaseScreenNameAnalytic.DISCOVER_LAUNCHED,
        ReleaseScreenNameAnalytic.EVENT_MORE_INFO_VIEWED
})


public @interface ReleaseScreenNameAnalytic {
    // 2020 new screen_view screen name metrics
    String NEAR_ME_VIEWED = "Near Me Viewed";
    String DISCOVER_SETTINGS_VIEWED = "Discover Location Selector Viewed";
    String DISCOVER_LAUNCHED = "Discover Launched";
    String MY_721_VIEWED = "My 721 Clicked";
    String EVENT_MORE_INFO_VIEWED = "Event More Information Viewed";
}
