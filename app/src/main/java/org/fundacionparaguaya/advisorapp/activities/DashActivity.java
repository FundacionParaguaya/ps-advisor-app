package org.fundacionparaguaya.advisorapp.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.novoda.merlin.Merlin;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager;
import org.fundacionparaguaya.advisorapp.fragments.AbstractTabbedFrag;
import org.fundacionparaguaya.advisorapp.fragments.SocialTabFrag;
import org.fundacionparaguaya.advisorapp.fragments.FamilyTabbedFragment;
import org.fundacionparaguaya.advisorapp.fragments.MapTabFrag;
import org.fundacionparaguaya.advisorapp.fragments.SettingsTabFrag;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.DisplayBackNavListener;
import org.fundacionparaguaya.advisorapp.jobs.SyncJob;
import org.fundacionparaguaya.advisorapp.repositories.SyncManager;
import org.fundacionparaguaya.advisorapp.util.ScreenCalculations;
import org.fundacionparaguaya.advisorapp.viewcomponents.DashboardTab;
import org.fundacionparaguaya.advisorapp.viewcomponents.DashboardTabBarView;

import javax.inject.Inject;

import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.UNAUTHENTICATED;
import static org.fundacionparaguaya.advisorapp.repositories.SyncManager.SyncState.ERROR_NO_INTERNET;
import static org.fundacionparaguaya.advisorapp.repositories.SyncManager.SyncState.SYNCING;

public class DashActivity extends AbstractFragSwitcherActivity implements DisplayBackNavListener
{
    DashboardTabBarView tabBarView;
    TextView mSyncLabel;
    LinearLayout mSyncArea;
    ImageView mSyncButtonIcon;
    RelativeTimeTextView mLastSyncTextView;
    TextView mTvTabTitle;
    TextView mTvBackLabel;

    LinearLayout mBackButton;

    @Inject
    SyncManager mSyncManager;
    @Inject
    AuthenticationManager mAuthManager;
    @Inject
    Merlin mNetworkWatcher;

    ObjectAnimator mSyncRotateAnimation;

    static String SELECTED_TAB_KEY = "SELECTED_TAB";


    //if display back button = false
    ///display title if it has a title

    //if display back button = true
    //display title, if it has a title
    //else display "Back"

    @Override
    public void onBackPressed() {
        ((AbstractTabbedFrag) getFragment(getClassForType(tabBarView.getSelected()))).onNavigateBack();
    }

    private Class getClassForType(DashboardTab.TabType type) {
        switch (type) {
            case FAMILY:
                return FamilyTabbedFragment.class;
            case MAP:
                return MapTabFrag.class;
            case SOCIAL:
                return SocialTabFrag.class;
            case SETTINGS:
                return SettingsTabFrag.class;
        }

        return null;
    }

    private DashboardTabBarView.TabSelectedHandler handler = (event) ->
    {
        switchToFrag(getClassForType(event.getSelectedTab()));
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //save selected tab
        outState.putString(SELECTED_TAB_KEY, tabBarView.getSelected().name());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void switchToFrag(Class fragmentClass) {
        super.switchToFrag(fragmentClass);

        if(mBackButton!=null) {
            mBackButton.setVisibility(View.GONE);
        }
        if(mTvTabTitle!=null) {
            mTvTabTitle.setVisibility(View.INVISIBLE);
            String title = ((AbstractTabbedFrag) getFragment(fragmentClass)).getTabTitle();
            mTvTabTitle.setText(title);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdvisorApplication) this.getApplication())
                .getApplicationComponent()
                .inject(this);

        setContentView(R.layout.activity_main);

        setFragmentContainer(R.id.dash_content);

        tabBarView = (DashboardTabBarView) findViewById(R.id.dashboardTabView);

        mSyncLabel = findViewById(R.id.topbar_synclabel);
        mLastSyncTextView = findViewById(R.id.last_sync_textview);

        mTvTabTitle = findViewById(R.id.tv_topbar_tabtitle);
        mTvBackLabel = findViewById(R.id.tv_topbar_backlabel);

        mSyncArea = (LinearLayout) findViewById(R.id.linearLayout_topbar_syncbutton);
        mSyncArea.setOnClickListener(this::onSyncButtonPress);

        mSyncButtonIcon = findViewById(R.id.iv_topbar_syncimage);

        //update last sync label when the sync manager updates
        mSyncManager.getProgress().observe(this, (value) -> {
            if (value != null) {
                if (value.getLastSyncedTime() == -1) {
                    mLastSyncTextView.setText(R.string.topbar_lastsync_never);
                } else {
                    mLastSyncTextView.setReferenceTime(value.getLastSyncedTime());
                }

                if (value.getSyncState() == SYNCING) {
                    mSyncLabel.setText(R.string.topbar_synclabel_syncing);
                    mSyncArea.setEnabled(false);
                    mSyncButtonIcon.setImageResource(R.drawable.ic_dashtopbar_sync);
                    mSyncButtonIcon.setBackgroundResource(R.drawable.dashtopbar_synccircle);

                    mSyncButtonIcon.startAnimation(
                            AnimationUtils.loadAnimation(this, R.anim.spin_forever));

                } else if (value.getSyncState() == ERROR_NO_INTERNET) {
                    mSyncLabel.setText(R.string.topbar_synclabel_offline);
                    mSyncArea.setEnabled(false);
                    mSyncButtonIcon.clearAnimation();
                    mSyncButtonIcon.setImageResource(R.drawable.ic_dashtopbar_offline);
                    mSyncButtonIcon.setBackgroundResource(android.R.color.transparent);

                } else {
                    mSyncArea.setEnabled(true);
                    mSyncButtonIcon.clearAnimation();
                    mSyncButtonIcon.setImageResource(R.drawable.ic_dashtopbar_sync);
                    mSyncButtonIcon.setBackgroundResource(R.drawable.dashtopbar_synccircle);
                    mSyncLabel.setText(R.string.topbar_synclabel);
                }
            }
        });

        mAuthManager.getStatus().observe(this, (value) -> {
            if (value == UNAUTHENTICATED) {
                Intent login = new Intent(this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });

        if (savedInstanceState != null) {
            String selectTypeName = savedInstanceState.getString(SELECTED_TAB_KEY);

            if (selectTypeName != null) {
                DashboardTab.TabType previouslySelected = DashboardTab.TabType.valueOf(selectTypeName);
                tabBarView.selectTab(previouslySelected);
                switchToFrag(getClassForType(previouslySelected));
            }
        }
        else
        {
            switchToFrag(FamilyTabbedFragment.class);
        }

        mBackButton = findViewById(R.id.linearlayout_dashactivity_back);
        mBackButton.setVisibility(View.GONE);
        mBackButton.setOnClickListener((event)-> onBackPressed());

        tabBarView.addTabSelectedHandler(handler);

        ImageView fpLogo = findViewById(R.id.fp_logo);

        if(ScreenCalculations.is7InchTablet(getApplicationContext()) && fpLogo !=null)
        {
            fpLogo.setVisibility(View.GONE);
        }
    }

    private void onSyncButtonPress(View view) {
        SyncJob.sync();
    }


    @Override
    public void onShowBackNav() {
       mBackButton.setVisibility(View.VISIBLE);
       mTvBackLabel.setText(mTvTabTitle.getText());
       mTvTabTitle.setVisibility(View.GONE);
    }

    @Override
    public void onHideBackNav() {
        mBackButton.setVisibility(View.GONE);
        mTvTabTitle.setVisibility(View.VISIBLE);
    }
}

