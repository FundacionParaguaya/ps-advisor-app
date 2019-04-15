package org.fundacionparaguaya.adviserplatform.jobs;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager;
import org.fundacionparaguaya.adviserplatform.data.repositories.SyncManager;
import org.fundacionparaguaya.adviserplatform.util.MixpanelHelper;

import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

/**
 * A job to sync the database.
 */

public class SyncJob extends Job {
    public static final String TAG = "SyncJob";
    public static final long SYNC_INTERVAL_MS = 1800000; //30 mins
    private SyncManager mSyncManager;
    private AuthenticationManager mAuthManager;
    private static AtomicBoolean mIsAlive = new AtomicBoolean();

    public SyncJob(SyncManager syncManager, AuthenticationManager authManager) {
        super();
        this.mSyncManager = syncManager;
        this.mAuthManager = authManager;
        mIsAlive.set(true);
    }

//    @Override
    protected void onCancel() {
      //  mIsAlive.set(false);
        Timber.d("Cancel requested... (We'll do our best)");
    }

    @Override
    @NonNull
    protected Result onRunJob(@NonNull Params params) {
        Result syncResult;

        MixpanelHelper.SyncEvents.syncStarted(getContext());

        final AuthenticationManager.AuthenticationStatus status = mAuthManager.getStatus();
        Log.d(TAG, String.format("Authentication Status: %s", status));
        if(status != AuthenticationManager.AuthenticationStatus.AUTHENTICATED) {
            mAuthManager.refreshLogin();
        }
        if(status != AuthenticationManager.AuthenticationStatus.AUTHENTICATED) {
            syncResult = Result.RESCHEDULE;
        } else {
            if (params.isExact()) //cancel any scheduled jobs cause we running RIGHT HERE, RIGHT NOW BOI
            {
                stopPeriodic();
            }

            if (mSyncManager.sync(mIsAlive)) {
                syncResult = Result.SUCCESS;
            } else
                syncResult = Result.FAILURE;

            if (params.isExact()) {
                schedulePeriodic(); //enough fun, let's get those regularly scheduled jobs back in
            }

            MixpanelHelper.SyncEvents.syncEnded(getContext(), syncResult == Result.SUCCESS);
        }
        Log.d(TAG, "Sync is over");

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
                .setPeriodic(SYNC_INTERVAL_MS, 600000)
                .setRequiresDeviceIdle(false)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .build()
                .schedule();
    }

    /**
     * Looks for any existing jobs that have been created (even if they haven't been ran)
     * @return if there are job(s) that have been created for syncing
     */
    public static boolean isSyncAboutToStart()
    {
        boolean inProgress=false;

        for(JobRequest jobRequest: JobManager.instance().getAllJobRequestsForTag(TAG))
        {
            inProgress|=(jobRequest.isExact() && jobRequest.getStartMs()<500);
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
        setIsAlive(false);
    }

    private static void setIsAlive(boolean b) {
        mIsAlive.set(b);
    }
}
