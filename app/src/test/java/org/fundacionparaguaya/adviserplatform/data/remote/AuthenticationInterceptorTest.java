package org.fundacionparaguaya.adviserplatform.data.remote;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationInterceptor.MAX_RETRIES;
import static org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager.AuthenticationStatus.AUTHENTICATED;
import static org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager.AuthenticationStatus.UNAUTHENTICATED;
import static org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.IrUtils.accessToken;
import static org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.IrUtils.accessTokenType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for the AuthenticationInterceptor.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Request.class, Response.class})
@SmallTest
public class AuthenticationInterceptorTest {
    @Mock
    AuthenticationManager authManager;
    @Mock
    Interceptor.Chain chain;
    @Mock
    Request requestOrig;
    @Mock
    Request requestNew;
    @Mock
    Request.Builder builder;
    @Mock
    Response response;

    @Rule
    public InstantTaskExecutorRule instantTask = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        when(authManager.getAccessString()).thenReturn(accessString());

        when(chain.request()).thenReturn(requestOrig);
        when(requestOrig.newBuilder()).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(requestNew);
        when(chain.proceed(any(Request.class))).thenReturn(response);
        when(response.code()).thenReturn(200);
        when(response.isSuccessful()).thenReturn(true);
    }

    @Test
    public void intercept_AddAuthentication() throws IOException {
        setAuthenticated();

        AuthenticationInterceptor authInterceptor = authInterceptor();
        authInterceptor.intercept(chain);

        verify(builder).header("Authorization", accessString());
        verify(chain).proceed(requestNew);
    }

    @Test
    public void intercept_AddAuthentication_error() throws IOException {
        setAuthenticated();
        setServerUnauthorized();

        AuthenticationInterceptor authInterceptor = authInterceptor();
        authInterceptor.intercept(chain);

        verify(builder, times(MAX_RETRIES + 1)).header("Authorization", accessString());
        verify(builder, times(MAX_RETRIES + 1)).build();
        verify(chain, times(MAX_RETRIES + 1)).proceed(requestNew);
        verify(authManager, times(MAX_RETRIES)).login();
    }

    @Test
    public void intercept_AddAuthentication_unauthorized() throws IOException {
        setUnauthenticated();

        AuthenticationInterceptor authInterceptor = authInterceptor();
        authInterceptor.intercept(chain);

        verify(builder, never()).build();
        verify(chain).proceed(requestOrig);
    }

    private void setAuthenticated() {
        when(authManager.getStatus()).thenReturn(AUTHENTICATED);
    }

    private void setUnauthenticated() {
        when(authManager.getStatus()).thenReturn(UNAUTHENTICATED);
    }

    private void setServerUnauthorized() {
        when(response.code()).thenReturn(401);
        when(response.isSuccessful()).thenReturn(false);
    }

    private AuthenticationInterceptor authInterceptor() {
        return new AuthenticationInterceptor(authManager);
    }

    private String accessString() {
        return accessTokenType() + " " + accessToken();
    }
}