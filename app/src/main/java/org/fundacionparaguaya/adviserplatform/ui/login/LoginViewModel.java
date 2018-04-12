package org.fundacionparaguaya.adviserplatform.ui.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager;
import org.fundacionparaguaya.adviserplatform.data.remote.Server;
import org.fundacionparaguaya.adviserplatform.data.remote.ServerManager;

/**
 * The view model exposing data for the login page.
 */

public class LoginViewModel extends ViewModel {
    private ServerManager mServerManager;
    private AuthenticationManager mAuthManager;

    private String mUsername = "";
    private String mPassword = "";

    public LoginViewModel(ServerManager serverManager, AuthenticationManager authManager) {
        this.mServerManager = serverManager;
        this.mAuthManager = authManager;
    }

    void clearPassword()
    {
        mPassword = "";
    }

    public AuthenticationManager getAuthManager() {
        return mAuthManager;
    }

    public LiveData<AuthenticationManager.AuthenticationStatus> getAuthStatus() {
        return mAuthManager.status();
    }

    public LiveData<Server> SelectedServer() {
        return mServerManager.selected();
    }

    public String getSelectedServerHost() {
        if(mServerManager.getSelected()!=null)
        {
            return mServerManager.getSelected().getHost();
        }
        else return "";
    }


    public void setSelectedServer(Server server) {
        mServerManager.setSelected(server);
    }

    public Server[] getServers() {
        return mServerManager.getServers();
    }

    public String getUsername(){
        return mUsername;
    }

    public void setUsername(String username){
        mUsername = username;
    }

    public String getPassword(){
        return mPassword;
    }

    public void setPassword(String password){
        mPassword = password;
    }
}


