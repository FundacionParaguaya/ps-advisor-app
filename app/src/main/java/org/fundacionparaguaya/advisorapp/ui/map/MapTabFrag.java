package org.fundacionparaguaya.advisorapp.ui.map;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.ui.base.AbstractStackedFrag;
import org.fundacionparaguaya.advisorapp.ui.base.AbstractTabbedFrag;
import org.fundacionparaguaya.advisorapp.ui.common.UnderConstructionFragment;

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
