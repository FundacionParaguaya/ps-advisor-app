package org.fundacionparaguaya.advisorapp.data.remote;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrMapper;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.LoginIr;
import org.fundacionparaguaya.advisorapp.jobs.CleanJob;
import org.fundacionparaguaya.advisorapp.jobs.SyncJob;
import org.fundacionparaguaya.advisorapp.models.Login;
import org.fundacionparaguaya.advisorapp.models.User;

import java.io.IOException;

import javax.inject.Singleton;

import static android.content.Context.MODE_PRIVATE;
import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.AUTHENTICATED;
import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.UNAUTHENTICATED;
import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.UNKNOWN;

/**
 * A manager for all things authentication related.
 */

@Singleton
public class AuthenticationManager {
    public static final String TAG = "AuthManager";
    private static final String PREFS_AUTHENTICATION = "auth";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";

    private static final String AUTH_KEY = "Basic YmFyQ2xpZW50SWRQYXNzd29yZDpzZWNyZXQ=";

    public enum AuthenticationStatus {
        PENDING,
        UNAUTHENTICATED,
        AUTHENTICATED,
        UNKNOWN
    }

    private SharedPreferences mPreferences;
    private AuthenticationService mAuthService;
    private User mUser;
    private MutableLiveData<AuthenticationStatus> mStatus;

    public AuthenticationManager(Application application, AuthenticationService authService) {
        mPreferences = application.getApplicationContext()
                .getSharedPreferences(PREFS_AUTHENTICATION, MODE_PRIVATE);
        mAuthService = authService;
        mUser = new User(null);

        mStatus = new MutableLiveData<>();
        String refreshToken = mPreferences.getString(KEY_REFRESH_TOKEN, null);
        if (refreshToken != null) {
            mUser.setLogin(new Login(refreshToken));
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    refreshLogin();
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            updateStatus(UNAUTHENTICATED);
        }

    }

    public User getUser() {
        return mUser;
    }

    public LiveData<AuthenticationStatus> getStatus() {
        return mStatus;
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
    public void refreshLogin() {
        try {
            retrofit2.Response<LoginIr> response = mAuthService
                        .loginWithRefreshToken(
                                AUTH_KEY,
                                mUser.getLogin().getRefreshToken()).execute();

            updateLogin(mUser, response);
        } catch (IOException e) {
            Log.e(TAG, "refreshToken: Could not refresh the token!", e);
            updateStatus(AUTHENTICATED); // assume authenticated because there is refresh token
        }
    }

    private void getToken(User user) {
        try {
            retrofit2.Response<LoginIr> response = mAuthService
                    .loginWithPassword(
                            AUTH_KEY,
                            user.getUsername(),
                            user.getPassword()).execute();

            updateLogin(user, response);
        } catch (IOException e) {
            Log.e(TAG, "refreshToken: Could not retrieve a new token!", e);
            updateStatus(UNKNOWN);
        }
    }

    private void updateLogin(User user, retrofit2.Response<LoginIr> response) {
        if (response.isSuccessful()) {
            updateStatus(AUTHENTICATED);
            Login newLogin = IrMapper.mapLogin(response.body());
            mUser = user;
            mUser.setLogin(newLogin);
            saveRefreshToken();
        } else {
            updateStatus(UNAUTHENTICATED);
            mUser.setLogin(null);
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
