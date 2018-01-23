package org.fundacionparaguaya.advisorapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.ExampleTabbedFragment;
import org.fundacionparaguaya.advisorapp.fragments.FamilyTabbedFragment;
import org.fundacionparaguaya.advisorapp.fragments.TabbedFrag;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.DisplayBackNavListener;
import org.fundacionparaguaya.advisorapp.viewcomponents.DashboardTab;
import org.fundacionparaguaya.advisorapp.viewcomponents.DashboardTabBarView;

public class DashActivity extends AbstractFragSwitcherActivity implements DisplayBackNavListener {
    DashboardTabBarView tabBarView;

    TabbedFrag mFamiliesFrag;
    TabbedFrag mMapFrag;
    TabbedFrag mArchiveFrag;
    TabbedFrag mSettingsFrag;

    @Override
    public void onBackPressed() {
        getFragForType(tabBarView.getSelected()).onNavigateBack();
    }

    private TabbedFrag getFragForType(DashboardTab.TabType type) {
        switch (type) {
            case FAMILY:
                return mFamiliesFrag;
            case MAP:
                return mMapFrag;
            case ARCHIVE:
                return mArchiveFrag;
            case SETTINGS:
                return mSettingsFrag;
        }

        return null;
    }

    private DashboardTabBarView.TabSelectedHandler handler = (event) ->
    {
        switchToFrag(getFragForType(event.getSelectedTab()));

        Toast.makeText(getApplicationContext(), event.getSelectedTab().name(), Toast.LENGTH_SHORT).show();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabBarView = (DashboardTabBarView) findViewById(R.id.dashboardTabView);
        tabBarView.addTabSelectedHandler(handler);

        /**
         * Create fragment for each tab
         */
        mFamiliesFrag = new FamilyTabbedFragment();
        mMapFrag = new ExampleTabbedFragment();
        mArchiveFrag = new ExampleTabbedFragment();
        mSettingsFrag = new ExampleTabbedFragment();

        initFragSwitcher(R.id.dash_content, mFamiliesFrag, mMapFrag);

        switchToFrag(mFamiliesFrag);

        //TODO: right now, hitting "back" will circumvent the login screen.
        // also, this should be called before everything in this function, and check if already authenticated.
        //maybe we could have a separate main activity that accomplishes this all

        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }


    @Override
    public void onShowBackNav() {
        Toast.makeText(getApplicationContext(), "Show Back Nav", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHideBackNav() {
        Toast.makeText(getApplicationContext(), "Hide Back Nav", Toast.LENGTH_SHORT).show();
    }
}
