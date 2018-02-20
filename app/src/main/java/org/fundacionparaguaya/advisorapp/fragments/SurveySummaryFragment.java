package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.SurveySummaryAdapter;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.viewcomponents.SurveySummaryComponent;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 *
 */

public class SurveySummaryFragment extends AbstractSurveyFragment implements SurveySummaryAdapter.InterfaceClickListener {

    SurveySummaryComponent backgroundQs;
    SurveySummaryComponent indicators;

    SharedSurveyViewModel mSurveyViewModel;

    @Inject
    InjectionViewModelFactory mViewModelFactory;

    SurveySummaryAdapter indicatorAdapter;

    ArrayList<String> indicatorNames = new ArrayList<>();

    Button mSubmitButton;
    Button mBackButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSurveyViewModel = ViewModelProviders
                .of(getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        setShowFooter(false);
        setHeaderColor(R.color.surveysummary_background);
        setTitle(getString(R.string.survey_summary_title));
    }

    @Override
    public void onResume() {
        try {
            indicatorNames.clear();
            for (IndicatorQuestion skippedQuestions : mSurveyViewModel.getSkippedIndicators()) {
                //Add in the card instances here
                indicatorNames.add(skippedQuestions.getName());
            }
            indicators.setNames(indicatorNames);
            if (indicatorNames.size() == 0) {
                indicators.setState(SurveySummaryComponent.SurveySummaryState.COMPLETE);
            } else {
                indicators.setState(SurveySummaryComponent.SurveySummaryState.INCOMPLETE);
            }

        } catch (NullPointerException e) {
            indicators.setState(SurveySummaryComponent.SurveySummaryState.COMPLETE);
        }
        backgroundQs.setState(SurveySummaryComponent.SurveySummaryState.COMPLETE);

        indicators.setNumSkipped(indicatorNames.size());

        indicatorAdapter = indicators.getAdapter();
        indicatorAdapter.setClickListener(this::onItemClick);

        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surveysummary, container, false);

        backgroundQs = (SurveySummaryComponent) view.findViewById(R.id.surveysummary_background);
        mSubmitButton = view.findViewById(R.id.btn_surveysummary_submit);
        mBackButton = view.findViewById(R.id.btn_surveysummary_back);

        mSubmitButton.setOnClickListener((event) ->
        {
            mSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.LIFEMAP);
        });

        mBackButton.setOnClickListener((event) ->
        {
            mSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.INDICATORS);
        });

        indicators = (SurveySummaryComponent) view.findViewById(R.id.surveysummary_indicators);

        return view;
    }

    public void onItemClick(View view, int position) {
        mSurveyViewModel.setFocusedQuestion(indicatorAdapter.getIndicatorName(position));
        mSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.REVIEWINDICATORS);
    }
}
