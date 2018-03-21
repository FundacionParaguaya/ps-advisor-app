package org.fundacionparaguaya.advisorapp.dependencyinjection;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

/**
 * The module responsible for creating and satisfying dependencies relating to the application.
 */

@Module
public class ApplicationModule {
    private static final String SHARED_PREFS_NAME = "advisor_app";

    private final Application application;

    public ApplicationModule(AdvisorApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return application.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
    }
}
