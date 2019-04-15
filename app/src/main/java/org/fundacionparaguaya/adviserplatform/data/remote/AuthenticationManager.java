package org.fundacionparaguaya.adviserplatform.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.fundacionparaguaya.adviserplatform.data.model.Login;
import org.fundacionparaguaya.adviserplatform.data.model.User;
import org.fundacionparaguaya.adviserplatform.data.model.UserRole;
import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.IrMapper;
import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.LoginIr;
import org.fundacionparaguaya.adviserplatform.util.AppConstants;
import org.fundacionparaguaya.adviserplatform.util.SecurityUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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
        AuthenticationStatus authenticationStatus = UNAUTHENTICATED;
        //TODO Sodep: Re-auth with refresh token is not working, throws a BAD_REQUEST response
        String refreshToken = mPreferences.getString(AppConstants.KEY_REFRESH_TOKEN, null);
        String username = mPreferences.getString(AppConstants.KEY_USERNAME, null);
        if (refreshToken != null && username != null &&
                !AuthenticationManager.isTokenExpired(mPreferences)) {
            authenticationStatus = refreshLogin(refreshToken, username);
        }
        if(UNAUTHENTICATED.equals(authenticationStatus)) {
            authenticationStatus = refreshLogin();
        }
        return authenticationStatus;
    }

    public static boolean isTokenExpired(SharedPreferences mPreferences) {
        Date now = new Date();
        final long yesterday = DateUtils.addDays(new Date(), -1).getTime();
        Long expirationTimeStamp = mPreferences.getLong(AppConstants.KEY_TOKEN_EXPIRATION,
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
        String encryptedPassword = mPreferences.getString(KEY_PASSWORD, null);
        String password = null;
        if(StringUtils.isNotBlank(encryptedPassword)) {
            password = SecurityUtils.decrypt(encryptedPassword);
        }
        if (mConnectivityWatcher.isOffline()) {
            mUser = User.builder()
                    .username(username)
                    .password(password)
                    .login(new Login(refreshToken))
                    .build();
            return updateStatus(AUTHENTICATED);//assume authenticated because there is refresh token
        }
        try {
            updateStatus(PENDING);
            retrofit2.Response<LoginIr> response = mAuthService
                    .loginWithRefreshToken(
                            AppConstants.AUTH_KEY,
                            refreshToken).execute();

            return updateLogin(User.builder()
                    .username(username)
                    .password(password).build(), response);
        } catch (IOException e) {
            Log.e(TAG, "refreshToken: Could not refresh the token!", e);
            return updateStatus(UNAUTHENTICATED);
        }
    }

    public AuthenticationStatus getToken(User user) {
        if (user == null) {
            return updateStatus(UNAUTHENTICATED);
        }
        try {
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
            updateStatus(PENDING);
            retrofit2.Response<LoginIr> response = mAuthService
                    .loginWithPassword(
                            AppConstants.AUTH_KEY,
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
            /**
             First it's necessary to validate if the user attempting to login has the
             role of 'SURVEY_USER', since this is the only role to which the app
             is targeting at the moment.
             */
            Login newLogin = IrMapper.mapLogin(response.body());

            if (!validateUserRole(newLogin.getUser().getAuthorities()) ) {
                return updateStatus(UNAUTHENTICATED);
            }

            if (user == null) {
                mUser = User.builder().login(newLogin).build();
            } else {
                mUser = user;
                mUser.setLogin(newLogin);
            }
            mUser.setOrganization(newLogin.getUser().getOrganization());
            saveLogin();
            return updateStatus(AUTHENTICATED);
        } else {
            mUser = null;
            return updateStatus(UNAUTHENTICATED);
        }
    }

    private boolean validateUserRole(List<UserRole> roles) {
        /**
         * A user may (or may not?) have more than one role
         * That's why we should iterate over all the values that
         * @getAuthority() retrieves*/
        for (UserRole role: roles) {
            if (AppConstants.USER_ROLE.equals(role.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private void saveLogin() {
        SharedPreferences.Editor editor = mPreferences.edit();
        final int expiresInSeconds = mUser.getLogin().getExpiresIn();
        final Date expirationDate = DateUtils.addSeconds(new Date(), expiresInSeconds);

        editor.putString(AppConstants.KEY_REFRESH_TOKEN, mUser.getLogin().getRefreshToken());
        editor.putString(AppConstants.KEY_USERNAME, mUser.getUsername());
        editor.putLong(AppConstants.KEY_TOKEN_EXPIRATION, expirationDate.getTime());
        editor.putLong(AppConstants.ORGANIZATION_ID, mUser.getOrganization().getId());
        if (StringUtils.isNotBlank(mUser.getPassword())) {
            editor.putString(KEY_PASSWORD, SecurityUtils.encrypt(mUser.getPassword()));
        } else {
            editor.remove(KEY_PASSWORD);
        }

        editor.apply();
    }

    private void clearLogin() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(AppConstants.KEY_REFRESH_TOKEN);
        //We don't delete username yet, if same user comes back; data should still be available
        //editor.remove(AppConstants.KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.remove(AppConstants.KEY_TOKEN_EXPIRATION);
        editor.remove(AppConstants.ORGANIZATION_ID);
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

    public AuthenticationStatus refreshLogin() {
        String userName = mPreferences.getString(AppConstants.KEY_USERNAME, null);
        String encrypted = mPreferences.getString(KEY_PASSWORD, null);
        String password = SecurityUtils.decrypt(encrypted);
        if(mUser == null) {
            mUser = User.builder().username(userName).password(password).build();
        }
        mUser.setUsername(userName);
        mUser.setPassword(password);
        return login(mUser);
    }

}
