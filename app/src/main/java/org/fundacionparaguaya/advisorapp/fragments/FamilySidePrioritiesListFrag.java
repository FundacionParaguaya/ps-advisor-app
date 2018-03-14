package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.PrioritiesListAdapter;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.viewmodels.FamilyDetailViewModel;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;

import javax.inject.Inject;

/**
 * List of priorities on the PrioritiesPage
 * - Only appears in Horizontal mode
 * * Made with love using Super Cow Powers
 */

public class FamilySidePrioritiesListFrag extends Fragment implements PrioritiesListAdapter.SelectedPriorityHandler{


    @Inject
    protected InjectionViewModelFactory mViewModelFactory;
    protected FamilyDetailViewModel mFamilyViewModel;

    AppCompatTextView mPrioritiesCount;
    RecyclerView mRvIndicatorList;

    private PrioritiesListAdapter mAdapter = new PrioritiesListAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        //Need to getParentFragment twice to get to the familydetails fragment
        mFamilyViewModel = ViewModelProviders
                .of(getParentFragment().getParentFragment(), mViewModelFactory)
                .get(FamilyDetailViewModel.class);

        mAdapter.addSelectedPriorityHandler(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_familypage_priorities_prioritylist, container, false);

        mPrioritiesCount = view.findViewById(R.id.family_priorities_list_title);

        mRvIndicatorList = view.findViewById(R.id.family_priorities_list);
        mRvIndicatorList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mRvIndicatorList.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        subscribeToViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeViewModelObservers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFamilyViewModel.removeSelectedPriority();
    }

    private void subscribeToViewModel(){
        mFamilyViewModel.getSelectedSnapshot().observe(this, this::updateSnapshot);
    }

    private void updateSnapshot(Snapshot snapshot){
        mFamilyViewModel.removeSelectedPriority();
        mAdapter.setSnapshot(snapshot);
        String title = getContext().getText(R.string.priorities_listcounttitle) +
                " (" + snapshot.getPriorities().size() + ")";
        mPrioritiesCount.setText(title);
    }

    private void removeViewModelObservers() {
        if (mFamilyViewModel.getSelectedSnapshot() != null) {
            if (!mFamilyViewModel.getSelectedSnapshot().hasActiveObservers()) {
                mFamilyViewModel.getSelectedSnapshot().removeObservers(this);
            }
        }
    }

    @Override
    public void onPrioritySelected(PrioritiesListAdapter.PrioritySelectedEvent event) {
        mFamilyViewModel.setSelectedPriority(event.getPriority());
    }
}