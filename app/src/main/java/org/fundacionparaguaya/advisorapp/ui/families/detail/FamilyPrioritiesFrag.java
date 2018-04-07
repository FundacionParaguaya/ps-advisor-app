package org.fundacionparaguaya.advisorapp.ui.families.detail;

import android.arch.lifecycle.ViewModelProviders;
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
import org.fundacionparaguaya.advisorapp.ui.base.AbstractStackedFrag;
import org.fundacionparaguaya.advisorapp.ui.base.NavigationListener;
import org.fundacionparaguaya.advisorapp.util.ScreenUtils;

import javax.inject.Inject;

/**
 * Displays a list of priorities and a description of each priority
 * - Implemented in FamilyDetailFrag
 *
 */

public class FamilyPrioritiesFrag extends Fragment implements NavigationListener {

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

        mIsDualPane = ScreenUtils.isLandscape(getContext());

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

    private void observeViewModel()
    {
        mViewModel.SelectedPriority().observe(this, priority -> {
            if(!mIsDualPane && priority == null && getChildFragmentManager().getBackStackEntryCount()>0)
            {
                getChildFragmentManager().popBackStack();
            }
            else if(!mIsDualPane && priority!=null &&
                    !(getChildFragmentManager().findFragmentById(R.id.fragment_container) instanceof FamilyPriorityDetailFragment))
            {
                getChildFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                                R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, new FamilyPriorityDetailFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        mViewModel.SelectedSnapshot().observe(this, snapshot -> {
            if(mIsDualPane && mViewModel.getSelectedPriority() == null)
            {
                mViewModel.selectFirstPriority();
            }
        });
    }

    @Override
    public void onNavigateNext(AbstractStackedFrag frag) {
        /* stub */
    }

    @Override
    public void onNavigateBack() {
        mViewModel.clearSelectedPriority();
    }
}