package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.FamilyIndicatorAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.PriorityChangeCallback;
import org.fundacionparaguaya.advisorapp.models.Indicator;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;
import org.fundacionparaguaya.advisorapp.util.IndicatorUtilities;
import org.fundacionparaguaya.advisorapp.viewcomponents.PriorityDetailPopupWindow;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Shows all of the indicators that a family has and their red/yellow/green status. Selecting one opens up a dialog,
 * that when filled out, adds the priority to the view model
 */

public class PriorityListFrag extends Fragment  {

    //TODO: this isn't enforced
    private static final int MAX_PRIORITIES = 5;

    private TextView mHeader;

    @Inject
    protected InjectionViewModelFactory mViewModelFactory;
    protected SharedSurveyViewModel mSharedSurveyViewModel;

    PriorityListAdapter mPriorityAdapter;

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
        mPriorityAdapter.setCallback(new PriorityChangeCallback(mSharedSurveyViewModel));

        mSharedSurveyViewModel.getPriorities().observe(this, (value) ->
        {
            if(value!=null && value.size()<MAX_PRIORITIES)
            {
                mHeader.setText(String.format(getString(R.string.prioritieslist_header_remaining),
                        MAX_PRIORITIES - value.size()));
            }
            else if(value!=null)
            {
                mHeader.setText(getString(R.string.prioritieslist_header_complete));
            }
            else
            {
                mHeader.setText(String.format(getString(R.string.prioritieslist_header_remaining),
                        MAX_PRIORITIES));
            }

            mPriorityAdapter.setPriorities(value);
        });

        mSharedSurveyViewModel.getIndicatorResponses().observe(this, mPriorityAdapter::setIndicators);
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

    public void onSave()
    {
        mSharedSurveyViewModel.saveSnapshotAsync();
    }

    static class EditPriorityListAdapter extends FamilyIndicatorAdapter
    {
        PriorityChangeCallback mCallback;

        @Override
        public int getNumberOfSections() {
            return 1;
        }

        @Override
        public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
            HeaderViewHolder v =  super.onCreateHeaderViewHolder(parent, headerType);
            v.itemView.setVisibility(View.GONE);
            return v;
        }

        private void setCallback(PriorityChangeCallback callback)
        {
            mCallback = callback;
        }

        public void setIndicators(Collection<IndicatorOption> indicators) {
            setIndicators(new ArrayList<>(indicators));
        }

        @Override
        public PriorityViewHolder createPriorityViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prioritylist, parent, false);

            EditPriorityViewHolder holder = new EditPriorityViewHolder(view);
            holder.setCallback(mCallback);

            return holder;
        }

        static class EditPriorityViewHolder extends PriorityViewHolder {
            private PriorityChangeCallback mCallback;

            private EditPriorityViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener((v)->
                {
                    if(mResponse.getLevel()== IndicatorOption.Level.Green)
                    {
                        Toast.makeText(v.getContext(), R.string.prioritychooser_greenselected, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        showEditPopup();
                    }
                });
            }

            private void showEditPopup()
            {
                new PriorityDetailPopupWindow.Builder(itemView.getContext()).
                    setIndicatorOption(mResponse).setPriority(mPriority).setResponseCallback(mCallback).build().show();
            }

            void setCallback(PriorityChangeCallback callback)
            {
                mCallback = callback;
            }
        }
        }
    }
