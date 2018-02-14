package org.fundacionparaguaya.advisorapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import org.fundacionparaguaya.advisorapp.R;

/**
 * Tab for a family
 *
 */

public class FamilyTabbedFragment extends AbstractTabbedFrag
{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTabTitle(getString(R.string.familytab_title));
    }

    @Override
    protected AbstractStackedFrag makeInitialFragment() {
        return new AllFamiliesStackedFrag();
    }
}
