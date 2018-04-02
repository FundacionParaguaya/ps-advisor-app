package org.fundacionparaguaya.advisorapp.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager;
import org.fundacionparaguaya.advisorapp.data.repositories.SyncManager;

import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.AUTHENTICATED;

/**
 * A job to sync the database.
 */

public class SyncJob extends Job {
    public static final String TAG = "SyncJob";

    private SyncManager mSyncManager;
    private AuthenticationManager mAuthManager;

    public SyncJob(SyncManager syncManager, AuthenticationManager authManager) {
        super();
        this.mSyncManager = syncManager;
        this.mAuthManager = authManager;
    }

    @Override
    @NonNull
    protected Result onRunJob(@NonNull Params params) {
        if (mAuthManager.getStatus() != AUTHENTICATED)
            return Result.RESCHEDULE;

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

    public static void startPeriodic() {
        new JobRequest.Builder(TAG)
                .setPeriodic(900000)
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
