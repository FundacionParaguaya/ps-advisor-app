package org.fundacionparaguaya.advisorapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import org.fundacionparaguaya.advisorapp.fragments.ChooseIndicatorFragment;
import org.fundacionparaguaya.advisorapp.fragments.SurveyIndicatorsSummary;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;

import java.util.List;

/**
 * Adapter class for the indicator asked during a survey
 */
public class SurveyIndicatorAdapter extends FragmentStatePagerAdapter {

    private List<IndicatorQuestion> indicatorQuestionList;

    public SurveyIndicatorAdapter(FragmentManager fragmentManager, List<IndicatorQuestion> questionList) {
        super(fragmentManager);

        indicatorQuestionList = questionList;
    }

    @Override
    public int getCount() {
        if(indicatorQuestionList==null) {
            return 0;
        } else {
            return indicatorQuestionList.size()+1;
        }
    }

    @Override
    public Fragment getItem(int position) {
        if(position<indicatorQuestionList.size())
        {
            return ChooseIndicatorFragment.build(position);
        }
        else
        {
            return new SurveyIndicatorsSummary();
        }
    }

    public IndicatorQuestion getQuestion(int position){
        return indicatorQuestionList.get(position);
    }

}
