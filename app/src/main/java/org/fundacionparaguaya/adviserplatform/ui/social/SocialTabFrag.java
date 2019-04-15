package org.fundacionparaguaya.adviserplatform.ui.social;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.ui.base.AbstractStackedFrag;
import org.fundacionparaguaya.adviserplatform.ui.base.AbstractTabbedFrag;
import org.fundacionparaguaya.adviserplatform.ui.common.UnderConstructionFragment;

/**
 * Tab that shows archived families, and eventually families that have been shared with them
 */

public class SocialTabFrag extends AbstractTabbedFrag
{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTabTitle(getString(R.string.socialtab_title));
    }

    @Override
    protected AbstractStackedFrag makeInitialFragment() {
        return UnderConstructionFragment.build(getResources().getString(R.string.socialtab_title));
    }
}
