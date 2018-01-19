package org.fundacionparaguaya.advisorapp.activities;

import android.app.Activity;
import org.fundacionparaguaya.advisorapp.fragments.TabbedFrag;

/**
 * Created by benhylak on 1/14/18.
 */

public class DashActivity extends Activity
{
    TabbedFrag mFamilyFrag;
    TabbedFrag mMapFrag;
    TabbedFrag mArchiveFrag;
    TabbedFrag mSettingsFrag;

    @Override
    public void onBackPressed()
    {

    }
}
