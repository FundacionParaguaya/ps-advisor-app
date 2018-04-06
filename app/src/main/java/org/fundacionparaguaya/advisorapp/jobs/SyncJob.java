package org.fundacionparaguaya.advisorapp.jobs;

import android.support.annotation.NonNull;

import android.util.Log;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager;
import org.fundacionparaguaya.advisorapp.data.repositories.SyncManager;

/**
 * A job to sync the database.
 */

public class SyncJob extends Job {
    public static final String TAG = "SyncJob";
    private static final long SYNC_INTERVAL_MS = 900000; //15 mins
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
        if(mAuthManager.getStatus() != AuthenticationManager.AuthenticationStatus.AUTHENTICATED)
        {
            return Result.RESCHEDULE;
        }

        if(params.isExact())
        {
            stopPeriodic();
        }

        Result syncResult;

        if (mSyncManager.sync()) {
            syncResult = Result.SUCCESS;
        }
        else
            syncResult = Result.FAILURE;

        if(params.isExact()) {
            schedulePeriodic();
        }

        return syncResult;
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
                .setRequirementsEnforced(true)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    public static boolean isSyncInProgress()
    {
        boolean inProgress=false;

        for(Job job: JobManager.instance().getAllJobsForTag(TAG))
        {
            inProgress^=!job.isFinished();
        }

        return inProgress;
    }

    private static void stopPeriodic()
    {
        for(JobRequest job: JobManager.instance().getAllJobRequestsForTag(TAG))
        {
            if(job.isPeriodic()) JobManager.instance().cancel(job.getJobId());
        }
    }

    public static void cancelAll() {
        JobManager.instance().cancelAllForTag(TAG);
    }
}
