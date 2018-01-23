package org.fundacionparaguaya.advisorapp.activities;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.TabbedFrag;
import org.fundacionparaguaya.advisorapp.viewcomponents.DashboardTab;

/**
 * Extending this class makes it easier to switch between fragments within the activity's container,
 * without losing the fragment's state
 *
 * @author benhylak
 */

public abstract class AbstractFragSwitcherActivity extends AppCompatActivity {
    Fragment mLastFrag;

    private int mFragmentContainer;


    /**
     * Sets the container for the fragments and attaches/detatches so their state is tracked by fragment manager
     *
     * @param fragments Frags to add to fragment manager
     */
    public void initFragSwitcher(int resourceId, Fragment... fragments) {
        mFragmentContainer = resourceId;

        for (Fragment frag : fragments) {
            getSupportFragmentManager().beginTransaction().attach(frag).commit();
            getSupportFragmentManager().beginTransaction().detach(frag).commit();
        }
    }

    /**
     * Detatches the currently attached fragment and replaces it with the specific fragment
     *
     * @param frag Fragment to switch to
     */
    protected void switchToFrag(Fragment frag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (mLastFrag != null) {
            ft.detach(mLastFrag);
        }

        ft.attach(frag).replace(mFragmentContainer, frag).commit();

        mLastFrag = frag;
    }

    /**
     * Replaces the current fragment without saving the old fragment's state
     */
    protected void removeThenSwitchFrag(Fragment frag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(mLastFrag).commit();

        mLastFrag = null;

        switchToFrag(frag);
    }
}
