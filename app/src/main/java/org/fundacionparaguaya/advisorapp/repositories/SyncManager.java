package org.fundacionparaguaya.advisorapp.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.util.Log;

import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import static android.content.Context.MODE_PRIVATE;
import static org.fundacionparaguaya.advisorapp.repositories.SyncManager.SyncState.ERROR_NO_INTERNET;
import static org.fundacionparaguaya.advisorapp.repositories.SyncManager.SyncState.ERROR_OTHER;
import static org.fundacionparaguaya.advisorapp.repositories.SyncManager.SyncState.NEVER;
import static org.fundacionparaguaya.advisorapp.repositories.SyncManager.SyncState.SYNCED;
import static org.fundacionparaguaya.advisorapp.repositories.SyncManager.SyncState.SYNCING;

/**
 * A utility that manages the synchronization of the local databases.
 */

@Singleton
public class SyncManager {
    private static final String TAG = "SyncManager";
    private static final String PREFS_SYNC = "sync";
    private static final String KEY_LAST_SYNC_TIME = "lastSyncTime";

    private FamilyRepository mFamilyRepository;
    private SurveyRepository mSurveyRepository;
    private SnapshotRepository mSnapshotRepository;
    private SharedPreferences mPreferences;
    private boolean isOnline;

    private MutableLiveData<SyncProgress> mProgress;

    @Inject
    SyncManager(Application application,
                FamilyRepository familyRepository,
                SurveyRepository surveyRepository,
                SnapshotRepository snapshotRepository,
                Merlin networkWatcher) {
        this.mFamilyRepository = familyRepository;
        this.mSurveyRepository = surveyRepository;
        this.mSnapshotRepository = snapshotRepository;

        mPreferences = application.getApplicationContext()
                .getSharedPreferences(PREFS_SYNC, MODE_PRIVATE);

        mProgress = new MutableLiveData<>();

        long lastSyncTime = mPreferences.getLong(KEY_LAST_SYNC_TIME, -1);
        mProgress.setValue(new SyncProgress(lastSyncTime != -1 ? SYNCED : NEVER, lastSyncTime));

        setIsOnline(MerlinsBeard.from(application).isConnected());

        networkWatcher.registerConnectable(() -> setIsOnline(true));
        networkWatcher.registerDisconnectable(() -> setIsOnline(false));
    }

    public LiveData<SyncProgress> getProgress() {
        return mProgress;
    }

    private void setIsOnline(boolean online) {
        if (online) {
            long lastSyncTime = mProgress.getValue().getLastSyncedTime();
            isOnline = true;
            updateProgress(lastSyncTime != -1 ? SYNCED : NEVER);
        } else {
            isOnline = false;
            updateProgress(ERROR_NO_INTERNET);
        }
    }

    /**
     * Synchronizes the local database with the remote one.
     * @return Whether the sync was successful.
     */
    public boolean syncNow() {
        if (!isOnline)
            return false;
        Log.d(TAG, "syncNow: Synchronizing the database...");
        updateProgress(SYNCING);
        boolean result;
        try {
            result = mFamilyRepository.sync();
            result &= mSurveyRepository.sync();
            result &= mSnapshotRepository.sync();
        } catch (Exception e) {
            Log.e(TAG, "syncNow: Error while syncing!", e);
            result = false;
        }

        Log.d(TAG, String.format("syncNow: Finished the synchronization %s.",
                result ? "successfully" : "with errors"));

        if (isOnline) { // should only change sync state if still online
            if (result) {
                updateProgress(SYNCED, new Date().getTime());
            } else {
                updateProgress(ERROR_OTHER);
            }
        }
        return result;
    }

    /**
      * Cleans all of the repositories, removing all entries. Useful for when a user logs out.
      */
    public void cleanNow() {
        Log.d(TAG, "clean: Cleaning the database...");
        updateProgress(SYNCING);
        mFamilyRepository.clean();
        mSurveyRepository.clean();
        mSnapshotRepository.clean();
        updateProgress(NEVER, -1);
        Log.d(TAG, "clean: Finished the database clean");
    }

    private void updateProgress(SyncState state) {
        SyncProgress progress = mProgress.getValue();
        if (progress == null) {
            progress = new SyncProgress(state);
        } else {
            progress = new SyncProgress(state, progress.getLastSyncedTime());
        }
        mProgress.postValue(progress);
    }

    private void updateProgress(SyncState state, long lastSyncTime) {
        mProgress.postValue(new SyncProgress(state, lastSyncTime));

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(KEY_LAST_SYNC_TIME, lastSyncTime);
        editor.apply();
    }

    /**
     * The states that a sync can be in.
     */
    public enum SyncState {
        NEVER,
        SYNCING,
        SYNCED,
        ERROR_NO_INTERNET,
        ERROR_OTHER
    }

    /**
     * The progress of a sync.
     */
    public class SyncProgress {
        private SyncState mState;
        private long mLastSyncedTime;

        SyncProgress(SyncState syncState) {
            this(syncState, -1);
        }

        SyncProgress(SyncState syncState, long lastSyncedTime) {
            mState = syncState;
            mLastSyncedTime = lastSyncedTime;
        }

        public SyncState getSyncState() {
            return mState;
        }

        public long getLastSyncedTime() {
            return mLastSyncedTime;
        }
    }
}
