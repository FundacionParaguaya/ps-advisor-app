package org.fundacionparaguaya.adviserplatform.ui.survey.priorities;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.fundacionparaguaya.assistantadvisor.AdviserAssistantApplication;
import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption;
import org.fundacionparaguaya.adviserplatform.data.model.LifeMapPriority;
import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.adviserplatform.ui.common.LifeMapAdapter;
import org.fundacionparaguaya.adviserplatform.ui.common.LifeMapFragmentCallback;
import org.fundacionparaguaya.adviserplatform.ui.common.PrioritiesListAdapter;
import org.fundacionparaguaya.adviserplatform.ui.survey.AbstractSurveyFragment;
import org.fundacionparaguaya.adviserplatform.ui.survey.SharedSurveyViewModel;
import org.fundacionparaguaya.adviserplatform.util.MixpanelHelper;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Top most fragment for displaying the life map
 */

public class SurveyChoosePrioritiesFragment extends AbstractSurveyFragment implements LifeMapFragmentCallback, PrioritiesListAdapter.PriorityClickedHandler{

    private static final int EDIT_PRIORITY_REQUEST = 72;

    @Inject
    protected InjectionViewModelFactory mViewModelFactory;
    protected SharedSurveyViewModel mSharedSurveyViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdviserAssistantApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSharedSurveyViewModel = ViewModelProviders.
                of(getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        setShowFooter(false);
        setShowHeader(true);
        setTitle(getString(R.string.life_map_title));
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
    public LiveData<Collection<IndicatorOption>> getIndicatorResponses() {
        return Transformations.map(mSharedSurveyViewModel.getIndicatorResponses(), Map::values);
    }


    @Override
    public void onPrioritySelected(PrioritiesListAdapter.PriorityClickedEvent event) {
        Intent intent = EditPriorityActivity.build(this.getContext(), mSharedSurveyViewModel.getSelectedSurvey(),
              mSharedSurveyViewModel.getResponseForIndicator(event.getPriority().getIndicator()), event.getPriority());

        startActivityForResult(intent, EDIT_PRIORITY_REQUEST);
    }

    @Override
    public void onLifeMapIndicatorClicked(LifeMapAdapter.LifeMapIndicatorClickedEvent e) {
        Intent intent = EditPriorityActivity.build(this.getContext(), mSharedSurveyViewModel.getSelectedSurvey(),
                e.getIndicatorOption(), e.getPriority());

        MixpanelHelper.PriorityEvents.startEditPriority(getContext());

        startActivityForResult(intent, EDIT_PRIORITY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        boolean existingPriority = false;

        switch (requestCode)
        {
            case EDIT_PRIORITY_REQUEST:

                if(resultCode == Activity.RESULT_OK) {
                    LifeMapPriority priority = EditPriorityActivity.processResult(data, mSharedSurveyViewModel.getSelectedSurvey());

                    if (mSharedSurveyViewModel.hasPriority(priority.getIndicator())) {
                        mSharedSurveyViewModel.updatePriority(priority);
                        existingPriority = true;
                    }
                    else {
                        mSharedSurveyViewModel.addPriority(priority);
                    }
                }

                break;

        }

        MixpanelHelper.PriorityEvents.finishEditPriority(getContext(), resultCode, existingPriority);
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
