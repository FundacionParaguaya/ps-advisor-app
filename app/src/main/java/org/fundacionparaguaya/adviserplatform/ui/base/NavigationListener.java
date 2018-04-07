package org.fundacionparaguaya.adviserplatform.ui.base;

/**
 * Call back for fragments that want their parent fragment/activity to navigate
 */

public interface NavigationListener
{
    void onNavigateNext(AbstractStackedFrag frag);

    void onNavigateBack();
}
