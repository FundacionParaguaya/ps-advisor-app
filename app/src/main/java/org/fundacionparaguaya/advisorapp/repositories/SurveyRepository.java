package org.fundacionparaguaya.advisorapp.repositories;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import org.fundacionparaguaya.advisorapp.data.local.SurveyDao;
import org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager;
import org.fundacionparaguaya.advisorapp.data.remote.SurveyService;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrMapper;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.SurveyIr;
import org.fundacionparaguaya.advisorapp.models.Survey;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;

import static java.lang.String.format;

/**
 * The utility for the storage of surveys and snapshots.
 */

public class SurveyRepository {
    private static final String TAG = "SurveyRepository";

    private final SurveyDao surveyDao;
    private final SurveyService surveyService;
    private final AuthenticationManager authManager;

    @Inject
    public SurveyRepository(SurveyDao surveyDao,
                            SurveyService surveyService,
                            AuthenticationManager authManager) {
        this.surveyDao = surveyDao;
        this.surveyService = surveyService;
        this.authManager = authManager;
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

    private boolean pullSurveys() {
        try {
            Response<List<SurveyIr>> response =
                    surveyService.getSurveys(authManager.getAuthenticationString()).execute();

            if (!response.isSuccessful() || response.body() == null) {
                Log.w(TAG, format("pullSurveys: Could not pull surveys! %s", response.errorBody()));
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
    boolean sync() {
        return pullSurveys();
    }
}
