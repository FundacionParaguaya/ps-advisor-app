package org.fundacionparaguaya.advisorapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import org.fundacionparaguaya.advisorapp.R;

/**
 * Tab that shows archived families, and eventually families that have been shared with them
 */

public class ArchiveTabFrag extends AbstractTabbedFrag
{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTabTitle(getString(R.string.archivetab_title));
    }

    @Override
    protected AbstractStackedFrag makeInitialFragment() {
        return UnderConstructionFragment.build(getResources().getString(R.string.archivetab_title));
    }
}
