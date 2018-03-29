package org.fundacionparaguaya.advisorapp.ui.base;

import org.fundacionparaguaya.advisorapp.ui.base.AbstractStackedFrag;

/**
 * Call back for fragments that want their parent fragment/activity to navigate
 */

public interface NavigationListener
{
    void onNavigateNext(AbstractStackedFrag frag);

    void onNavigateBack();
}
