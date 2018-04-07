package org.fundacionparaguaya.adviserplatform.ui.survey.questions;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import org.fundacionparaguaya.adviserplatform.AdviserApplication;
import org.fundacionparaguaya.adviserplatform.R;
import org.fundacionparaguaya.adviserplatform.data.model.BackgroundQuestion;
import org.fundacionparaguaya.adviserplatform.data.model.ResponseType;
import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.adviserplatform.ui.survey.SharedSurveyViewModel;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Fragment that displays economic questions
 */

public class SurveyEconomicQuestionsFragment extends SurveyQuestionsFrag {

    @Inject protected InjectionViewModelFactory mViewModelFactory;

    private SharedSurveyViewModel mSharedSurveyViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((AdviserApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSharedSurveyViewModel = ViewModelProviders.
                of(getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        setTitle(getString(R.string.surveyquestions_economic_title));

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initQuestionList() {

        mSharedSurveyViewModel.getEconomicResponses().observe(this, this);
        super.initQuestionList();
    }

    //region Survey Question Callback
    @Override
    public BackgroundQuestion getQuestion(int i) {
        return mSharedSurveyViewModel.getBackgroundQuestion(BackgroundQuestion.QuestionType.ECONOMIC, i);
    }

    @Override
    public String getResponse(BackgroundQuestion question) {
        return mSharedSurveyViewModel.getBackgroundResponse(question);
    }

    @Override
    public void onResponse(BackgroundQuestion question, String s) {
        mSharedSurveyViewModel.setBackgroundResponse(question, s);

        if(question.getResponseType() == ResponseType.LOCATION)
        {
            onNext(null);
        }

        updateRequirementsSatisfied();
    }
    //endregion

    //region Review Page Callback
    @Override
    public List<BackgroundQuestion> getQuestions()
    {
        return mSharedSurveyViewModel.getSurveyInProgress().getEconomicQuestions();
    }

    @Override
    public LiveData<Map<BackgroundQuestion, String>> getResponses() {
        return mSharedSurveyViewModel.getEconomicResponses();
    }

    @Override
    public void onSubmit() {
        mSharedSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.INDICATORS);
    }
    //endregion
}