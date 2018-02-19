package org.fundacionparaguaya.advisorapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.fundacionparaguaya.advisorapp.R;

/**
 * Tab that will show all of the families the asesora is working with on a map
 */

public class MapTabFrag extends AbstractTabbedFrag {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTabTitle(getString(R.string.maptab_title));
    }

    @Override
    protected AbstractStackedFrag makeInitialFragment() {
        return UnderConstructionFragment.build(getResources().getString(R.string.maptab_title));
    }
}
