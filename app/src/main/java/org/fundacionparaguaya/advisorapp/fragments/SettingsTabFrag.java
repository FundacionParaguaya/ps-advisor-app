package org.fundacionparaguaya.advisorapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import org.fundacionparaguaya.advisorapp.R;

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
