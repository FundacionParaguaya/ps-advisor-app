package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.IndicatorsSummaryAdapter;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 *
 */

public class SurveyIndicatorsSummary extends AbstractSurveyFragment implements IndicatorsSummaryAdapter.InterfaceClickListener {

    SharedSurveyViewModel mSurveyViewModel;

    @Inject
    InjectionViewModelFactory mViewModelFactory;

    ArrayList<String> indicatorNames = new ArrayList<>();

    private TextView numSkipped;
    private RecyclerView mIndicatorList;
    private IndicatorsSummaryAdapter mSurveySummaryAdapter;

    public enum IndicatorQuestionState {COMPLETE, INCOMPLETE}

    IndicatorQuestionState state;

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
        setTitle(getString(R.string.survey_summary_title));
    }

    @Override
    public void onResume() {
        try {
            indicatorNames.clear();
            for (IndicatorQuestion skippedQuestions : mSurveyViewModel.getSkippedIndicators()) {
                //Add in the card instances here
                indicatorNames.add(skippedQuestions.getIndicator().getTitle());
            }

            if (indicatorNames.size() == 0) {
                setState(IndicatorQuestionState.COMPLETE);
                mIndicatorList.setVisibility(View.INVISIBLE);
            } else {
                setState(IndicatorQuestionState.INCOMPLETE);
            }

        } catch (NullPointerException e) {
            setState(IndicatorQuestionState.COMPLETE);
        }

        setNumSkipped(indicatorNames.size());

        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surveysummary, container, false);

        AppCompatImageButton mSubmitButton = view.findViewById(R.id.btn_surveysummary_submit);
        AppCompatImageButton mBackButton = view.findViewById(R.id.btn_surveysummary_back);

        numSkipped = view.findViewById(R.id.surveysummary_layout_numskipped);

        mIndicatorList = view.findViewById(R.id.surveysummary_layout_recyclerview);
        mIndicatorList.setLayoutManager(new LinearLayoutManager(getContext()));

        mSubmitButton.setOnClickListener((event) ->
        {
            mSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.LIFEMAP);
        });

        mBackButton.setOnClickListener((event) ->
        {
            mSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.INDICATORS);
        });

        mSurveySummaryAdapter = new IndicatorsSummaryAdapter(getContext(), indicatorNames);
        mIndicatorList.setAdapter(mSurveySummaryAdapter);
        mSurveySummaryAdapter.setClickListener(this::onItemClick);

        return view;
    }

    public void onItemClick(View view, int position) {
        mSurveyViewModel.setFocusedQuestion(mSurveySummaryAdapter.getIndicatorName(position));
        mSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.REVIEWINDICATORS);
    }

    public void setState(IndicatorQuestionState state){
        this.state = state;
        switch (state){
            case COMPLETE:
                numSkipped.setText(" ");
                break;
            case INCOMPLETE:
                break;
            default:
                //do nothing
        }
    }

    public void setNumSkipped(int num){
        numSkipped.setText(num + " " + getResources().getString(R.string.survey_summary_questionsleft));
    }

}
