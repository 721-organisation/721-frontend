package com.travel721;

import java.util.Map;

public class Constants {
    public final static int SLIDE_ANIMATION_DURATION = 300;
    public static final int GLOBAL_ANIMATION_DURATION = 100;
    public static final double CARD_SWIPING_STICKINESS = 0.4;
    public static String API_ROOT_URL = "https://temp-243314.appspot.com/api/";
    public static final float testLat = 53.379426f;
    public static final float testLong = -1.477496f;

    public static final int testRadius = 5;

    public static final int testDaysFromNow = 4;

    /**
     * Returns an encoded URL for Profile Search
     * @param iid The Firebase instance ID to encode
     * @return The encoded URL
     */
    public static final String profileSearchURL(String iid) {
        return "?filter=%7B%22where%22%3A%7B%22profileId%22%3A%22" + iid + "%22%7D%7D";
    }


}
