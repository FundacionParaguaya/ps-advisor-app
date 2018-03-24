package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.PrioritiesListAdapter;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

import javax.inject.Inject;

/**
 * Shows all of the indicators that a family has and their red/yellow/green status. Selecting one opens up a dialog,
 * that when filled out, adds the priority to the view model
 */

public class PriorityListFrag extends Fragment {

    private TextView mHeader;

    @Inject
    protected InjectionViewModelFactory mViewModelFactory;
    protected SharedSurveyViewModel mSharedSurveyViewModel;

    PrioritiesListAdapter.SurveyPriorities mPriorityAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSharedSurveyViewModel = ViewModelProviders.
                of(getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        mPriorityAdapter = new PrioritiesListAdapter.SurveyPriorities();
        mPriorityAdapter.setCallback(getCallback());
    }

    public PrioritiesListAdapter.PriorityClickedHandler getCallback() {
        try {
            return ((PrioritiesListAdapter.PriorityClickedHandler) getParentFragment());
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment of LifeMap must implement PriorityClickedHandler");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_prioritylist, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.rv_prioritieslist);
        recyclerView.setAdapter(mPriorityAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        v.findViewById(R.id.btn_prioritylist_save).setOnClickListener((view -> onSave()));

        mHeader = v.findViewById(R.id.tv_prioriitylist_header);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSharedSurveyViewModel.getSnapshot().observe(this, snapshot ->
        {
            mPriorityAdapter.setSnapshot(snapshot);
        });
    }

    public void onSave() {

        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.all_confirmation_question))
                .setContentText(getString(R.string.survey_summary_confirmation_details))
                .setCancelText(getString(R.string.all_cancel))
                .setConfirmText(getString(R.string.survey_summary_submit))
                .showCancelButton(true)
                .setCancelClickListener(SweetAlertDialog::cancel)
                .setConfirmClickListener((dialog)-> {
                    mSharedSurveyViewModel.saveSnapshotAsync();
                    dialog.dismissWithAnimation();
                })
                .show();
    }
}
