package org.fundacionparaguaya.advisorapp.ui.settings;


import android.os.Bundle;
import android.support.annotation.Nullable;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.ui.base.AbstractStackedFrag;
import org.fundacionparaguaya.advisorapp.ui.base.AbstractTabbedFrag;

/**
 * Fragment for settings tab
 */

public class SettingsTabFrag extends AbstractTabbedFrag
{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTabTitle(getString(R.string.settingstab_title));
    }

    @Override
    protected AbstractStackedFrag makeInitialFragment() {
        return new SettingsStackedFrag();
    }
}
