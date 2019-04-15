package org.fundacionparaguaya.adviserplatform.ui.settings;

import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.fundacionparaguaya.adviserplatform.data.model.Snapshot;
import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.adviserplatform.ui.base.AbstractStackedFrag;
import org.fundacionparaguaya.adviserplatform.util.MixpanelHelper;
import org.fundacionparaguaya.assistantadvisor.AdviserAssistantApplication;
import org.fundacionparaguaya.assistantadvisor.R;

import java.util.List;

import javax.inject.Inject;

/**
 * Main settings page
 */

public class SettingsStackedFrag extends AbstractStackedFrag {

    private Button mLogout;
    private TextView mUsername;

    private TextView mReleaseNum;
    private Boolean queueSnapshots;

    protected @Inject
    InjectionViewModelFactory mViewModelFactory;
    private SettingsViewModel mSettingsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        ((AdviserAssistantApplication) getActivity().getApplication())
                .getApplicationComponent().inject(this);

        mSettingsViewModel = ViewModelProviders
                .of((FragmentActivity) getActivity(), mViewModelFactory)
                .get(SettingsViewModel.class);

        MixpanelHelper.FamilyOpened.openFamily(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settingsmain, container, false);
        mLogout = (Button) view.findViewById(R.id.settings_login_logout);
        mUsername = (TextView) view.findViewById(R.id.settings_login_username);

        mReleaseNum = view.findViewById(R.id.settings_releasenumber);
        String version = "";

        String username = mSettingsViewModel.getAuthManager().getUser().getUsername();

        if (username != null){
            mUsername.setText(username);
        } else {
            mUsername.setText(getText(R.string.settings_nousername));
        }

        try {
            version = getString(R.string.settings_releasenumber) + ": " +
                    getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;

        } catch (PackageManager.NameNotFoundException e){
            version = getString(R.string.settings_releasenumber_error);
        }
        mReleaseNum.setText(version);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        mLogout.setOnClickListener(v -> {
            if (!queueSnapshots) {
                makeExitDialog().show();
            } else {
                MixpanelHelper.LogoutEvent.logout(getContext());
                mSettingsViewModel.getAuthManager().logout();
            }
        });

    }

    private void noSnapshotsRemainingToSync() {
        AsyncTask.execute(() -> {
            List<Snapshot> snapshots = mSettingsViewModel.getSnapshotRepository().getQueueSnapshots();
            if(!snapshots.isEmpty()) {
                queueSnapshots = false;
            } else {
                queueSnapshots = true;
            }
        });
    }

    private SweetAlertDialog makeExitDialog() {
        SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.logout_title))
                .setContentText(getString(R.string.logout_description))
                .setConfirmText(getString(R.string.all_okay))
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        noSnapshotsRemainingToSync();
    }
}
