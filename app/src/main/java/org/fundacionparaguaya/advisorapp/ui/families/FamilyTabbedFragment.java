package org.fundacionparaguaya.advisorapp.ui.families;


import android.os.Bundle;
import android.support.annotation.Nullable;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.ui.base.AbstractStackedFrag;
import org.fundacionparaguaya.advisorapp.ui.base.AbstractTabbedFrag;
import org.fundacionparaguaya.advisorapp.ui.families.AllFamiliesFragment;

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
        return new AllFamiliesFragment();
    }
}
