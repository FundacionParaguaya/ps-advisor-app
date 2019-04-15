package org.fundacionparaguaya.adviserplatform.ui.settings;

import android.arch.lifecycle.ViewModel;

import org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager;
import org.fundacionparaguaya.adviserplatform.data.repositories.SnapshotRepository;

/**
 * Created by alex on 2/11/2018.
 */

public class SettingsViewModel extends ViewModel {
    private AuthenticationManager authManager;
    private SnapshotRepository snapshotRepository;

    public SettingsViewModel(AuthenticationManager authenticationManager, SnapshotRepository snapshotRepository){
        this.authManager = authenticationManager;
        this.snapshotRepository = snapshotRepository;
    }

    public AuthenticationManager getAuthManager(){
        return authManager;
    }

    public SnapshotRepository getSnapshotRepository() {
        return snapshotRepository;
    }
}
