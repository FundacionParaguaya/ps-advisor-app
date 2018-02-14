package org.fundacionparaguaya.advisorapp.viewmodels;

import android.arch.lifecycle.*;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import org.fundacionparaguaya.advisorapp.models.*;
import org.fundacionparaguaya.advisorapp.repositories.FamilyRepository;
import org.fundacionparaguaya.advisorapp.repositories.SnapshotRepository;
import org.fundacionparaguaya.advisorapp.repositories.SurveyRepository;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Survey view model that is shared across all of the fragments/activity related to the view model
 */

public class SharedSurveyViewModel extends ViewModel
{
    public enum SurveyState {NONE, NEW_FAMILY, INTRO, ECONOMIC_QUESTIONS, INDICATORS, SUMMARY, REVIEWINDICATORS, REVIEWBACKGROUND, LIFEMAP, COMPLETE}

    static String NO_SNAPSHOT_EXCEPTION_MESSAGE = "Method call requires an existing snapshot, but no snapshot has been created. (Call" +
            "makeSnapshot before this function";

    SurveyRepository mSurveyRepository;
    SnapshotRepository mSnapshotRespository;
    FamilyRepository mFamilyRepository;

    MutableLiveData<SurveyProgress> mProgress = new MutableLiveData<SurveyProgress>();
    private MutableLiveData<SurveyState> mSurveyState;

    Set<IndicatorQuestion> mSkippedIndicators;
    MutableLiveData<Snapshot> mSnapshot;
    LiveData<Family> mFamily;

    Survey mSurvey;
    IndicatorQuestion focusedQuestion;

    private final MutableLiveData<List<LifeMapPriority>> mPriorities;
    private final MutableLiveData<Collection<IndicatorOption>> mIndicatorResponses;

    private int mSurveyId;
    private int mFamilyId;

    public SharedSurveyViewModel(SnapshotRepository snapshotRepository, SurveyRepository surveyRepository, FamilyRepository familyRepository) {
        super();

        mSurveyRepository = surveyRepository;
        mFamilyRepository = familyRepository;
        mSnapshotRespository = snapshotRepository;

        mFamily = new MutableLiveData<>();

        mSurveyState = new MutableLiveData<>();

        mSurveyState.setValue(SurveyState.NONE);
        mSnapshot = new MutableLiveData<Snapshot>();

        mSkippedIndicators = new HashSet<>();

        mPriorities = new MutableLiveData<>();
        mIndicatorResponses = new MutableLiveData<>();
    }

    public LiveData<Family> getCurrentFamily()
    {
        return mFamily;
    }

    public void saveSnapshotAsync()
    {
        SaveAsyncTask task = new SaveAsyncTask(this);
        task.execute();
    }
    private void saveSnapshot()
    {
        Snapshot snapshot = mSnapshot.getValue();
        if (snapshot == null) {
            throw new IllegalStateException("saveSnapshot was called, but there is no snapshot to be saved.");
        }
        mSnapshotRespository.saveSnapshot(snapshot);

        setFamily(snapshot.getFamilyId()); // update the family, in case a new one was created
    }
    /**
     * Sets the family that is taking the survey
     *
     * @param familyId Id of the family taking the survey
     */
    public void setFamily(int familyId)
    {
        mFamilyId = familyId;
        mFamily = mFamilyRepository.getFamily(familyId);
    }

    /**
     * Makes a new snapshot based on the family set and the survey provided.
     *
     * Assumes that family live data object .getValue is not null
     * We should wait for this before proceeding from the start screen to the next screen
     *
     */
    public void makeSnapshot(Survey survey)
    {
        mSurvey = survey;
        Snapshot snapshot = new Snapshot(mFamily.getValue(), mSurvey);

        mSnapshot.setValue(snapshot);

        mPriorities.setValue(snapshot.getPriorities());
        mIndicatorResponses.setValue(snapshot.getIndicatorResponses().values());
    }

    public LiveData<Snapshot> getSnapshot()
    {
        return mSnapshot;
    }

    public Survey getSurveyInProgress()
    {
        return mSurvey;
    }

    public boolean isRequirementMet(BackgroundQuestion q)
    {
        String response = getBackgroundResponse(q);

        boolean requirementsMet = !(q.isRequired() && (response == null || response.isEmpty()));
        return requirementsMet;
    }

    /**
     * Returns the surveys available to take.
     */
    public LiveData<List<Survey>> getSurveys()
    {
        return mSurveyRepository.getSurveys();
    }

    public MutableLiveData<SurveyState> getSurveyState()
    {
        if(mSurveyState==null)
        {
            mSurveyState = new MutableLiveData<SurveyState>();
        }

        return mSurveyState;
    }

    public void setSurveyState(SurveyState state)
    {
        mSurveyState.setValue(state);

        calculateProgress();
    }

    public LiveData<List<LifeMapPriority>> getPriorities()
    {
        return mPriorities;
    }

    public void addPriority(LifeMapPriority p)
    {
        getSnapshotValue().getPriorities().add(p);
        mPriorities.setValue(getSnapshotValue().getPriorities());
    }

    public void removePriority(LifeMapPriority p)
    {
        getSnapshotValue().getPriorities().remove(p);
        mPriorities.setValue(getSnapshotValue().getPriorities());
    }


    public LiveData<Collection<IndicatorOption>> getIndicatorResponses() {
        return mIndicatorResponses;
    }

    public void setFocusedQuestion(String name){
        for (IndicatorQuestion question:mSkippedIndicators){
            if (question.getName().equals(name)){
                setFocusedQuestion(question);
                break;
            }
        }
    }

    public boolean hasFamily()
    {
        return mFamilyId != -1;
    }
    public void setFocusedQuestion(IndicatorQuestion question){
        focusedQuestion = question;
    }

    public IndicatorQuestion getFocusedQuestion(){
        return focusedQuestion;
    }

    public MutableLiveData<SurveyProgress> getProgress()
    {
        return mProgress;
    }

    public void addSkippedIndicator(IndicatorQuestion question) {
        //clears any responses for the question
        getSnapshotValue().getIndicatorResponses().remove(question);
        mSkippedIndicators.add(question);

        updateIndicatorLiveData();
    }

    public Set<IndicatorQuestion> getSkippedIndicators()
    {
        return mSkippedIndicators;
    }

    public @Nullable IndicatorOption getResponseForIndicator(IndicatorQuestion question)
    {
        return getSnapshotValue().getIndicatorResponses().get(question);

    }

    public void addIndicatorResponse(IndicatorQuestion indicator, IndicatorOption response)
    {
        if(response!=null) {
            if(mSkippedIndicators.contains(indicator))
            {
                mSkippedIndicators.remove(indicator);
            }

            getSnapshotValue().response(indicator, response);
            updateIndicatorLiveData();
        }
    }

    public void removeIndicatorResponse(IndicatorQuestion question){
        getSnapshotValue().getIndicatorResponses().remove(question);
        updateIndicatorLiveData();
    }

    public void addBackgroundResponse(BackgroundQuestion question, String response)
    {
        //TODO if string is empty, we probably want to remove any response that we used to have...?
        if(response!=null && !response.isEmpty()) {
            getSnapshotValue().response(question, response);
            calculateProgress();
        }
    }

    public @Nullable String getBackgroundResponse(BackgroundQuestion question)
    {
        if(question!=null) {
            return getSnapshotValue().getBackgroundResponse(question);
        }
        else
        {
            Log.e(this.getClass().getName(), "Tried to getBackgroundResponse for a null question.");
            return null;
        }
    }

    /**
     * Should be called every time an indicator is added, skipped, or removed. It updates the live data
     * object or indicator responses and calculates progress.
     */
    public void updateIndicatorLiveData()
    {
        mIndicatorResponses.setValue(getSnapshotValue().getIndicatorResponses().values());
        calculateProgress();
    }

    public void calculateProgress()
    {
        if(getSurveyInProgress() != null && mSurveyState.getValue() != null)
        {
            int progress = 0;
            String progressString = "";

            int remainingQuestions = 0;
            int skippedQuestions = 0;

            switch (mSurveyState.getValue())
            {

                case ECONOMIC_QUESTIONS:

                    int totalQuestions = getSurveyInProgress().getEconomicQuestions().size() +
                            getSurveyInProgress().getPersonalQuestions().size();

                    int completedQuestions = mSnapshot.getValue().getPersonalResponses().size() +
                            mSnapshot.getValue().getEconomicResponses().size();

                    progress = (100*completedQuestions)/totalQuestions;

                    remainingQuestions = totalQuestions - completedQuestions;
                    skippedQuestions = -1;

                    break;

                case INDICATORS:
                    int totalIndicators = getSurveyInProgress().getIndicatorQuestions().size();

                    int skippedIndicators = mSkippedIndicators.size();
                    int completedIndicators = mSnapshot.getValue().getIndicatorResponses().size();

                    skippedQuestions = skippedIndicators;
                    remainingQuestions = totalIndicators - (completedIndicators + skippedIndicators);

                    progress = (100* (completedIndicators + skippedIndicators))/totalIndicators;


            }

            mProgress.setValue(new SurveyProgress(progress, remainingQuestions, skippedQuestions));
        }
    }

    /**
     * Essentially "unwraps" the Snapshot live data and retrieves the value. If the value is null, it throws
     * an illegal state exception
     *
     * @return Snapshot in process
     * @throws IllegalStateException
     */
        private @NonNull Snapshot getSnapshotValue()
        {
            Snapshot value = mSnapshot.getValue();

            if(value == null)
            {
                throw new IllegalStateException(NO_SNAPSHOT_EXCEPTION_MESSAGE);
            }
            else return value;
    }

    public static class SurveyProgress
    {
        String mProgressDescription;
        int mPercentageComplete;

        int mQuestionsRemaining;
        int mQuestionsSkipped;

        SurveyProgress(int percentage, int remaining, int skipped)
        {
            mPercentageComplete = percentage;
            mQuestionsSkipped = skipped;
            mQuestionsRemaining = remaining;
        }

        public int getSkipped(){
            return mQuestionsSkipped;
        }

        public int getRemaining(){
            return mQuestionsRemaining;
        }

        void setDescription(String description)
        {
            mProgressDescription = description;
        }

        void setPercentageComplete(int percentage)
        {
            mPercentageComplete = percentage;
        }

        public String getDescription()
        {
            return mProgressDescription;
        }

        public int getPercentageComplete()
        {
            return mPercentageComplete;
        }
    }

    static class SaveAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private WeakReference<SharedSurveyViewModel> viewModelReference;

        SaveAsyncTask(SharedSurveyViewModel viewModel)
        {
            viewModelReference = new WeakReference<SharedSurveyViewModel>(viewModel);
        }

        @Override
        protected Void doInBackground (Void ... voids) {
            viewModelReference.get().saveSnapshot();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            viewModelReference.get().setSurveyState(SurveyState.COMPLETE);
        }
    }

}
