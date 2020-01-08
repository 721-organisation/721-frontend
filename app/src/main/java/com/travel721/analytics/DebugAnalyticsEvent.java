package com.travel721.analytics;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.SOURCE)
// Strictly enforce debug analytics events to be of these preset debug 0analytics values
@StringDef({
        // These two are used to log which location provider was quickest to report a location.
        // We're keeping this for now to experiment which provider is fastest.
        DebugAnalyticsEvent.DEBUG_USED_FUSED_LOCATION_PROVIDER,
        DebugAnalyticsEvent.DEBUG_USED_NATIVE_LOCATION_MANAGER
})
public @interface DebugAnalyticsEvent {
    // Debug Constants
    String DEBUG_USED_FUSED_LOCATION_PROVIDER = "FusedLocationProvider_Was_Used";
    String DEBUG_USED_NATIVE_LOCATION_MANAGER = "NativeLocationManager_Was_Used";
}