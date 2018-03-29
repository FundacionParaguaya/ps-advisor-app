package org.fundacionparaguaya.advisorapp.ui.families.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.data.model.IndicatorOption;
import org.fundacionparaguaya.advisorapp.data.model.LifeMapPriority;
import org.fundacionparaguaya.advisorapp.ui.common.widget.HeaderBodyView;
import org.fundacionparaguaya.advisorapp.ui.common.widget.IndicatorCard;
import org.fundacionparaguaya.advisorapp.injection.InjectionViewModelFactory;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

/**
 * This fragment requires a
 * {@link FamilyDetailViewModel to exist within
 * it's context.}
 */
public class FamilyPriorityDetailFragment extends Fragment {

    HeaderBodyView mProblemView;
    HeaderBodyView mSolutionView;
    HeaderBodyView mDueDateView;

    IndicatorCard mPriorityIndicatorCard;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prioritydetail, container, false);

        mTitle = view.findViewById(R.id.textview_prioritydetail_title);

        mProblemView = view.findViewById(R.id.headerbody_prioritydetail_problem);
        mSolutionView = view.findViewById(R.id.headerbody_prioritydetail_solution);
        mDueDateView = view.findViewById(R.id.headerbody_prioritydetail_date);
        mPriorityIndicatorCard = view.findViewById(R.id.indicatorcard_prioritydetail);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        subscribeToViewModel();
    }

    public void subscribeToViewModel() {
        mFamilyInformationViewModel.getSelectedPriority().observe(this, this::bindPriority);
    }

    public void bindPriority(@Nullable LifeMapPriority priority) {

        // If no priority, then hide everything and set to Title to No Priorities
        if (priority == null) {
            mTitle.setText(getContext().getString(R.string.no_priorities));
            mProblemView.setVisibility(View.INVISIBLE);
            mSolutionView.setVisibility(View.INVISIBLE);
            mDueDateView.setVisibility(View.INVISIBLE);
            mPriorityIndicatorCard.setVisibility(View.INVISIBLE);
        } else {
            mTitle.setText(priority.getIndicator().getTitle());
            mProblemView.setVisibility(View.VISIBLE);
            mSolutionView.setVisibility(View.VISIBLE);
            mDueDateView.setVisibility(View.VISIBLE);
            mPriorityIndicatorCard.setVisibility(View.VISIBLE);

            mProblemView.setHeaderText(getContext().getString(R.string.priority_problem));
            mSolutionView.setHeaderText(getContext().getString(R.string.priority_solution));
            mDueDateView.setHeaderText(getContext().getString(R.string.priority_estimatedcompletion));

            mProblemView.setBodyText(priority.getReason());
            mSolutionView.setBodyText(priority.getAction());
            String date = SimpleDateFormat.getDateInstance().format(priority.getEstimatedDate());
            mDueDateView.setBodyText(date);

            if (mIndicatorResponse != null) {
                mIndicatorResponse.removeObservers(this);
            }

            //View model management
            mIndicatorResponse = mFamilyInformationViewModel.getLatestIndicatorResponse(priority.getIndicator());
            mIndicatorResponse.observe(this, this::setIndicator);
        }
    }

    private void setIndicator(IndicatorOption option){
        mPriorityIndicatorCard.setOption(option);
    }

    @Override
    public void onDetach() {
        if(mIndicatorResponse!=null)
        {
            mIndicatorResponse.removeObservers(this);
        }

        super.onDetach();
    }
}
