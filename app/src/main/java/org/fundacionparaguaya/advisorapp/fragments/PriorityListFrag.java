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
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.FamilyIndicatorAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.LifeMapFragmentCallback;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.PriorityChangeCallback;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.viewcomponents.PriorityDetailPopupWindow;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

/**
 * Shows all of the indicators that a family has and their red/yellow/green status. Selecting one opens up a dialog,
 * that when filled out, adds the priority to the view model
 */

public class PriorityListFrag extends Fragment {

    //TODO: this isn't enforced
    private static final int MAX_PRIORITIES = 5;

    private TextView mHeader;

    @Inject
    protected InjectionViewModelFactory mViewModelFactory;
    protected SharedSurveyViewModel mSharedSurveyViewModel;

    EditPriorityListAdapter mPriorityAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSharedSurveyViewModel = ViewModelProviders.
                of(getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        mPriorityAdapter = new EditPriorityListAdapter();
        mPriorityAdapter.setCallback(getCallback());

        mSharedSurveyViewModel.getPriorities().observe(this, (value) ->
        {
            if (value != null && value.size() < MAX_PRIORITIES) {
                mHeader.setText(String.format(getString(R.string.prioritieslist_header_remaining),
                        MAX_PRIORITIES - value.size()));
            }
            else if (value != null) {
                mHeader.setText(getString(R.string.prioritieslist_header_complete));
            }
            else {
                mHeader.setText(String.format(getString(R.string.prioritieslist_header_remaining),
                        MAX_PRIORITIES));
            }

            mPriorityAdapter.setPriorities(value);
        });

        mSharedSurveyViewModel.getSnapshotIndicators().observe(this, mPriorityAdapter::setIndicators);
    }

    public LifeMapFragmentCallback getCallback() {
        try {
            return ((LifeMapFragmentCallback) getParentFragment());
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment of LifeMap must implement LifeMapFragmentCallback");
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

    public void onSave() {

        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.survey_summary_confirmation))
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

    static class EditPriorityListAdapter extends FamilyIndicatorAdapter {

        LifeMapFragmentCallback mCallback;

        @Override
        public int getNumberOfSections() {
            return 1;
        }

        public void setCallback(LifeMapFragmentCallback callback) {
            mCallback = callback;
        }

        @Override
        public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
            HeaderViewHolder v = super.onCreateHeaderViewHolder(parent, headerType);
            v.itemView.setVisibility(View.GONE);
            return v;
        }


        public void setIndicators(Collection<IndicatorOption> indicators) {
            super.setIndicators(new ArrayList<>(indicators));
        }

        @Override
        public PriorityViewHolder createPriorityViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prioritylist, parent, false);
            EditPriorityViewHolder holder = new EditPriorityViewHolder(view);

            return holder;
        }

        static class EditPriorityViewHolder extends PriorityViewHolder {

            private EditPriorityViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
