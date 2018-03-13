package org.fundacionparaguaya.advisorapp.fragments;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.activities.EditPriorityActivity;
import org.fundacionparaguaya.advisorapp.adapters.LifeMapAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.LifeMapFragmentCallback;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;


/**
 * Top most fragment for displaying the life map
 */

public class SurveyChoosePrioritiesFragment extends AbstractSurveyFragment implements LifeMapFragmentCallback {

    private static final int EDIT_PRIORITY_REQUEST = 72;

    @Inject
    protected InjectionViewModelFactory mViewModelFactory;
    protected SharedSurveyViewModel mSharedSurveyViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSharedSurveyViewModel = ViewModelProviders.
                of(getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        setShowFooter(false);
        setTitle(getString(R.string.choosepriorities_title));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_choosepriorities, container, false);
        return v;
    }

    @Override
    public LiveData<List<LifeMapPriority>> getPriorities() {
        return mSharedSurveyViewModel.getPriorities();
    }

    @Override
    public LiveData<Collection<IndicatorOption>> getSnapshotIndicators() {
        return mSharedSurveyViewModel.getSnapshotIndicators();
    }

    @Override
    public void onLifeMapIndicatorClicked(LifeMapAdapter.LifeMapIndicatorClickedEvent e) {
        if(e.getIndicatorOption().getLevel()== IndicatorOption.Level.Green)
        {
            Toast.makeText(getContext(), getResources().getString(R.string.prioritychooser_greenselected), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = EditPriorityActivity.build(this.getContext(), mSharedSurveyViewModel.getSurveyInProgress(),
                    e.getIndicatorOption(), e.getPriority());

            startActivityForResult(intent, EDIT_PRIORITY_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case EDIT_PRIORITY_REQUEST:

                if(resultCode == Activity.RESULT_OK) {
                    LifeMapPriority priority = EditPriorityActivity.processResult(data, mSharedSurveyViewModel.getSurveyInProgress());

                    if (mSharedSurveyViewModel.hasPriority(priority.getIndicator())) {
                        mSharedSurveyViewModel.updatePriority(priority);
                    }
                    else {
                        mSharedSurveyViewModel.addPriority(priority);
                    }
                }

                break;

        }
    }

    //live data for priorities


        //on change
        // if no priorities, show turtle image
        //  otherwise on change diff and show priorities.. update header
        //clicking on priority should launch dialog with it prefilled for editing
        //reordering should update order of live data object

        //recycler view
        //when priorities change
        //so if isSelected, just update number
        //or select/deselect
        //do diff?

        //when indicator is clicked -> show popup dialog

}
