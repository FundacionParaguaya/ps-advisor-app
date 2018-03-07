package org.fundacionparaguaya.advisorapp.data.local;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.models.Survey;

/**
 * The database storing a local cache of data for the user.
 */
@Database(entities = {Family.class, Survey.class, Snapshot.class}, version = 3)
public abstract class LocalDatabase extends RoomDatabase {

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE snapshots "
                    + " ADD COLUMN in_progress INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static final Migration[] MIGRATIONS = new Migration[] {
            MIGRATION_2_3
    };

    public abstract FamilyDao familyDao();
    public abstract SurveyDao surveyDao();
    public abstract SnapshotDao snapshotDao();
}
