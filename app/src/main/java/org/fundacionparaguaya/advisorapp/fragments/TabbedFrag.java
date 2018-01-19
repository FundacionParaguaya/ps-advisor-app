package org.fundacionparaguaya.advisorapp.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import org.fundacionparaguaya.advisorapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a fragment that lives inside of a tab
 */

public abstract class TabbedFrag extends Fragment
{
    //for logging
    private static final String TAG = "TabbedFrag";

    //listeners to be notified when we need to have back navigation controls displayed (when we have more than
    //1 fragment in the back stack
    List<BackNavRequiredChangeHandler> mRequiresBackNavHandlers;

    View mContentView;

    //view id the fragment will be placed in
    private int mContainerId;

    //was back nav required the last time we navigated
    boolean mWasBackNavRequired;

    public TabbedFrag()
    {
        mRequiresBackNavHandlers = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getChildFragmentManager().addOnBackStackChangedListener(() ->
        {
            //if there was a change in whether or not we need back nav, notify our listeners
            if(mWasBackNavRequired!=isBackNavRequired())
            {
                mWasBackNavRequired = isBackNavRequired();

                notifyBackNavRequiredChange(isBackNavRequired());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //mContainerId = View.generateViewId(); //creates an id for findViewById

        mContainerId = 12312312;

        mContentView = new LinearLayout(getActivity());
        mContentView.setId(mContainerId);

        return mContentView;
    }

    /**
     * Sets the initial stacked frag for this tabbed frag
     * @param frag Fragment to set
     */
    public void setInitialFragment(StackedFrag frag) {
       makeFragmentTransaction(frag).commit();
    }

    /**
     * Navigate to a new fragment
     *
     * @param frag Fragment to navigate to
     */
    public void navigateNext(StackedFrag frag) {
        makeFragmentTransaction(frag).addToBackStack(null).commit();
    }

    /**
     * Muscle behind setInitialFragment and navigateNext
     * @param frag
     */
    private FragmentTransaction makeFragmentTransaction(StackedFrag frag) {

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(mContainerId, frag);

        return ft;
    }

    /**
     * Navigate backwards in the stack
     */
    public void onNavigateBack() {
        getChildFragmentManager().popBackStack();
    }

    /**
     * Adds a handler for changes in whether this fragment needs back navigation controls
     * @param handler Handler to Add
     */
    public void addBackNavRequiredHandler(BackNavRequiredChangeHandler handler) {
        this.mRequiresBackNavHandlers.add(handler);
    }

    public void removeBackNavRequiredHandler(BackNavRequiredChangeHandler handler) {
        this.mRequiresBackNavHandlers.remove(handler);
    }

    /**
     * Notifies listeners to hide or show the back navigation button
     *
     * @param required Whether or not to show back nav
     */
    protected void notifyBackNavRequiredChange(boolean required)
    {
        for(BackNavRequiredChangeHandler handler: mRequiresBackNavHandlers)
        {
            handler.handleBackNavChange(new BackNavRequiredChangeEvent(required));
        }
    }

    /**
     *
     * @return whether this fragment needs a button for back navigation
     */
    public boolean isBackNavRequired()
    {
        Log.d(TAG, "Back Stack Entry Count: " + getChildFragmentManager().getBackStackEntryCount());
        return getChildFragmentManager().getBackStackEntryCount() > 0;
    }

    public class BackNavRequiredChangeEvent
    {
        boolean mRequired;

        BackNavRequiredChangeEvent(boolean required)
        {
            mRequired = required;
        }

        public boolean isRequired()
        {
            return mRequired;
        }
    }

    public interface BackNavRequiredChangeHandler
    {
        void handleBackNavChange(BackNavRequiredChangeEvent e);
    }
}
