package org.fundacionparaguaya.advisorapp.data.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.AUTHENTICATED;

/**
 * An interceptor which injects authentication headers into outgoing requests.
 */

public class AuthenticationInterceptor implements Interceptor {
    private static final String TAG = "AuthInterceptor";
    private static final int MAX_RETRIES = 1;

    private AuthenticationManager mAuthManager;

    public AuthenticationInterceptor(AuthenticationManager authManager) {
        this.mAuthManager = authManager;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (mAuthManager.getStatus().getValue() != AUTHENTICATED) {
            return chain.proceed(chain.request());
        }

        int retries = 0;
        Response response = null;
        while (retries <= MAX_RETRIES) {
            Request request = chain.request().newBuilder()
                    .header("Authorization", mAuthManager.getUser().getLogin().getAuthenticationString())
                    .build();

            response = chain.proceed(request);
            if (response.code() == 401) {
                Log.i(TAG, "intercept: Got an unauthorized message, refreshing the token.");
                mAuthManager.login();
                retries += 1;
            } else {
                return response;
            }
        }
        return response;
    }



}
