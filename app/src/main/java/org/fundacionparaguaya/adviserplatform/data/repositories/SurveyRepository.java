package org.fundacionparaguaya.adviserplatform.data.repositories;

import android.arch.lifecycle.LiveData;
import android.support.annotation.Nullable;

import org.fundacionparaguaya.adviserplatform.R;
import org.fundacionparaguaya.adviserplatform.data.local.SurveyDao;
import org.fundacionparaguaya.adviserplatform.data.remote.SurveyService;
import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.IrMapper;
import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.SurveyIr;
import org.fundacionparaguaya.adviserplatform.data.model.Survey;
import org.fundacionparaguaya.adviserplatform.util.AppConstants;

import retrofit2.Response;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;

/**
 * The utility for the storage of surveys and snapshots.
 */

public class SurveyRepository extends BaseRepository{
    private static final String TAG = "SurveyRepository";

    private final SurveyDao surveyDao;
    private final SurveyService surveyService;

    @Inject
    public SurveyRepository(SurveyDao surveyDao,
                            SurveyService surveyService) {
        this.surveyDao = surveyDao;
        this.surveyService = surveyService;
        setPreferenceKey(String.format("%s-%s", AppConstants.KEY_LAST_SYNC_TIME, TAG));
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
        Date lastSync = getLastSyncDate() > 0 ? new Date(getLastSyncDate()) : null;
        long loopCount = 0;

        try {
            if(shouldAbortSync()) return false;

            Response<List<SurveyIr>> response;
            if (lastSync != null) {
                String lastSyncString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
                        .format(lastSync);
                response = surveyService.getSurveysModifiedSince(lastSyncString).execute();
            } else {
                response = surveyService.getSurveys().execute();
            }

            if (!response.isSuccessful() || response.body() == null) {
                Timber.tag(TAG);
                Timber.e( format("pullSurveys: Could not pull surveys! %s",
                        response.errorBody().string()));
                return false;
            }

            List<Survey> surveys = IrMapper.mapSurveys(response.body());
            setRecordsCount(surveys.size());
            for (Survey survey : surveys) {
                if(shouldAbortSync()) return false;

                Survey old = surveyDao.queryRemoteSurveyNow(survey.getRemoteId());
                if (old != null) {
                    survey.setId(old.getId());
                }
                saveSurvey(survey);
                if(getDashActivity() != null) {
                    getDashActivity().setSyncLabel(R.string.syncing_surveys, ++loopCount,
                            getRecordsCount());
                }
            }
        } catch (IOException e) {
            Timber.tag(TAG);
            Timber.e("pullSurveys: Could not pull surveys!", e);
            return false;
        }
        return true;
    }
    //endregion

    /**
     * Synchronizes the local surveys with the remote database.
     * @return Whether the sync was successful.
     */
    @Override
    public boolean sync() {
        return pullSurveys();
    }

    public void clean() {
        setmIsAlive(new AtomicBoolean(false));
        surveyDao.deleteAll();
    }
}
