package org.fundacionparaguaya.advisorapp;

import android.support.multidex.MultiDexApplication;

import com.evernote.android.job.JobManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

import org.fundacionparaguaya.advisorapp.dependencyinjection.ApplicationComponent;
import org.fundacionparaguaya.advisorapp.dependencyinjection.ApplicationModule;
import org.fundacionparaguaya.advisorapp.dependencyinjection.DaggerApplicationComponent;
import org.fundacionparaguaya.advisorapp.dependencyinjection.DatabaseModule;
import org.fundacionparaguaya.advisorapp.jobs.JobCreator;
import org.fundacionparaguaya.advisorapp.util.MixpanelHelper;

/**
 * The advisor application.
 */

public class AdvisorApplication extends MultiDexApplication {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule(this))
                .build();

        new Instabug.Builder(this, BuildConfig.INSTABUG_API_KEY_STRING)
                .setInvocationEvent(InstabugInvocationEvent.FLOATING_BUTTON)
                .build();

        MixpanelHelper.identify(getApplicationContext());

        JobManager.create(this).addJobCreator(new JobCreator(this));

        Fresco.initialize(this);
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
