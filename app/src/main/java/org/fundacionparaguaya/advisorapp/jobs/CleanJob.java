package org.fundacionparaguaya.advisorapp.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import org.fundacionparaguaya.advisorapp.repositories.SyncManager;

/**
 * A job to clean the database.
 */

public class CleanJob extends Job {
    public static final String TAG = "CleanJob";

    private SyncManager mSyncManager;

    public CleanJob(SyncManager syncManager) {
        super();
        this.mSyncManager = syncManager;
    }

    @Override
    @NonNull
    protected Result onRunJob(@NonNull Params params) {
        mSyncManager.clean();
        return Result.SUCCESS;
    }


    public static void clean() {
        new JobRequest.Builder(TAG)
                .startNow()
                .build()
                .schedule();
    }
}
