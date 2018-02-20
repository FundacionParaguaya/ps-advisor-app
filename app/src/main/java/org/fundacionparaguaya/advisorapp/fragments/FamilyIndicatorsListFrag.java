package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.FamilyIndicatorAdapter;
import org.fundacionparaguaya.advisorapp.adapters.SelectedFirstSpinnerAdapter;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.util.MixpanelHelper;
import org.fundacionparaguaya.advisorapp.viewmodels.FamilyDetailViewModel;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.*;

import javax.inject.Inject;

/**
 * List of all the indicators a family has
 */

public class FamilyIndicatorsListFrag extends Fragment {

    AppCompatSpinner mSnapshotSpinner;
    SnapshotSpinAdapter mSpinnerAdapter;

    @Inject
    InjectionViewModelFactory mViewModelFactory;
    FamilyDetailViewModel mFamilyInformationViewModel;

    RecyclerView mRvIndicatorList;

    final FamilyIndicatorAdapter mIndicatorAdapter = new PriorityListFrag.EditPriorityListAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mFamilyInformationViewModel = ViewModelProviders
                .of(getParentFragment(), mViewModelFactory)
                .get(FamilyDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_familydetail_indicators, container, false);

        mSnapshotSpinner = view.findViewById(R.id.spinner_familyindicators_snapshot);
        mRvIndicatorList = view.findViewById(R.id.rv_familyindicators_list);

        mRvIndicatorList.setLayoutManager(new StickyHeaderLayoutManager());
        mRvIndicatorList.setHasFixedSize(true);
        mRvIndicatorList.setAdapter(mIndicatorAdapter);

        mSpinnerAdapter = new SnapshotSpinAdapter(this.getContext(), R.layout.item_tv_spinner);
        mSnapshotSpinner.setAdapter(mSpinnerAdapter);

        mSnapshotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Snapshot s = (Snapshot) mSpinnerAdapter.getDataAt(i);
                mFamilyInformationViewModel.setSelectedSnapshot(s);
                mSpinnerAdapter.setSelected(s);

                MixpanelHelper.SnapshotChanged.changeSnap(getContext());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //mSpinnerAdapter.setSelected(-1);
            }
        });

        addViewModelObservers();

        return view;
    }


    public void removeViewModelObservers()
    {
        mFamilyInformationViewModel.getSnapshots().removeObservers(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeViewModelObservers();
    }

    public void addViewModelObservers()
    {
        mFamilyInformationViewModel.getSnapshots().observe(this, (snapshots) -> {
            if(snapshots==null)
            {
                mSpinnerAdapter.setValues(null);
            }
            else {
                mSpinnerAdapter.setValues(snapshots.toArray(new Snapshot[snapshots.size()]));
                MixpanelHelper.ReviewingSnapshotEvent.snapshotReviewed(getContext());
            }

            //has to be called after getSnapshots
            mFamilyInformationViewModel.getSelectedSnapshot().observe(this, mSpinnerAdapter::setSelected);
        });

        mFamilyInformationViewModel.getPriorities().observe(this, mIndicatorAdapter::setPriorities);
        mFamilyInformationViewModel.getSnapshotIndicators().observe(this, mIndicatorAdapter::setIndicators);
    }

    static class SnapshotSpinAdapter extends SelectedFirstSpinnerAdapter<Snapshot>
    {
        SnapshotSpinAdapter(Context context, int textViewResourceId) {

            super(context, textViewResourceId);
        }

        @Override
        public void setValues(Snapshot[] values)
        {
            if(values!=null && values.length>0) {

                Arrays.sort(values, Collections.reverseOrder());
                values[0].setIsLatest(true);
            }

            super.setValues(values);
        }
    }
}
