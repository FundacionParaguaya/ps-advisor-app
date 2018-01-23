package org.fundacionparaguaya.advisorapp.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.fundacionparaguaya.advisorapp.models.Snapshot;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * The access utility for retrieving snapshots from the local database.
 */

@Dao
public interface SnapshotDao {
    @Query("SELECT * FROM snapshots WHERE survey_id = :surveyId")
    LiveData<List<Snapshot>> querySnapshots(int surveyId);

    @Query("SELECT * FROM snapshots WHERE family_id = :familyId")
    LiveData<List<Snapshot>> querySnapshotsForFamily(int familyId);

    @Query("SELECT * FROM snapshots WHERE family_id = :familyId AND survey_id = :surveyId")
    LiveData<List<Snapshot>> querySnapshotsForFamily(int familyId, int surveyId);

    @Query("SELECT * FROM snapshots WHERE id = :id")
    LiveData<Snapshot> querySnapshot(int id);

    @Insert(onConflict = REPLACE)
    long insertSnapshot(Snapshot snapshot);

    @Insert(onConflict = REPLACE)
    void insertSnapshots(Snapshot... snapshots);

    @Update
    int updateSnapshot(Snapshot snapshot);

    @Delete
    int deleteSnapshot(Snapshot snapshot);
}
