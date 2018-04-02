package org.fundacionparaguaya.advisorapp.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import org.fundacionparaguaya.advisorapp.data.repositories.SyncManager;

/**
 * A job to sync the database.
 */

public class SyncJob extends Job {
    public static final String TAG = "SyncJob";
    private static final long SYNC_INTERVAL_MS = 900000;
    private SyncManager mSyncManager;

    public SyncJob(SyncManager syncManager) {
        super();
        this.mSyncManager = syncManager;
    }

    @Override
    @NonNull
    protected Result onRunJob(@NonNull Params params) {
        if (mSyncManager.sync())
            return Result.SUCCESS;
        else
            return Result.FAILURE;
    }


    public static void sync() {
        new JobRequest.Builder(TAG)
                .startNow()
                .build()
                .schedule();
    }

    public static void schedulePeriodic() {
        new JobRequest.Builder(TAG)
                .setPeriodic(SYNC_INTERVAL_MS, JobRequest.MIN_FLEX)
                .setRequiresDeviceIdle(false)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setUpdateCurrent(true)
                .setRequirementsEnforced(true)
                .build()
                .schedule();
    }

    public static void stopPeriodic() {
        JobManager.instance().cancelAllForTag(TAG);
    }
}
