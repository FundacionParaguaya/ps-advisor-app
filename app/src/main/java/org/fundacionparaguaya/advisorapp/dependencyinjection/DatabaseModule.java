package org.fundacionparaguaya.advisorapp.dependencyinjection;

import android.app.Application;
import android.arch.persistence.room.Room;

import org.fundacionparaguaya.advisorapp.data.local.FamilyDao;
import org.fundacionparaguaya.advisorapp.data.local.LocalDatabase;
import org.fundacionparaguaya.advisorapp.rapositories.FamilyRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * The module responsible for creating and satisfying dependencies relating to the database.
 */

@Module
public class DatabaseModule {
    private final LocalDatabase database;

    public DatabaseModule(Application application) {
        this.database = Room.databaseBuilder(
                application,
                LocalDatabase.class,
                "Advisor.db"
        ).build();
    }

    @Provides
    @Singleton
    FamilyRepository provideAdvisorRepository(FamilyDao familyDao) {
        return new FamilyRepository(familyDao);
    }

    @Provides
    @Singleton
    FamilyDao provideFamilyDao(LocalDatabase database) {
        return database.familyDao();
    }
}
