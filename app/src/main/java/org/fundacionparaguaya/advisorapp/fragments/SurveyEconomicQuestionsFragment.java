package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

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
        ((AdvisorApplication) getActivity().getApplication())
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

        mSharedSurveyViewModel.getEconomicResponses().observe(this, (responses)->
        {
            checkConditions();
        });

        super.initQuestionList();
    }

    //region Survey Question Callback
    @Override
    public BackgroundQuestion getQuestion(int i) {
        return mSharedSurveyViewModel.getSurveyInProgress().getEconomicQuestions().get(i);
    }

    @Override
    public String getResponse(BackgroundQuestion question) {
        return mSharedSurveyViewModel.getBackgroundResponse(question);
    }

    @Override
    public void onResponse(BackgroundQuestion question, String s) {
        mSharedSurveyViewModel.setBackgroundResponse(question, s);
        checkConditions();
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