package org.fundacionparaguaya.advisorapp.ui.survey;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.ui.dashboard.DashActivity;
import org.fundacionparaguaya.advisorapp.util.KeyboardUtils;
import org.fundacionparaguaya.advisorapp.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.ui.survey.SharedSurveyViewModel.SurveyState;
import org.fundacionparaguaya.advisorapp.util.MixpanelHelper;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Fragment that the actual survey is taken in... (background, economic, indicators, life map, etc.)
 */

public class TakeSurveyFragment extends Fragment implements  StepperLayout.StepperListener {

    private StepperLayout mStepperLayout;

    @Inject
    InjectionViewModelFactory modelFactory;

    private SharedSurveyViewModel mSurveyViewModel;
    protected final static String FAMILY_ID_KEY = "FAMILY_ID";

    private TextView mTitle;
    private TextView mProgress;

    private final SurveyState[] surveyPages =
            {SurveyState.BACKGROUND, SurveyState.ECONOMIC_QUESTIONS, SurveyState.INDICATORS, SurveyState.LIFEMAP};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdvisorApplication) this.getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSurveyViewModel = ViewModelProviders
                .of(getActivity(), modelFactory)
                .get(SharedSurveyViewModel.class);

        mSurveyViewModel.getSurveyState().observe(this, surveyState -> {
            assert surveyState != null;
            switch (surveyState)
            {
                case BACKGROUND:
                case ECONOMIC_QUESTIONS:
                    setOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                    break;

                default:
                    setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    KeyboardUtils.hideKeyboard(mStepperLayout, getActivity());
                    break;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_takesurvey, container, false);

        mStepperLayout = view.findViewById(R.id.stepperLayout);
        mStepperLayout.setAdapter(new SurveyTabPagerAdapter(getChildFragmentManager(), getContext()));

        mStepperLayout.setListener(this);
        mTitle = view.findViewById(R.id.tv_takesurvey_title);
        mProgress = view.findViewById(R.id.tv_takesurvey_progress);

        mSurveyViewModel.getSurveyState().observe(this,
                surveyState -> {
                    assert surveyState != null;
                    //update title and progress
                    verifyStateMatchesPager(surveyState);
                    String title="";

                    switch (surveyState)
                    {
                        case BACKGROUND:
                            title = getResources().getString(R.string.survey_summary_backgroundtitle);
                            MixpanelHelper.SurveyEvents.openBackgroundQuestions(getContext());
                            break;

                        case ECONOMIC_QUESTIONS:
                            title = getResources().getString(R.string.surveyquestions_economic_title);
                            MixpanelHelper.SurveyEvents.openEconomicQuestions(getContext());
                            break;

                        case INDICATORS:
                            title = getResources().getString(R.string.survey_summary_indicatortitle);
                            MixpanelHelper.SurveyEvents.openIndicators(getContext());
                            break;

                        case LIFEMAP:
                            title = getResources().getString(R.string.life_map_title);
                            MixpanelHelper.SurveyEvents.openLifeMap(getContext());
                            break;

                        case COMPLETE:
                            MixpanelHelper.SurveyEvents.finishSurvey(getContext(), mSurveyViewModel.isResurvey());
                            finishSurvey();
                    }

                    mTitle.setText(title);
        });

        mSurveyViewModel.Progress().observe(this, surveyProgress -> {
            String progressText;

            if(surveyProgress.getRemaining()==0 && surveyProgress.getSkipped() ==0)
            {
                progressText = getString(R.string.all_complete);
            }
            else if(surveyProgress.getSkipped() == 0)
            {
                progressText = String.format(getString(R.string.survey_questionsremaining), surveyProgress.getRemaining());
            }
            else
            {
                progressText = String.format(getString(R.string.survey_questionsskippedandremaining),
                        surveyProgress.getSkipped(), surveyProgress.getRemaining());
            }

            mProgress.setText(progressText);
        });

        return view;
    }

    public void finishSurvey(){
        mSurveyViewModel.CurrentFamily().observe(this, family -> {
                    Intent result = new Intent(getContext(), DashActivity.class);
                    result.putExtra(FAMILY_ID_KEY, mSurveyViewModel.getCurrentFamily().getId());
                    getActivity().setResult(Activity.RESULT_OK, result);

                    mSurveyViewModel.submitSnapshotAsync();
                    getActivity().finish();
                });

    }

    private void setOrientation(int orientation)
    {
        if(getActivity().getRequestedOrientation() != orientation)
        {
            getActivity().setRequestedOrientation(orientation);
        }
    }
    /**
     * Checks whether the new state matches the pager. If not, move the pager to match the current state.
     *
     * @param surveyState
     */
    public void verifyStateMatchesPager(SharedSurveyViewModel.SurveyState surveyState)
    {
        int desiredIndex = getIndexFromState(surveyState);

        if(desiredIndex >=0 && mStepperLayout.getCurrentStepPosition() != desiredIndex)
        {
            mStepperLayout.setCurrentStepPosition(desiredIndex);
        }
    }

    public int getIndexFromState(SharedSurveyViewModel.SurveyState surveyState)
    {
        return Arrays.asList(surveyPages).indexOf(surveyState);
    }

    public SurveyState getStateFromIndex(int i)
    {
        return surveyPages[i];
    }

    //region Step Listener Interface
    @Override
    public void onCompleted(View completeButton) {

    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    //should set the survey state here probably... will handle rotations better also need to handle case of fragments switching between
    @Override
    public void onStepSelected(int newStepPosition) {
        MixpanelHelper.SurveyEvents.surveyStepperUsed(getContext());
        mSurveyViewModel.setSurveyState(getStateFromIndex(newStepPosition));
    }

    @Override
    public void onReturn() {

    }
    //endregion
}
