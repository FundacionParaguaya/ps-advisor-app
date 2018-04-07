package org.fundacionparaguaya.adviserplatform.ui.families;


import android.os.Bundle;
import android.support.annotation.Nullable;

import org.fundacionparaguaya.adviserplatform.R;
import org.fundacionparaguaya.adviserplatform.ui.base.AbstractStackedFrag;
import org.fundacionparaguaya.adviserplatform.ui.base.AbstractTabbedFrag;

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
