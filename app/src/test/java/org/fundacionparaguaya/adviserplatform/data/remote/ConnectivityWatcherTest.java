package org.fundacionparaguaya.adviserplatform.data.remote;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.test.suitebuilder.annotation.SmallTest;

import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A tests for the ConnectivityWatcher.
 */

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class ConnectivityWatcherTest {
    @Mock
    Context context;
    @Mock
    Merlin merlin;
    @Mock
    MerlinsBeard merlinsBeard;

    @Rule
    public InstantTaskExecutorRule instantTask = new InstantTaskExecutorRule();

    @Test
    public void status_ShouldUpdateStatus_initOffline() {
        setOffline();

        ConnectivityWatcher connectivityWatcher = connectivityWatcher();

        assertThat(connectivityWatcher.isOnline(), is(false));
        verify(merlinsBeard).isConnected();
    }

    @Test
    public void status_ShouldUpdateStatus_initOnline() {
        setOnline();

        ConnectivityWatcher connectivityWatcher = connectivityWatcher();

        assertThat(connectivityWatcher.isOnline(), is(true));
        verify(merlinsBeard).isConnected();
    }

    @Test
    public void status_ShouldUpdateStatus_disconnect() {
        setOnline();

        ConnectivityWatcher connectivityWatcher = connectivityWatcher();
        disconnect();

        assertThat(connectivityWatcher.isOffline(), is(true));
    }

    @Test
    public void status_ShouldUpdateStatus_repeated() {
        setOffline();

        ConnectivityWatcher connectivityWatcher = connectivityWatcher();
        disconnect();

        assertThat(connectivityWatcher.isOffline(), is(true));
    }

    @Test
    public void status_ShouldUpdateStatus_connect() {
        setOffline();

        ConnectivityWatcher connectivityWatcher = connectivityWatcher();
        connect();

        assertThat(connectivityWatcher.isOffline(), is(false));
    }

    public void status_ShouldNotifyChanges() {
        setOffline();

        Observer observer = mock(Observer.class);
        ConnectivityWatcher connectivityWatcher = connectivityWatcher();
        connectivityWatcher.status().observeForever(observer);
        connect();

        verify(observer).onChanged(true);
    }

    private void setOnline() {
        when(merlinsBeard.isConnected()).thenReturn(true);
    }

    private void setOffline() {
        when(merlinsBeard.isConnected()).thenReturn(false);
    }

    private void connect() {
        ArgumentCaptor<Connectable> argumentCaptor =
                ArgumentCaptor.forClass(Connectable.class);
        verify(merlin).registerConnectable(argumentCaptor.capture());

        argumentCaptor.getValue().onConnect();
    }

    private void disconnect() {
        ArgumentCaptor<Disconnectable> argumentCaptor =
                ArgumentCaptor.forClass(Disconnectable.class);
        verify(merlin).registerDisconnectable(argumentCaptor.capture());

        argumentCaptor.getValue().onDisconnect();
    }

    private ConnectivityWatcher connectivityWatcher() {
        return new ConnectivityWatcher(merlin, merlinsBeard);
    }
}