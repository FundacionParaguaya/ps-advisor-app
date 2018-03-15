package org.fundacionparaguaya.advisorapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.repositories.SurveyRepository;
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

    EditPriorityViewModel(SurveyRepository repository) {
        this.repo = repository;
    }

    public final String getReason() {
        return this.mReason;
    }

    public final void setReason(String mReason) {
        this.mReason = mReason;
    }

    public final String getAction() {
        return this.mAction;
    }

    public final void setAction(String value) {
        this.mAction = value;
    }

    public final Date getCompletionDate() {
        return this.mCompletionDate;
    }

    public final void setCompletionDate(Date value) {
        this.mCompletionDate = value;
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

    public void setNumMonths(int i) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, i);
        this.mCompletionDate = cal.getTime();
    }

    public int getMonthsUntilCompletion()
    {
        DateTime start = new DateTime();
        DateTime end = new DateTime(mCompletionDate);

        return Months.monthsBetween(start, end).getMonths() + 1;
    }
}
