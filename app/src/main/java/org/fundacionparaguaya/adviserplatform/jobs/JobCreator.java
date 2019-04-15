package org.fundacionparaguaya.adviserplatform.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.evernote.android.job.Job;
import org.fundacionparaguaya.assistantadvisor.AdviserAssistantApplication;
import org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager;
import org.fundacionparaguaya.adviserplatform.data.repositories.SyncManager;

import javax.inject.Inject;

/**
 * A utility that creates jobs.
 */

public class JobCreator implements com.evernote.android.job.JobCreator {
    private AdviserAssistantApplication mApplication;

    @Inject
    AuthenticationManager mAuthManager;

    @Inject
    SyncManager mSyncManager;

    public JobCreator(AdviserAssistantApplication application) {
        this.mApplication = application;

        mApplication.getApplicationComponent().inject(this);
    }

    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case SyncJob.TAG:
                return new SyncJob(mSyncManager, mAuthManager);
            default:
                return null;
        }
    }
}
