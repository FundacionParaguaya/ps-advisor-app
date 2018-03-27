package org.fundacionparaguaya.advisorapp.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Utilities Relating to the Keyboard
 */

public class KeyboardUtils {
    public static void hideKeyboard(View view, Activity activity) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
