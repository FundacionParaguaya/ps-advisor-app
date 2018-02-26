package org.fundacionparaguaya.advisorapp.data.remote;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.SharedPreferences;
import android.test.suitebuilder.annotation.SmallTest;

import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrUtils;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.LoginIr;
import org.fundacionparaguaya.advisorapp.jobs.CleanJob;
import org.fundacionparaguaya.advisorapp.jobs.SyncJob;
import org.fundacionparaguaya.advisorapp.models.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static junit.framework.Assert.fail;
import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.AUTHENTICATED;
import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.PENDING;
import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.UNAUTHENTICATED;
import static org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrUtils.accessToken;
import static org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrUtils.accessTokenType;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Test for the AuthenticationManager.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SyncJob.class, CleanJob.class, Response.class})
@SmallTest
public class AuthenticationManagerTest {
    @Mock
    SharedPreferences sharedPreferences;
    @Mock
    SharedPreferences.Editor sharedPreferencesEditor;
    @Mock
    AuthenticationService authService;
    @Mock
    Call<LoginIr> call;
    @Mock
    Response<LoginIr> response;
    @Mock
    ConnectivityWatcher connectivityWatcher;
    private MutableLiveData<Boolean> isOnline;

    @Rule
    public InstantTaskExecutorRule instantTask = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        isOnline = new MutableLiveData<>();
        isOnline.setValue(true);
        when(connectivityWatcher.status()).thenReturn(isOnline);

        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);

        when(authService.loginWithRefreshToken(anyString(), anyString())).thenReturn(call);
        when(authService.loginWithPassword(anyString(), anyString(), anyString()))
                .thenReturn(call);
        when(call.execute()).thenReturn(response);

        mockStatic(SyncJob.class);
        mockStatic(CleanJob.class);
    }

    @Test
    public void status_ShouldUpdateStatus_password() throws Exception {
        setOnline();
        setLoginSuccess();

        AuthenticationManager authManager = authManager();
        authManager.login(user());

        assertThat(authManager.status().getValue(), is(AUTHENTICATED));
    }

    @Test
    public void status_ShouldUpdateStatus_refreshToken() throws Exception {
        setOnline();
        setRefreshToken();
        setLoginSuccess();

        AuthenticationManager authManager = authManager();
        authManager.login();

        assertThat(authManager.status().getValue(), is(AUTHENTICATED));
    }

    @Test
    public void status_ShouldUpdateStatus_failure() {
        setOnline();
        setLoginFailure();

        AuthenticationManager authManager = authManager();
        authManager.login(user());

        assertThat(authManager.status().getValue(), is(UNAUTHENTICATED));
    }

    @Test
    public void status_ShouldUpdateStatus_offline() {
        setOffline();

        AuthenticationManager authManager = authManager();
        authManager.login();

        assertThat(authManager.status().getValue(), is(UNAUTHENTICATED));
    }

    @Test
    public void status_ShouldUpdateStatus_offlinePassword() {
        setOffline();

        AuthenticationManager authManager = authManager();
        authManager.login(user());

        assertThat(authManager.status().getValue(), is(UNAUTHENTICATED));
    }

    @Test
    public void status_ShouldUpdateStatus_offlineRefreshToken() {
        setOffline();
        setRefreshToken();

        AuthenticationManager authManager = authManager();
        authManager.login();

        assertThat(authManager.status().getValue(), is(AUTHENTICATED));
    }

    @Test
    public void status_ShouldUpdateStatus_error() {
        setOnline();
        setNetworkError();

        AuthenticationManager authManager = authManager();
        authManager.login(user());

        assertThat(authManager.status().getValue(), is(UNAUTHENTICATED));
    }

    @Test
    public void status_ShouldUpdateStatus_logout() {
        setOnline();
        setLoginSuccess();

        AuthenticationManager authManager = authManager();
        authManager.login(user());
        authManager.logout();

        assertThat(authManager.status().getValue(), is(UNAUTHENTICATED));
    }

    @Test
    public void status_ShouldUpdateStatus_pending() {
        setOnline();
        Observer observer = mock(Observer.class);

        AuthenticationManager authManager = authManager();
        authManager.status().observeForever(observer);
        authManager.login(user());

        ArgumentCaptor<AuthenticationManager.AuthenticationStatus> authStatusCaptor =
                ArgumentCaptor.forClass(AuthenticationManager.AuthenticationStatus.class);
        verify(observer, atLeastOnce()).onChanged(authStatusCaptor.capture());
        assertThat(authStatusCaptor.getAllValues().get(1), is(PENDING));
    }

    @Test
    public void details_ShouldUpdateDetails_init() {
        AuthenticationManager authManager = authManager();

        assertThat(authManager.getUser(), is(nullValue()));
    }

    @Test
    public void details_ShouldUpdateDetails_password() {
        setOnline();
        setLoginSuccess();

        AuthenticationManager authManager = authManager();
        authManager.login(user());

        assertThat(authManager.getUser().getLogin(), is(notNullValue()));
        assertThat(authManager.getUser().getUsername(), is(user().getUsername()));
    }

    @Test
    public void details_ShouldUpdateDetails_offlineRefreshToken() {
        setOffline();
        setRefreshToken();

        AuthenticationManager authManager = authManager();
        authManager.login();

        assertThat(authManager.getUser().getLogin().getRefreshToken(), is(notNullValue()));
    }

    @Test
    public void details_ShouldUpdateDetails_logout() {
        setOnline();
        setLoginSuccess();

        AuthenticationManager authManager = authManager();
        authManager.login(user());
        authManager.logout();

        assertThat(authManager.getUser(), is(nullValue()));
    }

    @Test
    public void details_ShouldSaveDetails_password() {
        setOnline();
        setLoginSuccess();

        AuthenticationManager authManager = authManager();
        authManager.login(user());

        verify(sharedPreferencesEditor)
                .putString(AuthenticationManager.KEY_REFRESH_TOKEN, refreshToken());
        verify(sharedPreferencesEditor).apply();
    }

    @Test
    public void details_ShouldSaveDetails_logout() {
        setRefreshToken();

        AuthenticationManager authManager = authManager();
        authManager.logout();

        verify(sharedPreferencesEditor)
                .remove(AuthenticationManager.KEY_REFRESH_TOKEN);
        verify(sharedPreferencesEditor).apply();
    }

    @Test
    public void accessString_ShouldUpdateAccessString_unauthenticated() {
        AuthenticationManager authManager = authManager();

        assertThat(authManager.getAccessString(), is(nullValue()));
    }

    @Test
    public void accessString_ShouldUpdateAccessString_password() {
        setLoginSuccess();

        AuthenticationManager authManager = authManager();
        authManager.login(user());

        assertThat(authManager.getAccessString(), startsWith(accessTokenType()));
        assertThat(authManager.getAccessString(), is(accessTokenType() + " " + accessToken()));
    }

    @Test
    public void accessString_ShouldUpdateAccessString_refreshToken() {
        setRefreshToken();
        setLoginSuccess();

        AuthenticationManager authManager = authManager();
        authManager.login();

        assertThat(authManager.getAccessString(), startsWith(accessTokenType()));
        assertThat(authManager.getAccessString(), is(accessTokenType() + " " + accessToken()));
    }

    @Test
    public void accessString_ShouldUpdateAccessString_offline() {
        setOffline();
        setRefreshToken();

        AuthenticationManager authManager = authManager();
        authManager.login();

        assertThat(authManager.getAccessString(), is(nullValue()));
    }

    /**
     * Sets the refresh token and returns it for assertion.
     */
    private void setRefreshToken() {
        when(sharedPreferences.getString(eq(AuthenticationManager.KEY_REFRESH_TOKEN), any()))
                .thenReturn(refreshToken());
    }

    /**
     * Causes the mocked authentication service to return a successful login when called.
     */
    private void setLoginSuccess() {
        when(response.body()).thenReturn(IrUtils.loginSuccess());
        when(response.isSuccessful()).thenReturn(true);
    }

    /**
     * Causes the mocked authentication service to return a failure when called.
     */
    private void setLoginFailure() {
        when(response.isSuccessful()).thenReturn(false);
    }

    /**
     * Causes the connectivity watcher to appear online.
     */
    private void setOnline() {
        isOnline.postValue(true);
        when(connectivityWatcher.isOnline()).thenReturn(true);
        when(connectivityWatcher.isOffline()).thenReturn(false);
    }

    /**
     * Causes the connectivity watcher to appear offline.
     */
    private void setOffline() {
        isOnline.postValue(false);
        when(connectivityWatcher.isOnline()).thenReturn(false);
        when(connectivityWatcher.isOffline()).thenReturn(true);
        setNetworkError();
    }

    /**
     * Causes the authentication service to throw an error when called.
     */
    private void setNetworkError() {
        try {
            when(call.execute()).thenThrow(new IOException());
        } catch (IOException e) {
            fail(); // the mock definition threw an exception? something is very wrong...
        }
    }

    private AuthenticationManager authManager() {
        return new AuthenticationManager(authService, sharedPreferences, connectivityWatcher);
    }

    private User user() {
        return new User("user", "password", true);
    }

    private String refreshToken() {
        return "d87e6156-b5fc-49b8-9b1c-45c4e3b48607";
    }
}