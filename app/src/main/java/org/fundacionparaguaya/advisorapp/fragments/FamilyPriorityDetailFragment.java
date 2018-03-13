package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.ObjectUtils;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;
import org.fundacionparaguaya.advisorapp.util.IndicatorUtilities;
import org.fundacionparaguaya.advisorapp.viewcomponents.HeaderBodyView;
import org.fundacionparaguaya.advisorapp.viewcomponents.IndicatorCard;
import org.fundacionparaguaya.advisorapp.viewmodels.FamilyDetailViewModel;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;

import javax.inject.Inject;

/**
 * This fragment requires a {@link org.fundacionparaguaya.advisorapp.viewmodels.FamilyDetailViewModel to exist within
 * it's context.}
 */
public class FamilyPriorityDetailFragment extends Fragment {

    HeaderBodyView mProblemView;
    HeaderBodyView mSolutionView;
    HeaderBodyView mDueDateView;

    IndicatorCard mPriorityIndicator;

    AppCompatTextView mTitle;

    @Inject
    InjectionViewModelFactory mViewModelFactory;
    FamilyDetailViewModel mFamilyInformationViewModel;

    LiveData<IndicatorOption> mIndicatorResponse = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        //Need to getParentFragment twice to get to the familydetails fragment
        mFamilyInformationViewModel = ViewModelProviders
                .of(getParentFragment().getParentFragment(), mViewModelFactory)
                .get(FamilyDetailViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prioritydetail, container, false);

        mTitle = view.findViewById(R.id.textview_prioritydetail_title);

        mProblemView = view.findViewById(R.id.headerbody_prioritydetail_problem);
        mSolutionView = view.findViewById(R.id.headerbody_prioritydetail_solution);
        mDueDateView = view.findViewById(R.id.headerbody_prioritydetail_date);
        mPriorityIndicator = view.findViewById(R.id.indicatorcard_prioritydetail);


        subscribeToViewModel();

        return view;
    }

    public void subscribeToViewModel() {
        mFamilyInformationViewModel.getSelectedPriority().observe(this, this::bindPriority);
    }

    public void bindPriority(@Nullable LifeMapPriority priority) {

        // If no priority, then hide everything and set to Title to No Priorities
        if (priority == null) {
            mTitle.setText(getContext().getString(R.string.priorities_defaulttitle));
            mProblemView.setVisibility(View.INVISIBLE);
            mSolutionView.setVisibility(View.INVISIBLE);
            mDueDateView.setVisibility(View.INVISIBLE);
            mPriorityIndicator.setVisibility(View.INVISIBLE);
        } else {
            mTitle.setText(priority.getIndicator().getTitle());
            mProblemView.setVisibility(View.VISIBLE);
            mSolutionView.setVisibility(View.VISIBLE);
            mDueDateView.setVisibility(View.VISIBLE);
            mPriorityIndicator.setVisibility(View.VISIBLE);

            mProblemView.setHeaderText(getContext().getString(R.string.priorities_problemtitle));
            mSolutionView.setHeaderText(getContext().getString(R.string.priorities_solutiontitle));
            mDueDateView.setHeaderText(getContext().getString(R.string.priorities_completiondatetitle));

            mProblemView.setBodyText(priority.getReason());
            mSolutionView.setBodyText(priority.getAction());
            mDueDateView.setBodyText(priority.getEstimatedDate().toString());

            if (mIndicatorResponse != null) {
                mIndicatorResponse.removeObservers(this);
            }

            mIndicatorResponse = mFamilyInformationViewModel.getLatestIndicatorResponse(priority.getIndicator());

            mIndicatorResponse.observe(this, this::setIndicator);
        }
    }

    private void setIndicator(IndicatorOption option){
        mPriorityIndicator.setOption(option);
        try {
            switch (mIndicatorResponse.getValue().getLevel()) {
                case Red:
                    mPriorityIndicator.setColor(IndicatorCard.CardColor.RED);
                    break;
                case Yellow:
                    mPriorityIndicator.setColor(IndicatorCard.CardColor.YELLOW);
                    break;
                case Green:
                    mPriorityIndicator.setColor(IndicatorCard.CardColor.GREEN);
                    break;
                default:
                    mPriorityIndicator.setColor(R.color.app_white);
                    break;
            }
        } catch (NullPointerException e){
            mPriorityIndicator.setColor(R.color.app_black);
        }
    }

    @Override
    public void onDetach() {
        try {
            mIndicatorResponse.removeObservers(this);
        } catch (NullPointerException e) {
            //Do nothing, this was never an observer
        }
        super.onDetach();
    }
}
