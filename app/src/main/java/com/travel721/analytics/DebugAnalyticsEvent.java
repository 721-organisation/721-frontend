package com.travel721.analytics;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.travel721.analytics.AnalyticsHelper.DEBUG_USED_FUSED_LOCATION_PROVIDER;
import static com.travel721.analytics.AnalyticsHelper.DEBUG_USED_NATIVE_LOCATION_MANAGER;

@Retention(RetentionPolicy.SOURCE)
// Strictly enforce debug analytics events to be of these preset debug 0analytics values
@StringDef({
        // These two are used to log which location provider was quickest to report a location.
        // We're keeping this for now to experiment which provider is fastest.
        DEBUG_USED_FUSED_LOCATION_PROVIDER,
        DEBUG_USED_NATIVE_LOCATION_MANAGER
})
public @interface DebugAnalyticsEvent {
}