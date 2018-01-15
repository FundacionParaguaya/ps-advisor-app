package org.fundacionparaguaya.advisorapp.dependencyinjection;

import android.app.Application;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * The module responsible for creating and satisfying dependencies relating to the application.
 */

@Module
public class ApplicationModule {
    private final Application application;

    public ApplicationModule(AdvisorApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }
}
