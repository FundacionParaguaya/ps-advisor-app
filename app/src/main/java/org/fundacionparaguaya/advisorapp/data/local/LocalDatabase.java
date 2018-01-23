package org.fundacionparaguaya.advisorapp.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.models.Survey;

/**
 * The database storing a local cache of data for the user.
 */
@Database(entities = {Family.class, Survey.class, Snapshot.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {

    public abstract FamilyDao familyDao();

    public abstract SurveyDao surveyDao();

    public abstract SnapshotDao snapshotDao();
}
