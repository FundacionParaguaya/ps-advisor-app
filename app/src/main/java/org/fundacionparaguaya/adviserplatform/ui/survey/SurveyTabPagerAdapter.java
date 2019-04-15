package org.fundacionparaguaya.adviserplatform.ui.survey;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;
import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.ui.survey.priorities.SurveyChoosePrioritiesFragment;
import org.fundacionparaguaya.adviserplatform.ui.survey.questions.SurveyEconomicQuestionsFragment;
import org.fundacionparaguaya.adviserplatform.ui.survey.questions.SurveyFamilyRecordFrag;
import org.fundacionparaguaya.adviserplatform.ui.survey.indicators.SurveyIndicatorsFragment;

/**
 * Pager for all of the different "tabs" in a survey (Background, Economic, Indicators, etc.)
 *
 * For use with a {@link com.stepstone.stepper.StepperLayout}
 */
public class SurveyTabPagerAdapter extends AbstractFragmentStepAdapter {

    public SurveyTabPagerAdapter(FragmentManager fm, Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        switch (position)
        {
            case 0:
                return new SurveyFamilyRecordFrag();
            case 1:
                return new SurveyEconomicQuestionsFragment();
            case 2:
                return new SurveyIndicatorsFragment();
            case 3:
                return new SurveyChoosePrioritiesFragment();
            default:
                throw new IndexOutOfBoundsException("Got index " + position + ", expected index less than 4");
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types

        String title;

        switch (position)
        {
            case 0:
                //todo update this with "Background Quesitons"
                title = context.getResources().getString(R.string.survey_summary_backgroundtitle);
                break;

            case 1:
                title = context.getResources().getString(R.string.surveyquestions_economic_title);
                break;

            case 2:
                title = context.getResources().getString(R.string.survey_summary_indicatortitle);
                break;

            case 3:
                title = context.getResources().getString(R.string.life_map_title);
                break;

            default:
                throw new IndexOutOfBoundsException("Got index " + position + ", expected index less than 4");
        }
        return new StepViewModel.Builder(context)
                .setTitle(title) //can be a CharSequence instead
                .create();
    }
}