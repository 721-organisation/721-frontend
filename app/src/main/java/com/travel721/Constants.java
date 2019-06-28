package com.travel721;

import java.util.Random;

public class Constants {
    public static final int REQUEST_CHECK_LOCATION_SETTINGS = 999;
    public static final int REQUEST_LOCATION_PERMISSIONS = 999;

    public final static int SLIDE_ANIMATION_DURATION = 300;
    public static String API_ROOT_URL = "https://temp-243314.appspot.com/api/";

    public static final int testRadius = 5;
    public static final int testDaysFromNow = 4;

    /*
        The following items are not necessarily constants, but are ugly to keep inline
     */
    /**
     * Returns an encoded URL for Profile Search
     *
     * @param iid The Firebase instance ID to encode
     * @return The encoded URL
     */
    public static final String profileSearchURL(String iid) {
        return "?filter=%7B%22where%22%3A%7B%22profileId%22%3A%22" + iid + "%22%7D%7D";
    }

    public static final String eventProfileSearchFilter(String profileID) {
        return "%7B%22where%22%3A%7B%22profileId%22%3A+%22" + profileID + "%22%2C%22swipe%22%3Atrue%7D%7D";
    }

    public static final String eventSearchFilter(String eventProfileID) {
        return "%7B%22where%22%3A%7B%22eventSourceId%22%3A%22" + eventProfileID + "%22%7D%7D";
    }

    public static final int getRandomOverlay() {
        // Generate random number
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(7) + 1;
        switch (randomInt) {
            case 1:
                return R.drawable.ic_overlay_bronze;
            case 2:
                return R.drawable.ic_overlay_dark_green;
            case 3:
                return R.drawable.ic_overlay_green;
            case 4:
                return R.drawable.ic_overlay_navy;
            case 5:
                return R.drawable.ic_overlay_pink;
            case 6:
                return R.drawable.ic_overlay_purple;
            case 7:
                return R.drawable.ic_overlay_red;
            case 8:
                return R.drawable.ic_overlay_teal;
            default:
                return R.drawable.ic_overlay_purple;
        }
    }


}
