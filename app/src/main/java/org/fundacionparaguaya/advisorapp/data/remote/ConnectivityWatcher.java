package org.fundacionparaguaya.advisorapp.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * A utility to watch and store the Internet connectivity for the application.
 */

public class ConnectivityWatcher extends BroadcastReceiver {
    private MutableLiveData<Boolean> mOnline;
    private ConnectivityManager mConnectivityManager;

    public ConnectivityWatcher(ConnectivityManager connectivityManager) {
        mOnline = new MutableLiveData<>();
        mConnectivityManager = connectivityManager;
        updateStatus();
    }

    public LiveData<Boolean> status() {
        return mOnline;
    }

    public boolean isOnline() {
        Boolean online = mOnline.getValue();
        return online != null ? online : false;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        updateStatus();
    }

    private void updateStatus() {
        NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
        mOnline.postValue(activeNetwork != null && activeNetwork.isConnected());
    }
}
