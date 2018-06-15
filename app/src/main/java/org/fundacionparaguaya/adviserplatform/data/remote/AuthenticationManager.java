package org.fundacionparaguaya.adviserplatform.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.fundacionparaguaya.adviserplatform.BuildConfig;
import org.fundacionparaguaya.adviserplatform.data.model.Login;
import org.fundacionparaguaya.adviserplatform.data.model.User;
import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.IrMapper;
import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.LoginIr;
import org.fundacionparaguaya.adviserplatform.util.AppConstants;
import org.fundacionparaguaya.adviserplatform.util.SecurityUtils;

import java.io.IOException;
import java.util.Date;

import javax.inject.Singleton;

import static android.accounts.AccountManager.KEY_PASSWORD;
import static org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager.AuthenticationStatus.AUTHENTICATED;
import static org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager.AuthenticationStatus.PENDING;
import static org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager.AuthenticationStatus.UNAUTHENTICATED;
import static org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager.AuthenticationStatus.UNKNOWN;

/**
 * A manager for all things authentication related.
 */

@Singleton
public class AuthenticationManager {
    public static final String TAG = "AuthManager";
    static final String KEY_REFRESH_TOKEN = "refreshToken";
    static final String KEY_USERNAME = "username";
    private static final String AUTH_KEY = "Basic " + BuildConfig.POVERTY_STOPLIGHT_API_KEY_STRING;
    private static final String KEY_TOKEN_EXPIRATION = "KEY_TOKEN_EXPIRATION";

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
    private AuthStateChangeHandler mHandler;

    public AuthenticationManager(AuthenticationService authService,
                                 SharedPreferences sharedPreferences,
                                 ConnectivityWatcher connectivityWatcher) {
        mAuthService = authService;
        mPreferences = sharedPreferences;
        mConnectivityWatcher = connectivityWatcher;

        mStatus = new MutableLiveData<>();
        mStatus.setValue(AuthenticationManager.isTokenExpired(mPreferences)
                ? UNAUTHENTICATED : AUTHENTICATED);

        mConnectivityWatcher.status().observeForever(this::handleNetworkStatusChange);
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
        if (AuthenticationManager.isTokenExpired(mPreferences)
                || refreshToken != null && username != null)
            return refreshLogin(refreshToken, username);
        else
            return updateStatus(UNAUTHENTICATED);
    }

    public static boolean isTokenExpired(SharedPreferences mPreferences) {
        Date now = new Date();
        final long yesterday = DateUtils.addDays(new Date(), -1).getTime();
        Long expirationTimeStamp = mPreferences.getLong(KEY_TOKEN_EXPIRATION,
                yesterday);
        final Date expirationDate = new Date(expirationTimeStamp);
        Log.d(TAG, String.format("Token is valid until: %s", expirationDate));
        return now.after(expirationDate);
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
        if (user == null) {
            return updateStatus(UNAUTHENTICATED);
        }
        try {
            updateStatus(PENDING);
            String password = user.getPassword();
            if (StringUtils.isBlank(password)) {
                String encrypted = mPreferences.getString(KEY_PASSWORD, null);
                password = SecurityUtils.decrypt(encrypted);
            }
            if (StringUtils.isBlank(password)) {
                return updateStatus(UNAUTHENTICATED);
            } else {
                user.setPassword(password);
            }
            retrofit2.Response<LoginIr> response = mAuthService
                    .loginWithPassword(
                            AUTH_KEY,
                            user.getUsername(),
                            password).execute();
            if (AppConstants.HTTP_SC_BAD_REQUEST == response.code()) {
                return updateStatus(UNKNOWN);
            }
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
        final int expiresInSeconds = mUser.getLogin().getExpiresIn();
        final Date expirationDate = DateUtils.addSeconds(new Date(), expiresInSeconds);

        editor.putString(KEY_REFRESH_TOKEN, mUser.getLogin().getRefreshToken());
        editor.putString(KEY_USERNAME, mUser.getUsername());
        editor.putLong(KEY_TOKEN_EXPIRATION, expirationDate.getTime());
        if (StringUtils.isNotBlank(mUser.getPassword())) {
            editor.putString(KEY_PASSWORD, SecurityUtils.encrypt(mUser.getPassword()));
        } else {
            editor.remove(KEY_PASSWORD);
        }

        editor.apply();
    }

    private void clearLogin() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_TOKEN_EXPIRATION);
        editor.apply();
    }

    public void setAuthStateChangeHandler(AuthStateChangeHandler handler) {
        this.mHandler = handler;
    }

    private AuthenticationStatus updateStatus(AuthenticationStatus newStatus) {
        if (newStatus == mStatus.getValue())
            return newStatus;

        mStatus.postValue(newStatus);

        if (mHandler != null) mHandler.onAuthStateChange(newStatus);

        return newStatus;
    }

    private void handleNetworkStatusChange(@Nullable Boolean isOnline) {
        if (isOnline == null) return;

        if (isOnline && mStatus.getValue() == AUTHENTICATED) {
            tryLoginAsync();
        }
    }

    private void tryLoginAsync() {
        new TryLoginTask().execute();
    }

    private class TryLoginTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            login();
            return null;
        }
    }

    /**
     * Auth state change handler
     */
    public interface AuthStateChangeHandler {
        void onAuthStateChange(AuthenticationManager.AuthenticationStatus status);
    }

    public AuthenticationStatus refreshToken() {
        return login(mUser);
    }


}
