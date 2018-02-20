package org.fundacionparaguaya.advisorapp.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.util.Log;

import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrMapper;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.LoginIr;
import org.fundacionparaguaya.advisorapp.jobs.CleanJob;
import org.fundacionparaguaya.advisorapp.jobs.SyncJob;
import org.fundacionparaguaya.advisorapp.models.Login;
import org.fundacionparaguaya.advisorapp.models.User;

import java.io.IOException;

import javax.inject.Singleton;

import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.AUTHENTICATED;
import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.PENDING;
import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.UNAUTHENTICATED;

/**
 * A manager for all things authentication related.
 */

@Singleton
public class AuthenticationManager {
    public static final String TAG = "AuthManager";
    static final String KEY_REFRESH_TOKEN = "refreshToken";

    private static final String AUTH_KEY = "Basic YmFyQ2xpZW50SWRQYXNzd29yZDpzZWNyZXQ=";

    public enum AuthenticationStatus {
        PENDING,
        UNAUTHENTICATED,
        AUTHENTICATED
    }

    private SharedPreferences mPreferences;
    private AuthenticationService mAuthService;
    private User mUser;
    private MutableLiveData<AuthenticationStatus> mStatus;
    private boolean online;

    public AuthenticationManager(AuthenticationService authService,
                                 SharedPreferences sharedPreferences,
                                 ConnectivityWatcher connectivityWatcher) {
        mAuthService = authService;
        mPreferences = sharedPreferences;

        connectivityWatcher.isOnline().observeForever((value) -> online = value);

        mStatus = new MutableLiveData<>();
        updateStatus(UNAUTHENTICATED);

    }

    public User getUser() {
        return mUser;
    }

    public LiveData<AuthenticationStatus> getStatus() {
        return mStatus;
    }

    /**
     * Attempts to login with the stored credentials, if any exist. This will update the status.
     */
    public void login() {
        String refreshToken = mPreferences.getString(KEY_REFRESH_TOKEN, null);
        if (refreshToken != null)
            refreshLogin(refreshToken);
    }

    /**
     * Attempts to login using the given credentials. This will update the status.
     */
    public void login(User user) {
        getToken(user);
    }

    public void logout() {
        clearRefreshToken();
        updateStatus(UNAUTHENTICATED);
        mUser = null;
    }

    /**
     * Attempts to refresh the login using a saved refresh token.
     */
    void refreshLogin(String refreshToken) {
        if (!online) {
            mUser = new User(new Login(refreshToken));
            updateStatus(AUTHENTICATED); // assume authenticated because there is refresh token
            return;
        }
        try {
            updateStatus(PENDING);
            retrofit2.Response<LoginIr> response = mAuthService
                        .loginWithRefreshToken(
                                AUTH_KEY,
                                refreshToken).execute();

            updateLogin(null, response);
        } catch (IOException e) {
            Log.e(TAG, "refreshToken: Could not refresh the token!", e);
            updateStatus(UNAUTHENTICATED);
        }
    }

    private void getToken(User user) {
        try {
            updateStatus(PENDING);
            retrofit2.Response<LoginIr> response = mAuthService
                    .loginWithPassword(
                            AUTH_KEY,
                            user.getUsername(),
                            user.getPassword()).execute();

            updateLogin(user, response);
        } catch (IOException e) {
            Log.e(TAG, "refreshToken: Could not retrieve a new token!", e);
            updateStatus(UNAUTHENTICATED);
        }
    }

    private void updateLogin(User user, retrofit2.Response<LoginIr> response) {
        if (response.isSuccessful()) {
            updateStatus(AUTHENTICATED);
            Login newLogin = IrMapper.mapLogin(response.body());
            if (user == null) {
                mUser = new User(newLogin);
            } else {
                mUser = user;
                mUser.setLogin(newLogin);
            }
            saveRefreshToken();
        } else {
            updateStatus(UNAUTHENTICATED);
            mUser = null;
        }
    }

    private void saveRefreshToken() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_REFRESH_TOKEN, mUser.getLogin().getRefreshToken());
        editor.apply();
    }

    private void clearRefreshToken() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(KEY_REFRESH_TOKEN);
        editor.apply();
    }

    private void updateStatus(AuthenticationStatus newStatus) {
        if (newStatus == mStatus.getValue())
            return;
        mStatus.postValue(newStatus);
        switch (newStatus) {
            case AUTHENTICATED:
                SyncJob.startPeriodic();
                break;
            case UNAUTHENTICATED:
                SyncJob.stopPeriodic();
                CleanJob.clean();
                break;
        }
    }
}
