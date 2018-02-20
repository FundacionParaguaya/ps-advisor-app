package org.fundacionparaguaya.advisorapp.fragments.callbacks;

import org.fundacionparaguaya.advisorapp.viewcomponents.PriorityDetailPopupWindow;

/**
 * Callback for when an individual priority is changed/removed/replaced etc.
 */

public interface PriorityChangeCallback
{
    void onPriorityChanged(PriorityDetailPopupWindow window, PriorityDetailPopupWindow.PriorityPopupFinishedEvent e);
}
