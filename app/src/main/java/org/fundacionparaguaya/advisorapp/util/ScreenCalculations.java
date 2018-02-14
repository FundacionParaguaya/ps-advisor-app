package org.fundacionparaguaya.advisorapp.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Functions for handling different screen sizes
 */

public class ScreenCalculations {

    public static boolean is7InchTablet(Context context)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        return (convertsPixelsToDp(metrics.heightPixels, context) < 600);
    }
    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertsPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    /**
     * Calculates the number of columns that we can fit on the screen. Assumes the grid view
     * takes up the entire width of the device. (If this isn't the case, use function that specifies
     * container width
     *
     * @param itemWidth Width of the item
     * @param margin Margin on each side of the item
     * @param context
     * @return Max number of columns that can fit
     */
    public static int calculateNoOfColumns(float itemWidth, float margin, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return calculateNoOfColumns(displayMetrics.widthPixels, itemWidth, margin, context );
    }

    /**
     * Calculates the max number of columns that can fit in a container of a given size.
     *
     * @param containerWidthPx The width of the container that the columns are in (Pixels)
     * @param itemWidth Width of the item
     * @param margin Margin on each side of the item
     * @return Max number of columns that can fit
     */
    public static int calculateNoOfColumns(float containerWidthPx, float itemWidth, float margin, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth =containerWidthPx / displayMetrics.density;

        return (int) (dpWidth/ (itemWidth + 2*margin));
    }
}