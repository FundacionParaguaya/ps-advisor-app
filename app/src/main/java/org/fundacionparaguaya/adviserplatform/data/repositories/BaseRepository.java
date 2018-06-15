package org.fundacionparaguaya.adviserplatform.data.repositories;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.time.DateUtils;
import org.fundacionparaguaya.adviserplatform.jobs.SyncJob;
import org.fundacionparaguaya.adviserplatform.ui.dashboard.DashActivity;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.fundacionparaguaya.adviserplatform.util.AppConstants.KEY_LAST_SYNC_TIME;

/**
 * Base Repository class. Provides functionality for checking if the sync should be aborted {@link #shouldAbortSync()}
 * <p>
 * Child classes should override the {@link #sync(Date)} function, and call periodically call {@link #shouldAbortSync()}
 * to handle job cancellations mid-sync
 */

public abstract class BaseRepository {

    private String preferenceKey;

    private DashActivity mDashActivity;

    private long recordsCount;


    //TODO Sodep: not clear what this flag does to manage sync status
    private AtomicBoolean mIsAlive = null;

    public boolean sync(AtomicBoolean isAlive, @Nullable Date lastSync) {
        mIsAlive = isAlive;
        return sync(lastSync);
    }

    /**
     * Main sync function. Any repository implementing this function should call {@link #shouldAbortSync()} periodically
     * throughout syncing and abort the sync if false. The function should also call {@link #clearSyncStatus()} after sync
     * is finished.
     */
    abstract boolean sync(@Nullable Date lastSync);

    public boolean shouldAbortSync() {
        return !(mIsAlive == null || mIsAlive.get());
    }

    public void clearSyncStatus() {
        mIsAlive = null;
    }

    public String getPreferenceKey() {
        return preferenceKey;
    }

    public void setPreferenceKey(String preferenceKey) {
        this.preferenceKey = preferenceKey;
    }

    public boolean needsSync(SharedPreferences preferences) {
        boolean doSync = true;
        final Date defaultToYesterday = DateUtils.addDays(new Date(), -1);
        long lastSyncTimeStamp = preferences.getLong(getPreferenceKey(),
                -1);
        if (lastSyncTimeStamp > 0) {
            final Date nextSync = DateUtils.addMilliseconds(new Date(lastSyncTimeStamp),
                    (int) SyncJob.SYNC_INTERVAL_MS);
            doSync = nextSync.before(new Date());
        }
        return doSync;
    }

    public void updateSyncDate(SharedPreferences preferences) {
        final SharedPreferences.Editor edit = preferences.edit();
        edit.putLong(getPreferenceKey(), new Date().getTime());
        edit.commit();
    }

    public void clearSyncDate(SharedPreferences preferences) {
        final SharedPreferences.Editor edit = preferences.edit();
        edit.remove(getPreferenceKey());
        edit.commit();
    }

    public abstract void clean();

    public long getLastSyncDate(SharedPreferences sharedPreferences) {
        return sharedPreferences.getLong(getPreferenceKey(), -1);
    }

    public DashActivity getDashActivity() {
        return mDashActivity;
    }

    public void setDashActivity(DashActivity mDashActivity) {
        this.mDashActivity = mDashActivity;
    }

    public AtomicBoolean getmIsAlive() {
        return mIsAlive;
    }

    public void setmIsAlive(AtomicBoolean mIsAlive) {
        this.mIsAlive = mIsAlive;
    }

    public long getRecordsCount() {
        return recordsCount;
    }

    public void setRecordsCount(long recordsCount) {
        this.recordsCount = recordsCount;
    }
}
