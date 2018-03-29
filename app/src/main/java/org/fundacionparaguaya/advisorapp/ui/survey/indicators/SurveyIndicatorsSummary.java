package org.fundacionparaguaya.advisorapp.ui.survey.indicators;

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
import org.fundacionparaguaya.advisorapp.ui.survey.AbstractSurveyFragment;
import org.fundacionparaguaya.advisorapp.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.ui.survey.SharedSurveyViewModel;

import javax.inject.Inject;

/**
 *
 */

public class SurveyIndicatorsSummary extends AbstractSurveyFragment implements IndicatorsSummaryAdapter.IndicatorClickListener {

    SharedSurveyViewModel mSurveyViewModel;

    @Inject
    InjectionViewModelFactory mViewModelFactory;

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
            mSurveyViewModel.setFocusedIndicator(mSurveyViewModel.getSurveyInProgress().getIndicatorQuestions().size()-1);
        });

        mSurveySummaryAdapter = new IndicatorsSummaryAdapter();
        mSurveySummaryAdapter.setClickListener(this::onItemClick);

        mSurveyViewModel.getIndicatorResponses().observe(this, responses->
        {
            mSurveySummaryAdapter.setSkippedIndicators(mSurveyViewModel.getSkippedIndicators());
            setNumSkipped(mSurveySummaryAdapter.getItemCount());
        });

        mIndicatorList.setAdapter(mSurveySummaryAdapter);

        return view;
    }

    public void onItemClick(View view, int position) {
        mSurveyViewModel.setFocusedIndicator(mSurveySummaryAdapter.getValue(position));
    }

    public void setState(IndicatorQuestionState state){
        this.state = state;
        switch (state){
            case COMPLETE:
                break;
            case INCOMPLETE:
                break;
            default:
                //do nothing
        }
    }

    public void setNumSkipped(int num){
        numSkipped.setText(num + " " + getResources().getString(R.string.survey_summary_questionsleft));

        if(num == 0) {
            setState(IndicatorQuestionState.COMPLETE);
            mIndicatorList.setVisibility(View.INVISIBLE);
        }
        else
        {
            setState(IndicatorQuestionState.INCOMPLETE);
            mIndicatorList.setVisibility(View.VISIBLE);
        }
    }

}
