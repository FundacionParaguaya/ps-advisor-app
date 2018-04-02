package org.fundacionparaguaya.advisorapp.ui.families.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.injection.InjectionViewModelFactory;

import javax.inject.Inject;

/**
 * Displays a list of priorities and a description of each priority
 * - Implemented in FamilyDetailFrag
 *
 */

public class FamilyPrioritiesFrag extends Fragment {

    @Inject
    InjectionViewModelFactory viewModelFactory;

    FamilyDetailViewModel mViewModel;

    private boolean mIsDualPane;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mViewModel = ViewModelProviders
                .of(getParentFragment(), viewModelFactory)
                .get(FamilyDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_familypriorities, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mIsDualPane = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if(mIsDualPane)
        {
            if(getChildFragmentManager().findFragmentById(R.id.family_prioritiespage_detailsfrag) == null)
            {
                getChildFragmentManager().beginTransaction()
                        .add(R.id.family_prioritiespage_detailsfrag, new FamilyPriorityDetailFragment())
                        .commit();
            }

            if(getChildFragmentManager().findFragmentById(R.id.family_prioritiespage_prioritieslistfrag) == null)
            {
                getChildFragmentManager().beginTransaction()
                        .add(R.id.family_prioritiespage_prioritieslistfrag, new FamilyPrioritiesListFrag())
                        .commit();
            }
        }
        else
        {
            if(getChildFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                getChildFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new FamilyPrioritiesListFrag())
                        .commit();
            }
        }

        observeViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        removeObservers();
    }

    private void observeViewModel()
    {
        mViewModel.SelectedPriority().observe(this, priority -> {
            if(!mIsDualPane)
            {
                if(priority == null)
                {
                    getChildFragmentManager().popBackStack();
                }
                else{
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new FamilyPriorityDetailFragment())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }

    private void removeObservers()
    {
        mViewModel.SelectedPriority().removeObservers(this);
    }
}