package org.fundacionparaguaya.advisorapp.dependencyinjection;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;

/**
 * The main application component.
 */

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class})
public interface ApplicationComponent {

    Application application();
}
