package org.fundacionparaguaya.advisorapp.data.local;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.fundacionparaguaya.advisorapp.models.Survey;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.FAIL;

/**
 * The access utility for retrieving surveys from the local database.
 */
@Dao
public interface SurveyDao {
    @Query("SELECT * FROM surveys")
    LiveData<List<Survey>> querySurveys();

    @Query("SELECT * FROM surveys")
    List<Survey> querySurveysNow();

    @Query("SELECT * FROM surveys WHERE id = :id")
    LiveData<Survey> querySurvey(int id);

    @Query("SELECT * FROM surveys WHERE id = :id")
    Survey querySurveyNow(int id);

    @Query("SELECT * FROM surveys WHERE remote_id = :remoteId")
    Survey queryRemoteSurveyNow(long remoteId);

    @Insert(onConflict = FAIL)
    long insertSurvey(Survey survey);

    @Update
    int updateSurvey(Survey survey);

    @Query("DELETE FROM surveys")
    void deleteAll();
}
