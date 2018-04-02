package org.fundacionparaguaya.advisorapp.ui.survey.priorities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import org.fundacionparaguaya.advisorapp.data.model.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.data.repositories.SurveyRepository;
import org.jcodec.common.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Months;

import java.util.Calendar;
import java.util.Date;

public final class EditPriorityViewModel extends ViewModel {
    private SurveyRepository repo;
    private LiveData<IndicatorQuestion> mIndicator = null;

    private String mReason;
    private String mAction;
    private Date mCompletionDate;
    private boolean hasWhenBeenSeen = false;

    private final MutableLiveData<Integer> mQuestionsUnanswered = new MutableLiveData<>();

    public EditPriorityViewModel(SurveyRepository repository) {
        this.repo = repository;
        updateUnansweredCount();
    }

    public final String getReason() {
        return this.mReason;
    }

    public final void setReason(String mReason) {
        this.mReason = mReason;
        updateUnansweredCount();
    }

    public final String getAction() {
        return this.mAction;
    }

    public final void setAction(String value) {
        this.mAction = value;
        updateUnansweredCount();
    }

    public final Date getCompletionDate() {
        return this.mCompletionDate;
    }

    public final void setCompletionDate(Date value) {
        this.mCompletionDate = value;
        updateUnansweredCount();
    }

    public final void setIndicator(int surveyId, int indicatorIndex) {
        mIndicator = Transformations.map(repo.getSurvey(surveyId), value -> value.getIndicatorQuestions().get(indicatorIndex));
    }

    public final LiveData<IndicatorQuestion> getIndicator() {
        if(mIndicator != null)
        {
            return mIndicator;
        }

        throw new IllegalStateException("getIndicator called before surveyId and IndicatorIndex set");
    }

    boolean areRequirementsMet()
    {
        return hasWhenBeenSeen && mCompletionDate!=null &&
                !StringUtils.isEmpty(mReason) &&
                !StringUtils.isEmpty(mAction);
    }

    private void updateUnansweredCount()
    {
        int unanswered = 0;

        if(hasWhenBeenSeen || mCompletionDate!=null) unanswered++;

        if(StringUtils.isEmpty(mReason)) unanswered++;

        if(StringUtils.isEmpty(mAction)) unanswered++;

        mQuestionsUnanswered.setValue(unanswered);
    }

    public LiveData<Integer> NumberOfQuestionsUnanswered()
    {
        return mQuestionsUnanswered;
    }

    /**
     * Whether or not the month stepper has been seen -- it starts out w a default value so we can't just
     * do it based off whether or not we have a value.
     */
    void setWhenSeen()
    {
        hasWhenBeenSeen = true;
    }

    void setNumMonths(int i) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, i);
        this.mCompletionDate = cal.getTime();
        updateUnansweredCount();
    }

    int getMonthsUntilCompletion()
    {
        DateTime start = new DateTime();
        DateTime end = new DateTime(mCompletionDate);

        return Months.monthsBetween(start, end).getMonths() + 1;
    }
}
