package org.fundacionparaguaya.advisorapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager;
import org.fundacionparaguaya.advisorapp.data.remote.Server;
import org.fundacionparaguaya.advisorapp.data.remote.ServerManager;

/**
 * The view model exposing data for the login page.
 */

public class LoginViewModel extends ViewModel {
    private ServerManager mServerManager;
    private AuthenticationManager mAuthManager;

    public LoginViewModel(ServerManager serverManager, AuthenticationManager authManager) {
        this.mServerManager = serverManager;
        this.mAuthManager = authManager;
    }

    public AuthenticationManager getAuthManager() {
        return mAuthManager;
    }

    public LiveData<AuthenticationManager.AuthenticationStatus> getAuthStatus() {
        return mAuthManager.getStatus();
    }

    public LiveData<Server> getSelectedServer() {
        return mServerManager.getSelected();
    }

    public void setSelectedServer(Server server) {
        mServerManager.setSelected(server);
    }

    public Server[] getServers() {
        return mServerManager.getServers();
    }
}


