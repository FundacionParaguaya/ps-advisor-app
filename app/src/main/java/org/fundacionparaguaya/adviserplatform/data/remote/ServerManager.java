package org.fundacionparaguaya.adviserplatform.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;

import org.fundacionparaguaya.assistantadvisor.BuildConfig;
import org.fundacionparaguaya.assistantadvisor.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import timber.log.Timber;

/**
 * A manager for choosing which server to be connected to.
 */

@Singleton
public class ServerManager {
    public static final String TAG = "ServerManager";
    static final String KEY_PROTOCOL = "protocol";
    static final String KEY_HOST = "host";
    static final String KEY_PORT = "port";

    private SharedPreferences mPreferences;
    private MutableLiveData<Server> mSelected;
    private Server[] mServers;

    public ServerManager(Context context, SharedPreferences sharedPreferences) {
        mPreferences = sharedPreferences;

        final List<Server> servers = new ArrayList<>();
        servers.addAll(Arrays.asList(
                new Server("https", "platform.backend.povertystoplight.org", 443,
                        context.getString(R.string.login_serverprod)),
                new Server("https", "demo.backend.povertystoplight.org", 443,
                        context.getString(R.string.login_serverdemo)),
                new Server("https", "testing.backend.povertystoplight.org", 443,
                        context.getString(R.string.login_servertest))));
        if (BuildConfig.DEBUG) {
            servers.add(0,new Server("http", context.getString(R.string.local_dev_host),
                    Integer.parseInt(context.getString(R.string.local_dev_port)),
                    "Local Dev"));
            servers.add(0,new Server("http", context.getString(R.string.sodep_dev_host),
                    Integer.parseInt(context.getString(R.string.sodep_dev_port)),
                    "Sodep Dev"));
        }
        mServers = servers.toArray(new Server[0]);

        mSelected = new MutableLiveData<>();

        Server selected = loadServerSelection();
        mSelected.setValue(selected);
        saveServerSelection(selected);
    }

    public LiveData<Server> selected() {
        return mSelected;
    }

    public Server getSelected() {
        return mSelected.getValue();
    }

    public void setSelected(Server selected) {
        if (selected == null) {
            return;
        }
        mSelected.postValue(selected);
        saveServerSelection(selected);
    }

    public Server[] getServers() {
        return mServers;
    }

    private Server loadServerSelection() {
        Server defaultServer = mServers[0];
        String protocol = mPreferences.getString(KEY_PROTOCOL, defaultServer.getProtocol());
        String host = mPreferences.getString(KEY_HOST, defaultServer.getHost());
        int port = mPreferences.getInt(KEY_PORT, defaultServer.getPort());
        for (Server server : mServers) {
            if (server.getProtocol().equals(protocol)
                    && server.getHost().equals(host)
                    && server.getPort() == port) {
                return server;
            }
        }
        return mServers[0];
    }

    private void saveServerSelection(Server selected) {
        if (selected == null) {
            Timber.w("saveServerSelection: Attempted to save a null selected server!");
            return;
        }
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_PROTOCOL, selected.getProtocol());
        editor.putString(KEY_HOST, selected.getHost());
        editor.putInt(KEY_PORT, selected.getPort());
        editor.apply();
    }
}
