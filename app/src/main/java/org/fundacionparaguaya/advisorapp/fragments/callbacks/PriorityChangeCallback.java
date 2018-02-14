package org.fundacionparaguaya.advisorapp.fragments.callbacks;

import org.fundacionparaguaya.advisorapp.util.MixpanelHelper;
import org.fundacionparaguaya.advisorapp.viewcomponents.PriorityDetailPopupWindow;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

/**
 * Callback for when an individual priority is changed/removed/replaced etc.
 */

public class PriorityChangeCallback
{
    SharedSurveyViewModel mSharedSurveyViewModel;

    public PriorityChangeCallback(SharedSurveyViewModel viewmodel)
    {
        mSharedSurveyViewModel = viewmodel;
    }

    public void onPriorityChanged(PriorityDetailPopupWindow window, PriorityDetailPopupWindow.PriorityPopupFinishedEvent e) {
        window.dismiss();

        switch (e.getResultType())
        {
            case ADD:
            {
                mSharedSurveyViewModel.addPriority(e.getNewPriority());
                break;
            }
            case REPLACE:
            {
                mSharedSurveyViewModel.removePriority(e.getOriginalPriority());
                mSharedSurveyViewModel.addPriority(e.getNewPriority());
                break;
            }
        }
    }
}