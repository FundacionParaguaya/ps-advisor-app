package org.fundacionparaguaya.advisorapp.viewcomponents;

import android.app.ActionBar;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import org.fundacionparaguaya.advisorapp.viewcomponents.DashboardTab.TabType;
import org.fundacionparaguaya.advisorapp.R;

import java.util.ArrayList;

/**
 * Main side tab for dashboard
 */


public class DashboardTabBarView extends LinearLayout {
    private DashboardTab mFamilyTab;
    private DashboardTab mMapTab;
    private DashboardTab mArchiveTab;
    private DashboardTab mSettingsTab;
    private ImageButton mBugButton;


    private DashboardTab mCurrentlySelected;

    private ArrayList <TabSelectedHandler> tabSelectedHandlers = new ArrayList();

    public DashboardTabBarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.dashboardtabview, this);

        mFamilyTab = (DashboardTab) findViewById(R.id.family_tab);
        mMapTab = (DashboardTab) findViewById(R.id.map_tab);
        mArchiveTab = (DashboardTab) findViewById(R.id.archive_tab);
        mSettingsTab = (DashboardTab) findViewById(R.id.settings_tab);
        mBugButton = (ImageButton) findViewById(R.id.bug_button);

        mFamilyTab.setOnClickListener(clickListener);
        mFamilyTab.setTabType(TabType.FAMILY);

        mMapTab.setOnClickListener(clickListener);
        mMapTab.setTabType(TabType.MAP);

        mArchiveTab.setOnClickListener(clickListener);
        mArchiveTab.setTabType(TabType.ARCHIVE);

        mSettingsTab.setOnClickListener(clickListener);
        mSettingsTab.setTabType(TabType.SETTINGS);

        mBugButton.setOnClickListener(clickListener);

        selectTab(TabType.FAMILY);
    }

    public void selectTab(TabType type)
    {
        if(mCurrentlySelected==null || mCurrentlySelected.getTabType() != type)
        {
            DashboardTab lastSelected = mCurrentlySelected;

            switch (type)
            {
                case FAMILY:
                    mCurrentlySelected = mFamilyTab;
                    break;

                case MAP:
                    mCurrentlySelected = mMapTab;
                    break;

                case ARCHIVE:
                    mCurrentlySelected = mArchiveTab;
                    break;

                case SETTINGS:
                    mCurrentlySelected = mSettingsTab;
                    break;
            }

            mCurrentlySelected.setSelected(true);

            //if there used to be a tab selected, reset that one to not selected and notify of selection event.
            if(lastSelected != null) {

                notifyHandlers(type);
                lastSelected.setSelected(false);
            }
        }

    }

    public TabType getSelected()
    {
        return mCurrentlySelected.getTabType();
    }

    public void addTabSelectedHandle(TabSelectedHandler handler){
        tabSelectedHandlers.add(handler);
    }

    private void notifyHandlers(TabType tab){
        for(int counter=0; counter < tabSelectedHandlers.size(); counter++){
            tabSelectedHandlers.get(counter).onTabSelection(new TabSelectedEvent(tab));
        }
    }

    final OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()){
                case R.id.family_tab:
                    selectTab(TabType.FAMILY);

                    break;
                case R.id.map_tab:
                    selectTab(TabType.MAP);

                    break;
                case R.id.archive_tab:
                    selectTab(TabType.ARCHIVE);

                    break;
                case R.id.settings_tab:
                    selectTab(TabType.SETTINGS);

                    break;
                case R.id.bug_button:
                    break;
            }
        }
    };

    public interface TabSelectedHandler {
        void onTabSelection(TabSelectedEvent event);
    }

    public class TabSelectedEvent {
        private TabType selectedTab;
        TabSelectedEvent(TabType selectedTab){
            this.selectedTab = selectedTab;
        }
        public TabType getSelectedTab() {
            return selectedTab;
        }
    }
}