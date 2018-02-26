package org.fundacionparaguaya.advisorapp.util;

import android.app.Activity;
import android.app.Dialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by Mone Elokda on 2/25/2018.
 */

public class Utilities {
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
}
