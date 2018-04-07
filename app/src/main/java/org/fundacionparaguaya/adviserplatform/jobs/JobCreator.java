package org.fundacionparaguaya.adviserplatform.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.evernote.android.job.Job;
import org.fundacionparaguaya.adviserplatform.AdviserApplication;
import org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager;
import org.fundacionparaguaya.adviserplatform.data.repositories.SyncManager;

import javax.inject.Inject;

/**
 * A utility that creates jobs.
 */

public class JobCreator implements com.evernote.android.job.JobCreator {
    private AdviserApplication mApplication;

    @Inject
    AuthenticationManager mAuthManager;

    @Inject
    SyncManager mSyncManager;

    public JobCreator(AdviserApplication application) {
        this.mApplication = application;

        mApplication.getApplicationComponent().inject(this);
    }

    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case SyncJob.TAG:
                return new SyncJob(mSyncManager, mAuthManager);
            case CleanJob.TAG:
                return new CleanJob(mSyncManager);
            default:
                return null;
        }
    }
}
