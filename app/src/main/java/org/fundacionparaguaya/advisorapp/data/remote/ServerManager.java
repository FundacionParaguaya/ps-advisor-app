package org.fundacionparaguaya.advisorapp.data.remote;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.util.Log;

import org.fundacionparaguaya.advisorapp.R;

import javax.inject.Singleton;

import static android.content.Context.MODE_PRIVATE;

/**
 * A manager for choosing which server to be connected to.
 */

@Singleton
public class ServerManager {
    public static final String TAG = "ServerManager";
    private static final String PREFS_SERVER = "server";
    private static final String KEY_PROTOCOL = "protocol";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";

    private SharedPreferences mPreferences;
    private MutableLiveData<Server> mSelected;
    private Server[] mServers;


    public ServerManager(Application application) {
        mPreferences = application.getApplicationContext()
                .getSharedPreferences(PREFS_SERVER, MODE_PRIVATE);

        mServers = new Server[] {
            new Server("http","povertystoplightiqp.org", 8080, application.getString(R.string.login_serverdev)),
            new Server("https","testing.backend.povertystoplight.org", 443, application.getString(R.string.login_servertest)),
        };

        mSelected = new MutableLiveData<>();
        mSelected.setValue(loadServerSelection());
    }

    public LiveData<Server> getSelected() {
        return mSelected;
    }

    public Server getSelectedNow() {
        return mSelected.getValue();
    }

    public void setSelected(Server selected) {
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
            Log.w(TAG, "saveServerSelection: Attempted to save a null selected server!");
            return;
        }
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_PROTOCOL, selected.getProtocol());
        editor.putString(KEY_HOST, selected.getHost());
        editor.putInt(KEY_PORT, selected.getPort());
        editor.apply();
    }
}
