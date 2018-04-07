package org.fundacionparaguaya.adviserplatform.data.remote;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.suitebuilder.annotation.SmallTest;

import org.fundacionparaguaya.adviserplatform.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A tests for the ServerManager.
 */

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class ServerManagerTest {
    @Mock
    Context context;
    @Mock
    SharedPreferences sharedPreferences;
    @Mock
    SharedPreferences.Editor sharedPreferencesEditor;

    @Rule
    public InstantTaskExecutorRule instantTask = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
    }

    @Test
    public void init_ShouldLoadServer_prefs() {
        setSavedServer();

        ServerManager serverManager = serverManager();

        assertThat(serverManager.getSelected(), is(server()));
    }

    @Test
    public void init_ShouldLoadServer_default() {
        ServerManager serverManager = serverManager();

        assertThat(serverManager.getSelected(), is(notNullValue()));
        assertThat(serverManager.getSelected(), is(serverManager.getServers()[0]));
    }

    @Test
    public void init_ShouldLoadServer_mismatch() {
        setSavedServer(new Server("https", "donkeykong.com", 1234, "DONKEY KONG"));

        ServerManager serverManager = serverManager();

        assertThat(serverManager.getSelected(), is(serverManager.getServers()[0]));
    }

    @Test
    public void save_ShouldSaveServer_default() {
        ServerManager serverManager = serverManager();

        Server server = serverManager.getSelected();
        verify(sharedPreferencesEditor).putString(ServerManager.KEY_HOST, server.getHost());
        verify(sharedPreferencesEditor).putInt(ServerManager.KEY_PORT, server.getPort());
        verify(sharedPreferencesEditor).putString(ServerManager.KEY_PROTOCOL, server.getProtocol());
        verify(sharedPreferencesEditor).apply();
    }

    @Test
    public void save_ShouldSaveServer_prefs() {
        setSavedServer();

        ServerManager serverManager = serverManager();

        Server server = serverManager.getSelected();
        verify(sharedPreferencesEditor).putString(ServerManager.KEY_HOST, server.getHost());
        verify(sharedPreferencesEditor).putInt(ServerManager.KEY_PORT, server.getPort());
        verify(sharedPreferencesEditor).putString(ServerManager.KEY_PROTOCOL, server.getProtocol());
        verify(sharedPreferencesEditor).apply();
    }

    @Test
    public void save_ShouldSaveServer_change() {
        ServerManager serverManager = serverManager();
        reset(sharedPreferencesEditor);
        serverManager.setSelected(server());

        Server server = serverManager.getSelected();
        verify(sharedPreferencesEditor).putString(ServerManager.KEY_HOST, server.getHost());
        verify(sharedPreferencesEditor).putInt(ServerManager.KEY_PORT, server.getPort());
        verify(sharedPreferencesEditor).putString(ServerManager.KEY_PROTOCOL, server.getProtocol());
        verify(sharedPreferencesEditor).apply();
    }

    @Test
    public void save_ShouldSaveServer_null() {
        setSavedServer();

        ServerManager serverManager = serverManager();
        reset(sharedPreferencesEditor);
        serverManager.setSelected(null);

        verify(sharedPreferencesEditor, never()).apply();
        assertThat(serverManager.getSelected(), is(notNullValue()));
    }

    private void setSavedServer() {
        setSavedServer(server());
    }

    private void setSavedServer(Server server) {
        when(sharedPreferences.getString(eq(ServerManager.KEY_HOST), anyString()))
                .thenReturn(server.getHost());
        when(sharedPreferences.getInt(eq(ServerManager.KEY_PORT), anyInt()))
                .thenReturn(server.getPort());
        when(sharedPreferences.getString(eq(ServerManager.KEY_PROTOCOL), anyString()))
                .thenReturn(server.getProtocol());
    }

    private ServerManager serverManager() {
        return new ServerManager(context, sharedPreferences);
    }

    private Server server() {
        return new Server("https",
                "testing.backend.povertystoplight.org",
                443,
                context.getString(R.string.login_servertest));
    }
}