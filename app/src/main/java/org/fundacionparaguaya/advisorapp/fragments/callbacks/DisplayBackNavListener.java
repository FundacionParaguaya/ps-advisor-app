package org.fundacionparaguaya.advisorapp.fragments.callbacks;

/**
 * Interface that allows a fragment to tell it's parent to show back navigation
 */

public interface DisplayBackNavListener {
    void onShowBackNav();

    void onHideBackNav();
}
