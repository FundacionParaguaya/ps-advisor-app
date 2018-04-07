package org.fundacionparaguaya.adviserplatform.ui.settings;

import android.arch.lifecycle.ViewModel;

import org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager;

/**
 * Created by alex on 2/11/2018.
 */

public class SettingsViewModel extends ViewModel {
    private AuthenticationManager authManager;

    public SettingsViewModel(AuthenticationManager authenticationManager){
        this.authManager = authenticationManager;
    }

    public AuthenticationManager getAuthManager(){
        return authManager;
    }

}
