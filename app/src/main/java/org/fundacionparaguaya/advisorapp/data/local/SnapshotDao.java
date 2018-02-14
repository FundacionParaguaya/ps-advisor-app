package org.fundacionparaguaya.advisorapp.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.fundacionparaguaya.advisorapp.models.Snapshot;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.FAIL;

/**
 * The access utility for retrieving snapshots from the local database.
 */

@Dao
public interface SnapshotDao {
    @Query("SELECT * FROM snapshots WHERE survey_id = :surveyId")
    LiveData<List<Snapshot>> querySnapshots(int surveyId);

    @Query("SELECT * FROM snapshots WHERE survey_id = :surveyId")
    List<Snapshot> querySnapshotsNow(int surveyId);

    @Query("SELECT * FROM snapshots WHERE family_id = :familyId")
    LiveData<List<Snapshot>> querySnapshotsForFamily(int familyId);

    @Query("SELECT * FROM snapshots WHERE family_id = :familyId AND survey_id = :surveyId")
    LiveData<List<Snapshot>> querySnapshotsForFamily(int familyId, int surveyId);

    @Query("SELECT * FROM snapshots WHERE id = :id")
    LiveData<Snapshot> querySnapshot(int id);

    /**
     * Queries for all snapshots that only exist locally, which haven't been pushed to the
     * remote database and do not have a remote ID.
     * @return The pending snapshots.
     */
    @Query("SELECT * FROM snapshots WHERE remote_id IS NULL")
    List<Snapshot> queryPendingSnapshots();


    @Query("SELECT * FROM snapshots WHERE remote_id = :remoteId")
    Snapshot queryRemoteSnapshotNow(long remoteId);

    @Insert(onConflict = FAIL)
    long insertSnapshot(Snapshot snapshot);

    @Update
    int updateSnapshot(Snapshot snapshot);

    @Query("DELETE FROM snapshots")
    int deleteAll();
}
