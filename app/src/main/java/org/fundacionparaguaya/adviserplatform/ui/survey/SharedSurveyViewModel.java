package org.fundacionparaguaya.adviserplatform.ui.survey;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import org.fundacionparaguaya.adviserplatform.data.model.*;
import org.fundacionparaguaya.adviserplatform.data.repositories.FamilyRepository;
import org.fundacionparaguaya.adviserplatform.data.repositories.SnapshotRepository;
import org.fundacionparaguaya.adviserplatform.data.repositories.SurveyRepository;
import org.fundacionparaguaya.adviserplatform.jobs.SyncJob;
import org.fundacionparaguaya.adviserplatform.util.IndicatorUtilities;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Survey view model that is shared across all of the fragments/activity related to the view model
 */

public class SharedSurveyViewModel extends ViewModel {
    public enum SurveyState {NONE, INTRO, BACKGROUND, ECONOMIC_QUESTIONS, INDICATORS, LIFEMAP, COMPLETE}

    static String NO_SNAPSHOT_EXCEPTION_MESSAGE = "Method call requires an existing snapshot, but no snapshot has been created. (Call" +
            "makeSnapshot before this function";

    SurveyRepository mSurveyRepository;
    SnapshotRepository mSnapshotRespository;
    FamilyRepository mFamilyRepository;

    MutableLiveData<SurveyProgress> mProgress = new MutableLiveData<SurveyProgress>();
    private MutableLiveData<SurveyState> mSurveyState;
    private MediatorLiveData<Snapshot> mPendingSnapshot;
    private LiveData<Snapshot> mPendingSnapshotSource;

    Set<IndicatorQuestion> mSkippedIndicators;
    MutableLiveData<Snapshot> mSnapshot;
    LiveData<Family> mFamily;

    private final MutableLiveData<List<LifeMapPriority>> mPriorities;
    private final MutableLiveData<Map<IndicatorQuestion, IndicatorOption>> mIndicatorResponses;
    private final MutableLiveData<Map<BackgroundQuestion, String>> mEconomicResponses;
    private final MutableLiveData<Map<BackgroundQuestion, String>> mPersonalResponses;
    private final MutableLiveData<Survey> mSurvey = new MutableLiveData<>();
    private final MutableLiveData<IndicatorQuestion> mFocusedQuestion = new MutableLiveData<>();

    public final static int DESIRED_NUM_PRIORITIES = 5;

    private int mFamilyId;

    public SharedSurveyViewModel(SnapshotRepository snapshotRepository, SurveyRepository surveyRepository, FamilyRepository familyRepository) {
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
        mEconomicResponses = new MutableLiveData<>();
        mPersonalResponses = new MutableLiveData<>();

        mPendingSnapshot = new MediatorLiveData<>();

        mSurvey.setValue(null);
    }

    public LiveData<Family> CurrentFamily() {
        return mFamily;
    }

    public Family getCurrentFamily() {
        return mFamily.getValue();
    }

    public void submitSnapshotAsync() {
        SaveCompleteAsyncTask task = new SaveCompleteAsyncTask(this);
        task.execute();
    }


    /**
     * Sets the family that is taking the survey
     *
     * @param familyId Id of the family taking the survey, or -1 if a new family will be created.
     */
    public void setFamily(int familyId) {
        mFamilyId = familyId;
        mFamily = mFamilyRepository.getFamily(familyId);

        updatePendingSnapshotsSource();
    }

    private void updatePendingSnapshotsSource() {
        if (mPendingSnapshotSource != null) {
            mPendingSnapshot.removeSource(mPendingSnapshotSource);
        }
        mPendingSnapshotSource = mSnapshotRespository
                .getPendingSnapshot(mFamilyId != -1 ? mFamilyId : null);
        mPendingSnapshot.addSource(mPendingSnapshotSource, mPendingSnapshot::setValue);
    }

    public LiveData<Snapshot> getPendingSnapshot() {
        return mPendingSnapshot;
    }

    public void resumeSnapshot(Snapshot snapshot, Survey survey, @Nullable Family family) {
        mSnapshot.setValue(snapshot);

        setSnapshot(snapshot);
        setFamily(family != null ? family.getId() : -1);

        SurveyState lastState = (family != null ? SurveyState.ECONOMIC_QUESTIONS : SurveyState.BACKGROUND);

        if(snapshot.getEconomicResponses().size()!=0)
        {
            lastState = SurveyState.ECONOMIC_QUESTIONS;
        }

        if(snapshot.getIndicatorResponses().size()!=0)
        {
            lastState = SurveyState.INDICATORS;
        }

        if(snapshot.getPriorities().size()!=0)
        {
            lastState = SurveyState.LIFEMAP;
        }

        startSurvey(survey);
        setSurveyState(family != null ? lastState : SurveyState.BACKGROUND);
    }

    /**
     * Makes a new snapshot based on the family set and the survey provided.
     * <p>
     * Assumes that family live data object .getValue is not null
     * We should wait for this before proceeding from the start screen to the next screen
     */
    public void makeSnapshot(Survey survey) {
        Snapshot s= new Snapshot(mFamily.getValue(), survey);

        if(mFamily.getValue()!=null) setSurveyState(SurveyState.ECONOMIC_QUESTIONS);
        else setSurveyState(SurveyState.BACKGROUND);

        setSnapshot(s);
        startSurvey(survey);
    }

    /**
     * Whether or not the survey in progress is resurveying an existing family. Can't be called until
     * the survey activity has either set (or not set a family)
     *
     * @return whether or not we are resurveying.
     */
    public boolean isResurvey()
    {
        return mFamily.getValue() == null;
    }

    private void startSurvey(Survey s)
    {
        mSurvey.setValue(s);
    }

    private void setSnapshot(Snapshot s)
    {
        mSnapshot.setValue(s);

        mPriorities.setValue(s.getPriorities());
        mIndicatorResponses.setValue(s.getIndicatorResponses());
        mPersonalResponses.setValue(s.getPersonalResponses());
        mEconomicResponses.setValue(s.getEconomicResponses());
    }

    public LiveData<Snapshot> getSnapshot() {
        return mSnapshot;
    }

    public Survey getSurveyInProgress() {
        return mSurvey.getValue();
    }

    public LiveData<Survey> SurveyInProgress()
    {
        return mSurvey;
    }

    /**
     * Returns the surveys available to take.
     */
    public LiveData<List<Survey>> getSurveys() {
        return mSurveyRepository.getSurveys();
    }

    public MutableLiveData<SurveyState> getSurveyState() {
        if (mSurveyState == null) {
            mSurveyState = new MutableLiveData<SurveyState>();
        }

        return mSurveyState;
    }

    public void setSurveyState(SurveyState state) {
        if(state!=mSurveyState.getValue())
        {
            mSurveyState.setValue(state);
        }

        calculateProgress();
    }

    public LiveData<List<LifeMapPriority>> getPriorities() {
        return mPriorities;
    }

    public void addPriority(LifeMapPriority p) {
        getSnapshotValue().getPriorities().add(p);
        mPriorities.setValue(getSnapshotValue().getPriorities());

        saveProgress();
    }

    public void updatePriority(LifeMapPriority newPriority)
    {
        for(LifeMapPriority p: getSnapshotValue().getPriorities())
        {
            if(p.getIndicator().equals(newPriority.getIndicator()))
            {
                p.setReason(newPriority.getReason());
                p.setAction(newPriority.getAction());
                p.setEstimatedDate(newPriority.getEstimatedDate());
            }
        }

        mPriorities.setValue(getSnapshotValue().getPriorities());
        saveProgress();
    }

    public boolean hasPriority(Indicator i)
    {
        for(LifeMapPriority p: getSnapshotValue().getPriorities())
        {
            if(p.getIndicator().equals(i)) return true;
        }

        return false;
    }

    public LiveData<Map<IndicatorQuestion, IndicatorOption>> getIndicatorResponses() {
        return mIndicatorResponses;
    }

    public boolean hasFamily() {
        return mFamilyId != -1;
    }

    public void setFocusedIndicator(IndicatorQuestion question) {
         mFocusedQuestion.setValue(question);
    }

    public void setFocusedIndicator(int i) {
        mFocusedQuestion.setValue(getSurveyInProgress().getIndicatorQuestions().get(i));
    }

    public LiveData<IndicatorQuestion> FocusedQuestion(){
        return mFocusedQuestion;
    }

    public MutableLiveData<SurveyProgress> Progress() {
        return mProgress;
    }


    public Set<IndicatorQuestion> getSkippedIndicators() {
        return mSkippedIndicators;
    }

    public @Nullable IndicatorOption getResponseForIndicator(IndicatorQuestion question) {
        return getSnapshotValue().getIndicatorResponses().get(question);
    }

    public @Nullable IndicatorOption getResponseForIndicator(Indicator indicator)
    {
        return IndicatorUtilities.getResponseForIndicator(indicator, getSnapshotValue().getIndicatorResponses());
    }

    public void setIndicatorResponse(int i, IndicatorOption response)
    {
        setIndicatorResponse(getIndicator(i), response);
    }

    public void setIndicatorResponse(IndicatorQuestion indicator, IndicatorOption response) {
        if (response != null) {
            if (mSkippedIndicators.contains(indicator)) {
                mSkippedIndicators.remove(indicator);
            }

            getSnapshotValue().response(indicator, response);
            saveProgress();
        }
        else
        {
            //clears any responses for the question
            getSnapshotValue().getIndicatorResponses().remove(indicator);
            mSkippedIndicators.add(indicator);
        }

        updateIndicatorLiveData();
    }

    public boolean hasResponse(IndicatorQuestion question)
    {
        return getSnapshotValue().getIndicatorResponses().get(question) != null;
    }

    public boolean hasIndicatorResponse(int i)
    {
        return hasResponse(mSurvey.getValue().getIndicatorQuestions().get(i));
    }

    public IndicatorQuestion getIndicator(int i)
    {
        return getSurveyInProgress().getIndicatorQuestions().get(i);
    }

    /**
     * Returns either the economic or personal question at the given index
     */
    public BackgroundQuestion getBackgroundQuestion(BackgroundQuestion.QuestionType questionType, int i)
    {
        if(questionType == BackgroundQuestion.QuestionType.ECONOMIC)
        {
            return getSurveyInProgress().getEconomicQuestions().get(i);
        }
        else return getSurveyInProgress().getPersonalQuestions().get(i);
    }

    public void setBackgroundResponse(BackgroundQuestion question, String response) {
        //TODO if string is empty, we probably want to remove any response that we used to have...?
        if (response != null && !response.isEmpty()) {
            getSnapshotValue().response(question, response);

            if (question.getQuestionType() == BackgroundQuestion.QuestionType.ECONOMIC) {
                mEconomicResponses.setValue(getSnapshotValue().getEconomicResponses());
            } else {
                mPersonalResponses.setValue(getSnapshotValue().getPersonalResponses());
            }
        }
        else
        {
            if(question.getQuestionType() == BackgroundQuestion.QuestionType.ECONOMIC)
            {
                getSnapshotValue().getEconomicResponses().remove(question);
            }
            else getSnapshotValue().getPersonalResponses().remove(question);
        }

        saveProgress();
        calculateProgress();
    }

    public LiveData<Map<BackgroundQuestion, String>> getPersonalResponses() {
        return mPersonalResponses;
    }

    public LiveData<Map<BackgroundQuestion, String>> getEconomicResponses() {
        return mEconomicResponses;
    }

    public @Nullable
    String getBackgroundResponse(BackgroundQuestion question) {
        if (question != null) {
            return getSnapshotValue().getBackgroundResponse(question);
        } else {
            Log.e(this.getClass().getName(), "Tried to getBackgroundResponse for a null question.");
            return null;
        }
    }

    /**
     * Should be called every time an indicator is added, skipped, or removed. It updates the live data
     * object or indicator responses and calculates progress.
     */
    public void updateIndicatorLiveData() {
        mIndicatorResponses.setValue(getSnapshotValue().getIndicatorResponses());
        calculateProgress();
    }

    public void calculateProgress() {
        if (getSurveyInProgress() != null && mSurveyState.getValue() != null) {
            int remainingQuestions = 0;
            int skippedQuestions = 0;

            switch (mSurveyState.getValue()) {

                case BACKGROUND:
                    remainingQuestions = getSurveyInProgress().getPersonalQuestions().size() -
                            mSnapshot.getValue().getPersonalResponses().size();
                    break;

                case ECONOMIC_QUESTIONS:
                    //can't distinguish between skipped/remaining for questions
                    remainingQuestions = getSurveyInProgress().getEconomicQuestions().size() -
                            mSnapshot.getValue().getEconomicResponses().size();
                    break;

                case INDICATORS:
                    int totalIndicators = getSurveyInProgress().getIndicatorQuestions().size();

                    int skippedIndicators = mSkippedIndicators.size();
                    int completedIndicators = mSnapshot.getValue().getIndicatorResponses().size();

                    skippedQuestions = skippedIndicators;
                    remainingQuestions = totalIndicators - (completedIndicators + skippedIndicators);
                    break;

                case LIFEMAP:
                    remainingQuestions = DESIRED_NUM_PRIORITIES - mPriorities.getValue().size();
                    break;


            }

            mProgress.setValue(new SurveyProgress(remainingQuestions, skippedQuestions, mSurveyState.getValue()));
        }
    }

    /**
     * Should be called whenever changes to the snapshot are made
     */
    private void saveProgress() {
        new SaveProgressAsyncTask(this).execute();
        mSnapshot.setValue(mSnapshot.getValue()); //updates observers
    }

    /**
     * Essentially "unwraps" the Snapshot live data and retrieves the value. If the value is null, it throws
     * an illegal state exception
     *
     * @return Snapshot in process
     * @throws IllegalStateException
     */
    private @NonNull Snapshot getSnapshotValue() {
        Snapshot value = mSnapshot.getValue();

        if (value == null) {
            throw new IllegalStateException(NO_SNAPSHOT_EXCEPTION_MESSAGE);
        } else return value;
    }

    public static class SurveyProgress {
        private SurveyState mState;

        private int mQuestionsRemaining;
        private int mQuestionsSkipped;

        SurveyProgress(int remaining, int skipped, SurveyState state) {
            mQuestionsSkipped = skipped;
            mQuestionsRemaining = remaining;
            mState = state;
        }

        public int getSkipped() {
            return mQuestionsSkipped;
        }

        public int getRemaining() {
            return mQuestionsRemaining;
        }

        public SurveyState getState()
        {
            return mState;
        }
    }


    /**
     * Saves a snapshot that is in progress.
     */
    private static class SaveProgressAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<SharedSurveyViewModel> viewModelReference;

        SaveProgressAsyncTask(SharedSurveyViewModel viewModel) {
            viewModelReference = new WeakReference<>(viewModel);
        }

        @Override
        protected Void doInBackground(Void ... voids) {
            SharedSurveyViewModel viewModel = viewModelReference.get();
            if (viewModel == null) {
                return null;
            }

            viewModel.mSnapshotRespository.saveSnapshot(viewModel.getSnapshotValue());
            return null;
        }
    }

    /**
     * Saves a snapshot that has just been completed.
     */
    static class SaveCompleteAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<SharedSurveyViewModel> viewModelReference;

        SaveCompleteAsyncTask(SharedSurveyViewModel viewModel) {
            viewModelReference = new WeakReference<SharedSurveyViewModel>(viewModel);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SharedSurveyViewModel viewModel = viewModelReference.get();
            if (viewModel == null) {
                return null;
            }

            Snapshot snapshot = viewModel.getSnapshotValue();
            snapshot.setInProgress(false);
            viewModel.mSnapshotRespository.saveSnapshot(snapshot);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            SharedSurveyViewModel viewModel = viewModelReference.get();
            if (viewModel == null) {
                return;
            }

            Snapshot snapshot = viewModel.getSnapshotValue();

            if(viewModel.getCurrentFamily() == null)
            {
                viewModel.setFamily(snapshot.getFamilyId());
            }

            SyncJob.sync();

            viewModel.setSurveyState(SurveyState.COMPLETE);
        }
    }
}
