package org.fundacionparaguaya.adviserplatform.ui.dashboard;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.fundacionparaguaya.adviserplatform.data.model.Snapshot;
import org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager;
import org.fundacionparaguaya.adviserplatform.data.repositories.SyncManager;
import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.adviserplatform.jobs.SyncJob;
import org.fundacionparaguaya.adviserplatform.ui.base.AbstractTabbedFrag;
import org.fundacionparaguaya.adviserplatform.ui.base.DisplayBackNavListener;
import org.fundacionparaguaya.adviserplatform.ui.common.AbstractFragSwitcherActivity;
import org.fundacionparaguaya.adviserplatform.ui.common.widget.NonScrollView;
import org.fundacionparaguaya.adviserplatform.ui.families.FamilyTabbedFragment;
import org.fundacionparaguaya.adviserplatform.ui.login.LoginActivity;
import org.fundacionparaguaya.adviserplatform.ui.map.MapTabFrag;
import org.fundacionparaguaya.adviserplatform.ui.settings.SettingsTabFrag;
import org.fundacionparaguaya.adviserplatform.ui.social.SocialTabFrag;
import org.fundacionparaguaya.adviserplatform.util.AppConstants;
import org.fundacionparaguaya.adviserplatform.util.MixpanelHelper;
import org.fundacionparaguaya.assistantadvisor.AdviserAssistantApplication;
import org.fundacionparaguaya.assistantadvisor.BuildConfig;
import org.fundacionparaguaya.assistantadvisor.R;

import java.util.List;

import javax.inject.Inject;

import static org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager.AuthenticationStatus.UNAUTHENTICATED;
import static org.fundacionparaguaya.adviserplatform.data.repositories.SyncManager.SyncState.ERROR_NO_INTERNET;
import static org.fundacionparaguaya.adviserplatform.data.repositories.SyncManager.SyncState.ERROR_OTHER;
import static org.fundacionparaguaya.adviserplatform.data.repositories.SyncManager.SyncState.SYNCED;
import static org.fundacionparaguaya.adviserplatform.data.repositories.SyncManager.SyncState.SYNCING;

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
    protected @Inject
    InjectionViewModelFactory mViewModelFactory;
    private DashActivityViewModel mDashActivityModel;

    static String SELECTED_TAB_KEY = "SELECTED_TAB";
    private static int queueSnapshots = 0;

    @Override
    public void onBackPressed() {
        ((AbstractTabbedFrag) getFragment(getClassForType(tabBarView.getSelected()))).onNavigateBack();
    }

    public static int getSnapshotQueue() {
        return queueSnapshots;
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
    protected void switchToFrag(Class<? extends Fragment> fragmentClass) {
        super.switchToFrag(fragmentClass);

        AbstractTabbedFrag frag = (AbstractTabbedFrag)getFragment(fragmentClass);

        if(!frag.isBackNavRequired()) mTvTabTitle.setText(frag.getTabTitle());

        TransitionManager.beginDelayedTransition(findViewById(R.id.dashboardtopbar));
        mBackButton.setVisibility(frag.isBackNavRequired()? View.VISIBLE: View.GONE);
        mTvTabTitle.setVisibility(frag.isBackNavRequired()? View.GONE: View.VISIBLE);

        snapshotsRemainingToSync();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdviserAssistantApplication) this.getApplication())
                .getApplicationComponent()
                .inject(this);

        mDashActivityModel = ViewModelProviders
                .of(this, mViewModelFactory)
                .get(DashActivityViewModel.class);

        setContentView(R.layout.activity_main);
        setFragmentContainer(R.id.dash_content);

        tabBarView = findViewById(R.id.dashboardTabView);
        mSyncLabel = findViewById(R.id.topbar_synclabel);
        mLastSyncTextView = findViewById(R.id.last_sync_textview);
        mTvTabTitle = findViewById(R.id.tv_topbar_tabtitle);
        mTvBackLabel = findViewById(R.id.tv_topbar_backlabel);
        mSyncArea = findViewById(R.id.linearLayout_topbar_syncbutton);
        if(BuildConfig.DEBUG) {
            //Manual synchronization temporally disabled except for DEBUG mode
            mSyncArea.setOnClickListener(this::onSyncButtonPress);
        }
        mSyncButtonIcon = findViewById(R.id.iv_topbar_syncimage);
        mBackButton = findViewById(R.id.linearlayout_dashactivity_back);
        mBackButton.setVisibility(View.GONE);
        mBackButton.setOnClickListener((event)-> onBackPressed());

        tabBarView.addTabSelectedHandler(handler);

        NonScrollView rootView = findViewById(R.id.scroll_main_activity);
        rootView.setScrollingEnabled(true);
        ViewCompat.setNestedScrollingEnabled(rootView, false);
        mSyncManager.setDashActivity(this);

        //update last sync label when the sync manager updates
        mSyncManager.getProgress().observe(this, (value) -> {

            if (value != null) {
                if (value.getLastSyncedTime() == -1) {
                    mLastSyncTextView.setText(R.string.topbar_lastsync_never);
                } else {
                    mLastSyncTextView.setReferenceTime(value.getLastSyncedTime());
                    setSyncStatus(true, R.drawable.ic_dashtopbar_sync,
                            R.drawable.dashtopbar_synccircle);
                }

                //SyncJob.isSyncAboutToStart() lets us know if there are any sync jobs that are about to start
                //this will happen if the app is force quit/crashes. Job's don't start immediately -- system decides
                //when they run. So, the sync job will be created but it'll be a few seconds before the sync actually
                //happens. In this case, we don't want the user to be able to start a new sync job and we want to let
                //them know we are doing our best :)
                //Sodep: If SYNCED already, do not sync again
                if (!SYNCED.equals(value.getSyncState()) &&
                        (SyncJob.isSyncAboutToStart() || value.getSyncState() == SYNCING)) {
                    mSyncLabel.setText(R.string.topbar_synclabel_syncing);
                    mSyncArea.setEnabled(false);
                    mSyncButtonIcon.setImageResource(R.drawable.ic_dashtopbar_sync);
                    mSyncButtonIcon.setBackgroundResource(R.drawable.dashtopbar_synccircle);

                    mSyncButtonIcon.startAnimation(
                            AnimationUtils.loadAnimation(this, R.anim.spin_forever));

                } else if (value.getSyncState() == ERROR_NO_INTERNET) {
                    mSyncLabel.setText(R.string.topbar_synclabel_offline);
                    setSyncStatus(false, R.drawable.ic_dashtopbar_offline, android.R.color.transparent);

                } else if(value.getSyncState() == ERROR_OTHER) {
                    setSyncStatus(true, R.drawable.ic_warning, 0);
                    mSyncLabel.setText(R.string.topbar_synclabel);
                }
                else
                {
                    setSyncStatus(true, R.drawable.ic_dashtopbar_sync,
                            R.drawable.dashtopbar_synccircle);
                    mSyncLabel.setText(R.string.topbar_synclabel);
                    queueSnapshots = 0;
                }
            }
        });

        mAuthManager.status().observe(this, (value) -> {
            if (value == UNAUTHENTICATED) {
                showLogin();
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
    }

    private void setSyncStatus(boolean b, int ic_dashtopbar_sync, int dashtopbar_synccircle) {
        mSyncArea.setEnabled(b);
        mSyncButtonIcon.clearAnimation();
        mSyncButtonIcon.setImageResource(ic_dashtopbar_sync);
        mSyncButtonIcon.setBackgroundResource(dashtopbar_synccircle);
    }

    public void showLogin() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();
    }

    private void onSyncButtonPress(View view) {
        MixpanelHelper.SyncEvents.syncButtonPressed(this);

        if(!SyncJob.isSyncAboutToStart())
        {
            SyncJob.sync();
        }
    }

    @Override
    public void onShowBackNav() {
        TransitionManager.beginDelayedTransition(findViewById(R.id.dashboardtopbar));
        mBackButton.setVisibility(View.VISIBLE);
        mTvTabTitle.setVisibility(View.GONE);
    }

    @Override
    public void onHideBackNav() {
        TransitionManager.beginDelayedTransition(findViewById(R.id.dashboardtopbar));
        mBackButton.setVisibility(View.GONE);
        mTvTabTitle.setVisibility(View.VISIBLE);
        mTvTabTitle.setText(((AbstractTabbedFrag)getSelectedFragment()).getTabTitle());
    }

    public void setSyncLabel(Integer id, final long value, final long total) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mSyncLabel.setText(getString(id, value, total));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        snapshotsRemainingToSync();
    }

    private void snapshotsRemainingToSync() {
        AsyncTask.execute(() -> {
            List<Snapshot> snapshots = mDashActivityModel.getSnapshotRepository().getQueueSnapshots();
            queueSnapshots = snapshots.size();

            runOnUiThread(() -> {
                validateStorageCapacity();
            });
        });
    }

    private void validateStorageCapacity() {
        /**
         * Depending on the amount of snapshots that haven't already been sync,
         * a message will be display letting the user know how much have he reach
         * this could be a toast or a popup*/
        String message;
        if(queueSnapshots >= AppConstants.MEDIUM_CAPACITY && queueSnapshots < AppConstants.HIGH_CAPACITY) {
            message = getString(R.string.snapshots_limit_medium, (queueSnapshots*100)/AppConstants.MAXIMUM_CAPACITY);
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();

        } else if (queueSnapshots >= AppConstants.HIGH_CAPACITY && queueSnapshots < AppConstants.MAXIMUM_CAPACITY) {
            message = getString(R.string.snapshots_limit_high,(queueSnapshots*100)/AppConstants.MAXIMUM_CAPACITY);
            makeLimitDialog(message).show();

        } else if (queueSnapshots >= AppConstants.MAXIMUM_CAPACITY) {
            message = getString(R.string.snapshot_limit_reach);
            makeLimitDialog(message).show();

            mSyncLabel.setText(getString(R.string.sync_require));
            setSyncStatus(true, R.drawable.ic_warning, 0);
        }

    }

    private SweetAlertDialog makeLimitDialog(String message) {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.attention_title))
                .setContentText(message)
                .setConfirmText(getString(R.string.all_okay))
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
        return dialog;
    }
}

