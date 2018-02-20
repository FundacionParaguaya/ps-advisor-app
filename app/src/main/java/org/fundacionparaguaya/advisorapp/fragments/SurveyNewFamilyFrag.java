package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;
import org.fundacionparaguaya.advisorapp.models.Survey;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

import javax.inject.Inject;

public class SurveyNewFamilyFrag extends SurveyQuestionsFrag {

    @Inject
    protected InjectionViewModelFactory mViewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSharedSurveyViewModel = ViewModelProviders
                .of(getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        setTitle(getString(R.string.addfamily_new_family_title));
        setShowFooter(false);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initQuestionList() {
        mSharedSurveyViewModel.getSurveys().observe(this, (surveys) ->
        {
            if (surveys != null && surveys.size() > 0) {
                Survey survey = surveys.get(0);

                mSharedSurveyViewModel.makeSnapshot(survey); //assumes family livedata object has value

                mQuestions = mSharedSurveyViewModel.getSurveyInProgress().getPersonalQuestions();
            }

            mSharedSurveyViewModel.getPersonalResponses().observe(this, mSurveyReviewAdapter::setResponses);

            super.initQuestionList();
        });
    }

    @Override
    public void onSubmit() {
        mSharedSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.ECONOMIC_QUESTIONS);
        //set family in survey view model..
        //change state
    }

    @Override
    public String getResponseFor(BackgroundQuestion q) {
        return mSharedSurveyViewModel.getBackgroundResponse(q);
    }
}
