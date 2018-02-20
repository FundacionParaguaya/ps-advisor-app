package org.fundacionparaguaya.advisorapp.data.remote;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An interceptor which injects server information into outgoing requests.
 */

public class ServerInterceptor implements Interceptor {
    private ServerManager mServerManager;

    public ServerInterceptor(ServerManager serverManager) {
        this.mServerManager = serverManager;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Server server = mServerManager.getSelected();

        Request old = chain.request();
        Request request = old.newBuilder()
                .url(old.url().newBuilder()
                        .scheme(server.getProtocol())
                        .host(server.getHost())
                        .port(server.getPort()).build())
                .build();
        return chain.proceed(request);
    }



}
