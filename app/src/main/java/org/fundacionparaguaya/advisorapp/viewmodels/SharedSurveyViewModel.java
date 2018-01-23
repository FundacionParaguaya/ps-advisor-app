package org.fundacionparaguaya.advisorapp.viewmodels;

import android.arch.lifecycle.*;
import org.fundacionparaguaya.advisorapp.models.*;
import org.fundacionparaguaya.advisorapp.repositories.FamilyRepository;
import org.fundacionparaguaya.advisorapp.repositories.SurveyRepository;

import java.util.Collection;
import java.util.List;

/**
 * Survey view model that is shared across all of the fragments/activity related to the view model
 */

public class SharedSurveyViewModel extends ViewModel {
    public enum SurveyState {NONE, INTRO, BACKGROUND_QUESTIONS, INDICATORS, REVIEW}

    SurveyRepository mSurveyRepository;
    FamilyRepository mFamilyRepository;

    MutableLiveData<SurveyProgress> mProgress = new MutableLiveData<SurveyProgress>();
    private MutableLiveData<SurveyState> mSurveyState;

    List<IndicatorQuestion> mSkippedIndicators;

    MutableLiveData<Snapshot> mSnapshot;

    LiveData<Family> mFamily;

    Survey mSurvey;

    private int mSurveyId;
    private int mFamilyId;

    public SharedSurveyViewModel(SurveyRepository surveyRepository, FamilyRepository familyRepository) {
        super();

        mSurveyRepository = surveyRepository;
        mFamilyRepository = familyRepository;

        mProgress.setValue(new SurveyProgress());
        mSurveyState = new MutableLiveData<>();

        mSurveyState.setValue(SurveyState.NONE);
        mSnapshot = new MutableLiveData<Snapshot>();
    }

    public LiveData<Family> getCurrentFamily() {
        return mFamily;
    }

    /**
     * Sets the family that is taking the survey
     *
     * @param familyId Id of the family taking the survey
     */
    public void setFamily(int familyId) {
        mFamilyId = familyId;
        mFamily = mFamilyRepository.getFamily(familyId);
    }

    /**
     * Makes a new snapshot based on the family set and the survey provided.
     * <p>
     * Assumes that family live data object .getValue is not null
     * <p>
     * We should wait for this before proceeding from the start screen to the next screen
     */
    public void makeSnapshot(Survey survey) {
        mSurvey = survey;

        mSnapshot.setValue(new Snapshot(mFamily.getValue(), mSurvey));
    }

    public LiveData<Snapshot> getSnapshot() {
        return mSnapshot;
    }

    public Survey getSurveyInProgress() {
        return mSurvey;
    }

    /**
     * Returns the surveys available to take.
     */
    public LiveData<List<Survey>> getSurveys() {
        return mSurveyRepository.getSurveys();
    }

    /**
     * Gets the current snapshot. Snapshot must have been created with "getNewSnapshot"
     * <p>
     * TODO: Remove if this turns out not to be necessary.
     */
    public LiveData<Snapshot> getCurrentSnapshot() {
        return mSnapshot;
    }

    public MutableLiveData<SurveyState> getSurveyState() {
        if (mSurveyState == null) {
            mSurveyState = new MutableLiveData<SurveyState>();
        }

        return mSurveyState;
    }

    public MutableLiveData<SurveyProgress> getProgress() {
        return mProgress;
    }

    public void addSkippedIndicator(IndicatorQuestion question) {
        mSkippedIndicators.add(question);
    }

    public List<IndicatorQuestion> getSkippedIndicators() {
        return mSkippedIndicators;
    }

    public void getResponseForIndicator(IndicatorQuestion question) {
        mSnapshot.getValue().getIndicatorResponses().get(question);
    }

    public void addIndicatorResponse(IndicatorQuestion indicator, IndicatorOption response) {
        mSnapshot.getValue().response(indicator, response);
    }

    public void addBackgroundResponse(SurveyQuestion question, String response) {
        if (question instanceof PersonalQuestion) {
            mSnapshot.getValue().response((PersonalQuestion) question, response);
        }
        else if (question instanceof EconomicQuestion) {
            mSnapshot.getValue().response((EconomicQuestion) question, response);
        }
    }

    public static class SurveyProgress {
        String mProgressDescription;
        int mPercentageComplete;

        void setDescription(String description) {
            mProgressDescription = description;
        }

        void setPercentageComplete(int percentage) {
            mPercentageComplete = percentage;
        }

        String getDescription() {
            return mProgressDescription;
        }

        int getPercentageComplete() {
            return mPercentageComplete;
        }
    }
}
