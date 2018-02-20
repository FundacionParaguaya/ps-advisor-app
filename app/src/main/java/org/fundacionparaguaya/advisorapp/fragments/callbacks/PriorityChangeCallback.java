package org.fundacionparaguaya.advisorapp.fragments.callbacks;

import org.fundacionparaguaya.advisorapp.viewcomponents.PriorityDetailPopupWindow;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

/**
 * Callback for when an individual priority is changed/removed/replaced etc.
 */

public interface PriorityChangeCallback
{

    void onPriorityChanged(PriorityDetailPopupWindow window, PriorityDetailPopupWindow.PriorityPopupFinishedEvent e);
}
