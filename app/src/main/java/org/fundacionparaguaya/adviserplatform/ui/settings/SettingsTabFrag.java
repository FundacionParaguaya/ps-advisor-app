package org.fundacionparaguaya.adviserplatform.ui.settings;


import android.os.Bundle;
import android.support.annotation.Nullable;

import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.ui.base.AbstractStackedFrag;
import org.fundacionparaguaya.adviserplatform.ui.base.AbstractTabbedFrag;

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
