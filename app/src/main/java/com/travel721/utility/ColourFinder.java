package com.travel721.utility;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.travel721.R;

/**
 * This class is designed to match an overlay colour to a specified colour.
 * <p>
 * Be very careful when modifying this class, there can be confusion between
 * resolved and unresolved colours and their respective resource ids.
 *
 * @author Bhav
 */
public class ColourFinder {

    /**
     * This integer array contains the RESOURCE IDs of some
     * of the colours that were used to generate the overlay
     * SVG/XML files. See /res/drawable/ic_overlay_*.xml
     * and /res/values/colors.xml for more insight.
     */
    public static SparseIntArray colourSwatch() {
        SparseIntArray sparseIntArray = new SparseIntArray();
        sparseIntArray.put(R.color.darkgreen5, R.drawable.ic_overlay_dark_green);
        sparseIntArray.put(R.color.bronze5, R.drawable.ic_overlay_bronze);
        sparseIntArray.put(R.color.green5, R.drawable.ic_overlay_green);
        sparseIntArray.put(R.color.navy5, R.drawable.ic_overlay_navy);
        sparseIntArray.put(R.color.pink5, R.drawable.ic_overlay_pink);
        sparseIntArray.put(R.color.purple5, R.drawable.ic_overlay_purple);
        sparseIntArray.put(R.color.red5, R.drawable.ic_overlay_red);
        sparseIntArray.put(R.color.teal5, R.drawable.ic_overlay_teal);
        return sparseIntArray;
    }

    /**
     * This method matches an RGB COLOUR given as a parameter the
     * the RESOURCE ID of it's matched overlay.
     *
     * @param colourToMatch the int-packed RGB value
     * @param context       Context, required to resolve the IDs in the
     *                      colourSwatch array to real RGB values.
     * @return R.drawable.* literal of the overlay matched
     */
    public static int getColourMatchedOverlay(int colourToMatch, @NonNull Context context) {
        // Unpacks RGB values from parameter
        int colourToMatchRed = (colourToMatch >> 16) & 0xFF;
        int colourToMatchGreen = (colourToMatch >> 8) & 0xFF;
        int colourToMatchBlue = colourToMatch & 0xFF;

        // Setup variables
        int currentLowest = Integer.MAX_VALUE;
        int closestOverlay = 999;
        // Loop through each Resource ID in the swatch
        for (int i = 0; i < colourSwatch().size(); i++) {
            int key = colourSwatch().keyAt(i);
            // Resolve the actual colour
            int col = ContextCompat.getColor(context, key);
            // Extract RGB values
            int swatchRedValue = (col >> 16) & 0xFF;
            int swatchGreenValue = (col >> 8) & 0xFF;
            int swatchBlueValue = col & 0xFF;
            // Calculate Euclidean distance between primary colours
            int redDifference = swatchRedValue - colourToMatchRed;
            int greenDifference = swatchGreenValue - colourToMatchGreen;
            int blueDifference = swatchBlueValue - colourToMatchBlue;
            // Square each value (removes effect of negative distances).
            // There's no need to square root the overall value
            int overallDistance = redDifference * redDifference + greenDifference * greenDifference +
                    blueDifference * blueDifference;
            // Set the current colour if it matches better.
            if (overallDistance < currentLowest) {
                currentLowest = overallDistance;
                closestOverlay = colourSwatch().get(key);
            }
        }
        // Send back the closest color in the swatch
        Log.v("CS", "Closest Overlay: " + closestOverlay);
        return closestOverlay;
    }
}
