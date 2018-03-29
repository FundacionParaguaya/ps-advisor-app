package org.fundacionparaguaya.advisorapp.ui.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.fundacionparaguaya.advisorapp.R;

import java.util.ArrayList;

/**
 * Main side tab for dashboard
 */

public class DashboardTabBarView extends LinearLayout implements View.OnClickListener {

    private DashboardTab mFamilyTab;
    private DashboardTab mMapTab;
    private DashboardTab mSocialTab;
    private DashboardTab mSettingsTab;
    private ImageButton mBugButton;

    private DashboardTab mCurrentlySelected;

    private ArrayList <TabSelectedHandler> tabSelectedHandlers = new ArrayList<>();

    public DashboardTabBarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_main_tab_bar, this);

        mFamilyTab = findViewById(R.id.family_tab);
        mMapTab = findViewById(R.id.map_tab);
        mSocialTab = findViewById(R.id.social_tab);
        mSettingsTab = findViewById(R.id.settings_tab);
       // mBugButton = (ImageButton) findViewById(R.id.bug_button);

        mFamilyTab.initTab(DashboardTab.TabType.FAMILY, this);
        mMapTab.initTab(DashboardTab.TabType.MAP, this);
        mSocialTab.initTab(DashboardTab.TabType.SOCIAL, this);
        mSettingsTab.initTab(DashboardTab.TabType.SETTINGS, this);

       //mBugButton.setOnClickListener(tabSelectedListener);

        selectTab(DashboardTab.TabType.FAMILY);
    }

    public void selectTab(DashboardTab.TabType type)
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

                case SOCIAL:
                    mCurrentlySelected = mSocialTab;
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

    /**
     *
     * @return the currently selected tab
     */
    public DashboardTab.TabType getSelected()
    {
        return mCurrentlySelected.getTabType();
    }

    public void addTabSelectedHandler(TabSelectedHandler handler){
        tabSelectedHandlers.add(handler);
    }

    /**
     * Notify listeners that we have selected a tab
     *
     * @param tab The type of tab that was selected
     */
    private void notifyHandlers(DashboardTab.TabType tab){
        for(int counter=0; counter < tabSelectedHandlers.size(); counter++){
            tabSelectedHandlers.get(counter).onTabSelection(new TabSelectedEvent(tab));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.family_tab:
                selectTab(DashboardTab.TabType.FAMILY);
                break;

            case R.id.map_tab:
                selectTab(DashboardTab.TabType.MAP);
                break;

            case R.id.social_tab:
                selectTab(DashboardTab.TabType.SOCIAL);
                break;

            case R.id.settings_tab:
                selectTab(DashboardTab.TabType.SETTINGS);
                break;
        }
    }

    /**
     * A listener for tab selection events in this component
     */
    public interface TabSelectedHandler {
        void onTabSelection(TabSelectedEvent event);
    }

    public class TabSelectedEvent {
        private DashboardTab.TabType selectedTab;
        TabSelectedEvent(DashboardTab.TabType selectedTab){
            this.selectedTab = selectedTab;
        }
        public DashboardTab.TabType getSelectedTab() {
            return selectedTab;
        }
    }
}