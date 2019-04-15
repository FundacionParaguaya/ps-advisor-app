package org.fundacionparaguaya.adviserplatform.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;


public class Utilities {

    private Utilities() {
        //Utilities classes does not use constructor
    }

    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        Integer resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog dialog = googleApiAvailability.getErrorDialog(activity, resultCode, 0);
            if (dialog != null) {
                dialog.show();
            }
            return false;
        }
        return true;
    }

    public static Point getScreenSizeInPixels(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static boolean hasBeenXSeconds(Date lastSync, long syncIntervalMs) {
        boolean deadLine = true;
        if(lastSync != null) {
            final Date nextSync = DateUtils.addMilliseconds(lastSync, (int) syncIntervalMs);
            deadLine = nextSync.before(new Date());
        }
        return deadLine;
    }
}
