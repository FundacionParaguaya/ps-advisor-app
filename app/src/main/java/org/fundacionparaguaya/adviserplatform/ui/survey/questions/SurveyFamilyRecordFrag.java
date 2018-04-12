package org.fundacionparaguaya.adviserplatform.ui.survey.questions;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import org.fundacionparaguaya.adviserplatform.AdviserApplication;
import org.fundacionparaguaya.adviserplatform.R;
import org.fundacionparaguaya.adviserplatform.data.model.BackgroundQuestion;
import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.adviserplatform.ui.survey.SharedSurveyViewModel;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class SurveyFamilyRecordFrag extends SurveyQuestionsFrag {

    @Inject
    protected InjectionViewModelFactory mViewModelFactory;

    private SharedSurveyViewModel mSharedSurveyViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        ((AdviserApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSharedSurveyViewModel = ViewModelProviders
                .of(getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        setTitle(getString(R.string.survey_newfamily));
        setShowFooter(false);

        super.onCreate(savedInstanceState);

        mSharedSurveyViewModel.CurrentFamily().observe(this, family -> {
            if(family!=null) mQuestionAdapter.hideQuestions();
        });
    }

    @Override
    protected void initQuestionList() {
        mSharedSurveyViewModel.getPersonalResponses().observe(this, this);

        super.initQuestionList();
    }

    //region Background Question Callbacks
    @Override
    public BackgroundQuestion getQuestion(int i) {
        return mSharedSurveyViewModel.getBackgroundQuestion(BackgroundQuestion.QuestionType.PERSONAL, i);
    }

    @Override
    public String getResponse(BackgroundQuestion question) {
        return mSharedSurveyViewModel.getBackgroundResponse(question);
    }

    @Override
    public void onResponse(BackgroundQuestion question, String s) {
        mSharedSurveyViewModel.setBackgroundResponse(question, s);
        updateRequirementsSatisfied();
    }
    //endregion

    //region Review Page Callbacks
    @Override
    public List<BackgroundQuestion> getQuestions()
    {
        return mSharedSurveyViewModel.getSelectedSurvey().getPersonalQuestions();
    }

    @Override
    public LiveData<Map<BackgroundQuestion, String>> getResponses() {
        return mSharedSurveyViewModel.getPersonalResponses();
    }

    @Override
    public void onSubmit() {
        mSharedSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.ECONOMIC_QUESTIONS);
    }
    //endregion
}
