package org.fundacionparaguaya.advisorapp.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.NavigationListener;

/**
 * A StackedFrag is a fragment that is nested in a TabbedFrag. When it needs to navigate, it is able to communicate
 * with the parent fragment.
 */

public abstract class StackedFrag extends Fragment {
    private NavigationListener mNavigateCallback;

    /**
     * Gets parent fragment (of type TabbedFrag) and then calls navigation function. Current
     * fragment gets placed in a stack.
     *
     * @param fragment fragment to navigate to
     */
    public void navigateTo(StackedFrag fragment) {
        mNavigateCallback.onNavigateNext(fragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            //if this is a nested fragment
            if (getParentFragment() != null) {
                mNavigateCallback = (NavigationListener) getParentFragment();
            }
            else {
                //just nested inside of an activity
                mNavigateCallback = (NavigationListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent activity or fragment must implement NavigationListener");
        }
    }
}
