package org.fundacionparaguaya.advisorapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.fundacionparaguaya.advisorapp.fragments.ChooseIndicatorFragment;
import org.fundacionparaguaya.advisorapp.fragments.SurveyIndicatorsFragment;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for the indicator asked during a survey
 */

public class SurveyIndicatorAdapter extends FragmentStatePagerAdapter {

    private List<IndicatorQuestion> indicatorQuestionList;

    SharedSurveyViewModel mSurveyViewModel;
    SurveyIndicatorsFragment mSurveyFragment;
    public SurveyIndicatorAdapter(FragmentManager fragmentManager, SharedSurveyViewModel surveyViewModel) {
        super(fragmentManager);

        mSurveyViewModel = surveyViewModel;

        indicatorQuestionList = mSurveyViewModel.getSurveyInProgress().getIndicatorQuestions();
    }

    @Override
    public int getCount() {
        if(indicatorQuestionList==null) return 0;
        else
        {
            return indicatorQuestionList.size();
        }
    }

    @Override
    public Fragment getItem(int position) {
        return ChooseIndicatorFragment.newInstance(this, indicatorQuestionList.get(position));

    }

    public IndicatorQuestion getQuestion(int position){
        return indicatorQuestionList.get(position);
    }

}