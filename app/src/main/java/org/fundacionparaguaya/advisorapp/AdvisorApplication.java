package org.fundacionparaguaya.advisorapp;

import android.app.Application;

import org.fundacionparaguaya.advisorapp.dependencyinjection.ApplicationComponent;
import org.fundacionparaguaya.advisorapp.dependencyinjection.ApplicationModule;
import org.fundacionparaguaya.advisorapp.dependencyinjection.DaggerApplicationComponent;
import org.fundacionparaguaya.advisorapp.dependencyinjection.DatabaseModule;

/**
 * The advisor application.
 */

public class AdvisorApplication extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
