package org.fundacionparaguaya.advisorapp.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import org.fundacionparaguaya.advisorapp.BuildConfig;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrMapper;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.LoginIr;
import org.fundacionparaguaya.advisorapp.jobs.CleanJob;
import org.fundacionparaguaya.advisorapp.jobs.SyncJob;
import org.fundacionparaguaya.advisorapp.data.model.Login;
import org.fundacionparaguaya.advisorapp.data.model.User;

import java.io.IOException;

import javax.inject.Singleton;

import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.AUTHENTICATED;
import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.PENDING;
import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.UNAUTHENTICATED;
import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.UNKNOWN;

/**
 * A manager for all things authentication related.
 */

@Singleton
public class AuthenticationManager {
    public static final String TAG = "AuthManager";
    static final String KEY_REFRESH_TOKEN = "refreshToken";
    static final String KEY_USERNAME = "username";
    private static final String AUTH_KEY = "Basic " + BuildConfig.POVERTY_STOPLIGHT_API_KEY_STRING;

    public enum AuthenticationStatus {
        UNKNOWN,
        PENDING,
        UNAUTHENTICATED,
        AUTHENTICATED
    }

    private SharedPreferences mPreferences;
    private AuthenticationService mAuthService;
    private User mUser;
    private MutableLiveData<AuthenticationStatus> mStatus;
    private ConnectivityWatcher mConnectivityWatcher;

    public AuthenticationManager(AuthenticationService authService,
                                 SharedPreferences sharedPreferences,
                                 ConnectivityWatcher connectivityWatcher) {
        mAuthService = authService;
        mPreferences = sharedPreferences;
        mConnectivityWatcher = connectivityWatcher;

        mStatus = new MutableLiveData<>();
        mStatus.setValue(UNKNOWN);
    }

    public User getUser() {
        return mUser;
    }

    public String getAccessString() {
        if (mUser == null || mUser.getLogin() == null
                || mUser.getLogin().getAccessToken() == null) {
            return null;
        }
        return mUser.getLogin().getTokenType() + " " + mUser.getLogin().getAccessToken();
    }

    public LiveData<AuthenticationStatus> status() {
        return mStatus;
    }

    public AuthenticationStatus getStatus() {
        return mStatus.getValue();
    }

    /**
     * Attempts to login with the stored credentials, if any exist. This will update the status.
     */
    public AuthenticationStatus login() {
        String refreshToken = mPreferences.getString(KEY_REFRESH_TOKEN, null);
        String username = mPreferences.getString(KEY_USERNAME, null);
        if (refreshToken != null && username != null)
            return refreshLogin(refreshToken, username);
        else
            return updateStatus(UNAUTHENTICATED);
    }

    /**
     * Attempts to login using the given credentials. This will update the status.
     */
    public AuthenticationStatus login(User user) {
        return getToken(user);
    }

    public AuthenticationStatus logout() {
        clearLogin();
        mUser = null;
        return updateStatus(UNAUTHENTICATED);
    }

    /**
     * Attempts to refresh the login using a saved refresh token.
     */
    private AuthenticationStatus refreshLogin(String refreshToken, String username) {
        if (mConnectivityWatcher.isOffline()) {
            mUser = User.builder()
                    .username(username)
                    .login(new Login(refreshToken))
                    .build();
            return updateStatus(AUTHENTICATED);//assume authenticated because there is refresh token
        }
        try {
            updateStatus(PENDING);
            retrofit2.Response<LoginIr> response = mAuthService
                        .loginWithRefreshToken(
                                AUTH_KEY,
                                refreshToken).execute();

            return updateLogin(User.builder().username(username).build(), response);
        } catch (IOException e) {
            Log.e(TAG, "refreshToken: Could not refresh the token!", e);
            return updateStatus(UNAUTHENTICATED);
        }
    }

    private AuthenticationStatus getToken(User user) {
        try {
            updateStatus(PENDING);
            retrofit2.Response<LoginIr> response = mAuthService
                    .loginWithPassword(
                            AUTH_KEY,
                            user.getUsername(),
                            user.getPassword()).execute();

            return updateLogin(user, response);
        } catch (IOException e) {
            Log.e(TAG, "refreshToken: Could not retrieve a new token!", e);
            return updateStatus(UNAUTHENTICATED);
        }
    }

    private AuthenticationStatus updateLogin(@Nullable User user, retrofit2.Response<LoginIr> response) {
        if (response.isSuccessful()) {
            Login newLogin = IrMapper.mapLogin(response.body());
            if (user == null) {
                mUser = User.builder().login(newLogin).build();
            } else {
                mUser = user;
                mUser.setLogin(newLogin);
            }
            saveLogin();
            return updateStatus(AUTHENTICATED);
        } else {
            mUser = null;
            return updateStatus(UNAUTHENTICATED);
        }
    }

    private void saveLogin() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_REFRESH_TOKEN, mUser.getLogin().getRefreshToken());
        editor.putString(KEY_USERNAME, mUser.getUsername());
        editor.apply();
    }

    private void clearLogin() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(KEY_USERNAME);
        editor.apply();
    }

    private AuthenticationStatus updateStatus(AuthenticationStatus newStatus) {
        if (newStatus == mStatus.getValue())
            return newStatus;

        mStatus.postValue(newStatus);

        switch (newStatus) {
            case AUTHENTICATED:
                SyncJob.schedulePeriodic();
                SyncJob.sync();
                break;
            case UNAUTHENTICATED:
                SyncJob.stopPeriodic();
                CleanJob.clean();
                break;
        }
        return newStatus;
    }
}
