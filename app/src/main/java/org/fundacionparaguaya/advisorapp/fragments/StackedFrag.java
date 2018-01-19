package org.fundacionparaguaya.advisorapp.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * A StackedFrag is a fragment that is nested in a TabbedFrag. When it needs to navigate, it is able to communicate
 * with the parent fragment.
 */

public abstract class StackedFrag extends Fragment
{
    private static final String TAG = "StackedFrag";

    /**
     * Gets parent fragment (of type TabbedFrag) and then calls navigation function. Current
     * fragment gets placed in a stack.
     *
     * @param fragment fragment to navigate to
     */
    public void navigateTo(StackedFrag fragment)
    {
        if (getParentFragment() != null)
        {
            if (getParentFragment() instanceof TabbedFrag)
            {
                ((TabbedFrag) getParentFragment()).navigateNext(fragment);
            }
            else Log.e(TAG, "PARENT OF STACKED FRAG MUST BE TABBED FRAG");
        }
        else
        {
            Log.e(TAG, "Navigate called on StackFrag, but no parent found");
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if(!(getParentFragment() instanceof TabbedFrag))
        {
            throw new ClassCastException("The parent of a TabbedFrag must be a StackedFrag");
        }
    }
}
