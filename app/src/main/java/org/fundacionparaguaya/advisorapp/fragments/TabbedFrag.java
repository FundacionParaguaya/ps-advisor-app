package org.fundacionparaguaya.advisorapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.NavigationListener;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.DisplayBackNavListener;

import java.util.ArrayList;

/**
 * This is a fragment that lives inside of a tab
 */

public abstract class TabbedFrag extends Fragment implements NavigationListener {
    //for logging
    private static final String TAG = "TabbedFrag";

    private DisplayBackNavListener mDisplayBackNavListener;

    //view id the fragment will be placed in
    private int mContainerId;

    //was back nav required the last time we navigated
    boolean mWasBackNavRequired;

    public TabbedFrag() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getChildFragmentManager().addOnBackStackChangedListener(() ->
        {
            //if there was a change in whether or not we need back nav, notify our listeners
            if (mWasBackNavRequired != isBackNavRequired()) {
                //update our local variable to reflect the change
                mWasBackNavRequired = isBackNavRequired();

                if (mWasBackNavRequired) //if required
                {
                    mDisplayBackNavListener.onShowBackNav();
                }
                else //if not required
                {
                    mDisplayBackNavListener.onHideBackNav();
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tabbed, container, false);

        mContainerId = R.id.fragment_container;
        return view;
    }

    /**
     * Sets the initial stacked frag for this tabbed frag
     *
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
    public void onNavigateNext(StackedFrag frag) {
        makeFragmentTransaction(frag).addToBackStack(null).commit();
    }

    /**
     * Muscle behind setInitialFragment and onNavigateNext
     *
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
     * @return whether this fragment needs a button for back navigation
     */
    public boolean isBackNavRequired() {
        Log.d(TAG, "Back Stack Entry Count: " + getChildFragmentManager().getBackStackEntryCount());
        return getChildFragmentManager().getBackStackEntryCount() > 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            //if this is a nested fragment
            if (getParentFragment() != null) {
                mDisplayBackNavListener = (DisplayBackNavListener) getParentFragment();
            }
            else {
                //nested inside of an activity
                mDisplayBackNavListener = (DisplayBackNavListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent activity or fragment must implement ShowBackNavCallback");
        }
    }
}
