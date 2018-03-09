package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;
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

        mFamilyInformationViewModel = ViewModelProviders
                .of(this, mViewModelFactory)
                .get(FamilyDetailViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prioritydetail, container, false);

        mProblemView = view.findViewById(R.id.headerbody_prioritydetail_problem);
        mSolutionView = view.findViewById(R.id.headerbody_prioritydetail_solution);
        mDueDateView = view.findViewById(R.id.headerbody_prioritydetail_date);
        mPriorityIndicator = view.findViewById(R.id.indicatorcard_prioritydetail);

        subscribeToViewModel();

        return view;
    }

    public void subscribeToViewModel()
    {
        mFamilyInformationViewModel.getSelectedPriority().observe(this, this::bindPriority);
    }

    public void bindPriority(LifeMapPriority p)
    {
        mProblemView.setBodyText(p.getReason());
        mSolutionView.setBodyText(p.getAction());
        mDueDateView.setBodyText(p.getEstimatedDate().toString());

        if(mIndicatorResponse!=null)
        {
            mIndicatorResponse.removeObservers(this);
        }

        mIndicatorResponse = mFamilyInformationViewModel.getLatestIndicatorResponse(p.getIndicator());
        mIndicatorResponse.observe(this, mPriorityIndicator::setOption);
    }

    @Override
    public void onDetach() {
        mIndicatorResponse.removeObservers(this);
        super.onDetach();
    }
}
