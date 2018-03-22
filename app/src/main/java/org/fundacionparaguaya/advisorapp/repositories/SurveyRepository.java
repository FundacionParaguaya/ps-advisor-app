package org.fundacionparaguaya.advisorapp.repositories;

import android.arch.lifecycle.LiveData;
import android.support.annotation.Nullable;
import android.util.Log;
import org.fundacionparaguaya.advisorapp.data.local.SurveyDao;
import org.fundacionparaguaya.advisorapp.data.remote.SurveyService;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrMapper;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.SurveyIr;
import org.fundacionparaguaya.advisorapp.models.Survey;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;

/**
 * The utility for the storage of surveys and snapshots.
 */

public class SurveyRepository {
    private static final String TAG = "SurveyRepository";

    private final SurveyDao surveyDao;
    private final SurveyService surveyService;

    @Inject
    public SurveyRepository(SurveyDao surveyDao,
                            SurveyService surveyService) {
        this.surveyDao = surveyDao;
        this.surveyService = surveyService;
    }

    //region Survey
    public LiveData<List<Survey>> getSurveys() {
        return surveyDao.querySurveys();
    }

    /**
     * Gets the surveys synchronously.
     */
    public List<Survey> getSurveysNow() {
        return surveyDao.querySurveysNow();
    }

    public LiveData<Survey> getSurvey(int id) {
        return surveyDao.querySurvey(id);
    }

    /**
     * Gets a survey synchronously.
     */
    public Survey getSurveyNow(int id) {
        return surveyDao.querySurveyNow(id);
    }

    private void saveSurvey(Survey survey) {
        long rows = surveyDao.updateSurvey(survey);
        if (rows == 0) { // no row was updated
            surveyDao.insertSurvey(survey);
        }
    }

    private boolean pullSurveys(@Nullable Date lastSync) {
        try {
            Response<List<SurveyIr>> response;
            if (lastSync != null) {
                String lastSyncString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
                        .format(lastSync);
                response = surveyService.getSurveysModifiedSince(lastSyncString).execute();
            } else {
                response = surveyService.getSurveys().execute();
            }

            if (!response.isSuccessful() || response.body() == null) {
                Log.w(TAG, format("pullSurveys: Could not pull surveys! %s", response.errorBody().string()));
                return false;
            }

            List<Survey> surveys = IrMapper.mapSurveys(response.body());
            for (Survey survey : surveys) {
                Survey old = surveyDao.queryRemoteSurveyNow(survey.getRemoteId());
                if (old != null) {
                    survey.setId(old.getId());
                }
                saveSurvey(survey);
            }
        } catch (IOException e) {
            Log.e(TAG, "pullSurveys: Could not pull surveys!", e);
            return false;
        }
        return true;
    }
    //endregion

    /**
     * Synchronizes the local surveys with the remote database.
     * @return Whether the sync was successful.
     */
    boolean sync(@Nullable Date lastSync) {
        return pullSurveys(lastSync);
    }

    void clean() {
        surveyDao.deleteAll();
    }
}
