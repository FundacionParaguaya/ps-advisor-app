package org.fundacionparaguaya.advisorapp.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;

/**
 * A utility to watch and store the Internet connectivity for the application.
 */

public class ConnectivityWatcher {
    private MutableLiveData<Boolean> mOnline;

    public ConnectivityWatcher(Merlin merlin, MerlinsBeard merlinsBeard) {
        mOnline = new MutableLiveData<>();

        mOnline.setValue(merlinsBeard.isConnected());
        merlin.registerConnectable(() ->  {
            mOnline.postValue(true);
        });
        merlin.registerDisconnectable(() -> {
            mOnline.postValue(false);
        });
    }

    public LiveData<Boolean> status() {
        return mOnline;
    }

    public boolean isOnline() {
        Boolean online = mOnline.getValue();
        return online != null ? online : false;
    }

    public boolean isOffline() {
        Boolean online = mOnline.getValue();
        return online != null && !online;
    }
}
