package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.SubTabFragmentCallback;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.viewmodels.FamilyInformationViewModel;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import javax.inject.Inject;
import java.util.*;

/**
 * List of all the indicators a family has
 */

public class FamilyIndicatorsListFrag extends Fragment {

    Spinner mSnapshotSpinner;
    SnapshotSpinAdapter mSpinnerAdapter;

    @Inject
    InjectionViewModelFactory mViewModelFactory;
    FamilyInformationViewModel mFamilyInformationViewModel;

    ImageButton mBtnNewSnapshot;

    RecyclerView mRvIndicatorList;

    final FamilyIndicatorAdapter mIndicatorAdapter = new FamilyIndicatorAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mFamilyInformationViewModel = ViewModelProviders
                .of(getActivity(), mViewModelFactory)
                .get(FamilyInformationViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_familydetail_indicators, container, false);

        mSnapshotSpinner = view.findViewById(R.id.spinner_familyindicators_snapshot);
        mRvIndicatorList = view.findViewById(R.id.rv_familyindicators_list);
        mBtnNewSnapshot = view.findViewById(R.id.btn_familyindicators_newsnapshot);

        mBtnNewSnapshot.setOnClickListener(l->
        {
          try
          {
              //starts the survey activity
              ((SubTabFragmentCallback)getParentFragment()).onTakeSnapshot();
          }
          catch (NullPointerException | ClassCastException e)
          {
              Log.e(this.getClass().getName(), e.getMessage());

              throw e;
          }
        });

        mRvIndicatorList.setLayoutManager(new StickyHeaderLayoutManager());
        mRvIndicatorList.setAdapter(mIndicatorAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSpinnerAdapter = new SnapshotSpinAdapter(this.getContext(), R.layout.item_tv_spinner);
        mSnapshotSpinner.setAdapter(mSpinnerAdapter);

        mSnapshotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Snapshot s = (Snapshot) mSpinnerAdapter.getItem(i);
                mFamilyInformationViewModel.setSelectedSnapshot(s);
                mSpinnerAdapter.setSelected(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //mSpinnerAdapter.setSelected(-1);
            }
        });

        addViewModelObservers();
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
                    mSpinnerAdapter.setSnapshotList(null);
                }
                else mSpinnerAdapter.setSnapshotList(snapshots.toArray(new Snapshot[snapshots.size()]));

                //has to be called after getSnapshots
                mFamilyInformationViewModel.getSelectedSnapshot().observe(this, mSpinnerAdapter::setSelected);
        });

        mFamilyInformationViewModel.getSnapshotIndicators().observe(this, mIndicatorAdapter::setIndicators);
    }

    static class FamilyIndicatorAdapter extends SectioningAdapter
    {
        SortedMap<IndicatorQuestion, IndicatorOption> mIndicatorOptionMap;

        private static class Section {
            IndicatorOption.Level mLevel;
            ArrayList<Map.Entry<IndicatorQuestion, IndicatorOption>> indicatorEntries = new ArrayList<>();
        }

        //section for each color
        Section mRedSection = new Section();
        Section mYellowSection = new Section();
        Section mGreenSection = new Section();

        ArrayList<Section> mSections;

        FamilyIndicatorAdapter()
        {
            mSections = new ArrayList<>();

            mRedSection.mLevel = IndicatorOption.Level.Red;
            mYellowSection.mLevel = IndicatorOption.Level.Yellow;
            mGreenSection.mLevel = IndicatorOption.Level.Green;

            mSections.add(mRedSection);
            mSections.add(mYellowSection);
            mSections.add(mGreenSection);
        }

        @Override
        public boolean doesSectionHaveHeader(int sectionIndex) {
            return true;
        }

        void setIndicators(SortedMap<IndicatorQuestion, IndicatorOption> indicatorMap)
        {
            mIndicatorOptionMap = indicatorMap;

            mRedSection.indicatorEntries.clear();
            mYellowSection.indicatorEntries.clear();
            mGreenSection.indicatorEntries.clear();

            for(Map.Entry<IndicatorQuestion, IndicatorOption> optionEntry: indicatorMap.entrySet())
            {
                IndicatorOption.Level optionLevel = optionEntry.getValue().getLevel();

                Section s = null;

                switch (optionLevel)
                {
                    case Red:
                        s = mRedSection;
                        break;

                    case Yellow:
                        s = mYellowSection;
                        break;

                    case Green:
                        s = mGreenSection;
                        break;
                }

                if(s!=null) {
                    s.indicatorEntries.add(optionEntry);
                }
            }

            notifyAllSectionsDataSetChanged();
        }


        public int getNumberOfSections() {
            return mSections.size();
        }

        @Override
        public int getNumberOfItemsInSection(int sectionIndex) {
            return mSections.get(sectionIndex).indicatorEntries.size();
        }

        /**For creating the indicator items**/
        @Override
        public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.
                    from(parent.getContext()).inflate(R.layout.item_familydetailindicator, parent, false);

            return new FamilyIndicatorViewHolder(itemView);
        }

        /**For creating the header view holder**/
        @Override
        public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_familydetail_indicatoritem_header, parent, false);
            return new HeaderViewHolder(v);
        }

        @Override
        public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {

            ((FamilyIndicatorViewHolder)viewHolder).setIndicatorResponse(
                    mSections.get(sectionIndex).indicatorEntries.get(itemIndex));
        }

        @Override
        public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
            Section s = mSections.get(sectionIndex);
            HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;

            hvh.titleTextView.setText(s.mLevel.name());
        }

        public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
            TextView titleTextView;

            HeaderViewHolder(View itemView) {
                super(itemView);
                titleTextView = (TextView) itemView.findViewById(R.id.tv_familydetail_indicators_sectionlabel);
            }
        }

        static class FamilyIndicatorViewHolder extends SectioningAdapter.ItemViewHolder
        {
            View mLevelIndicator;
            TextView mTitle;
            TextView mLevelDescription;

            IndicatorQuestion mIndicatorQuestion;
            IndicatorOption mIndicatorOption;

            public FamilyIndicatorViewHolder(View itemView) {
                super(itemView);

                mTitle = itemView.findViewById(R.id.tv_familydetail_indicatoritem_title);
                mLevelDescription = itemView.findViewById(R.id.tv_familydetail_indicatoritem_description);
                mLevelIndicator = itemView.findViewById(R.id.view_familydetail_indicatoritem_color);
            }

            public void setIndicatorResponse(Map.Entry<IndicatorQuestion, IndicatorOption> indicatorResponse)
            {
                mIndicatorQuestion = indicatorResponse.getKey();
                mIndicatorOption = indicatorResponse.getValue();

                mTitle.setText(mIndicatorQuestion.getDescription());
                mLevelDescription.setText(mIndicatorOption.getDescription());

                int color = -1;

                switch (mIndicatorOption.getLevel())
                {
                    case Red:
                        color = R.color.indicator_card_red;
                        break;

                    case Yellow:
                        color = R.color.indicator_card_yellow;
                        break;

                    case Green:
                        color = R.color.indicator_card_green;
                        break;

                    case None:
                        color = -1;
                        break;
                }

                if(color!=-1)
                {
                    mLevelIndicator.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), color));
                }
            }
        }
    }

    //The problem with creating a custom adapter (https://stackoverflow.com/a/8116756) is that the spinner
    //has to be restyled.. it should be done eventually so we don't have to do the nonsense in this constructor
    //TODO @blhylak clean up this mess
    static class SnapshotSpinAdapter extends ArrayAdapter<Snapshot>
    {
        // Your sent context
        private Context context;
        // Your custom values for the spinner (User)
        private Snapshot[] values;

        //the currently selected item. -1 -> no selection
        private int mSelectedArrayIndex = -1;

        SnapshotSpinAdapter(Context context, int textViewResourceId) {

            super(context, textViewResourceId);
            this.context = context;
        }

        void setSelected(Snapshot s)
        {
            if(s==null) mSelectedArrayIndex = 0;
            else {
                for (int i = 0; i < values.length; i++) {
                    if (values[i].equals(s)) mSelectedArrayIndex = i;
                }
            }
        }

        void setSelected(int spinnerIndex)
        {
            //Array
            //// [   0   ]
            //// [   1   ]   <- selected array index = 1, loc = 0
            //// [   2   ]
            //// [   3   ]

            //Spinner
            ////Index 0: [   1   ] <- currently selected
            ////Index 1: [   0   ]
            ////Index 2: [   2   ]
            ////Index 3: [   3   ]

            //so if index 0 is clicked, that's our currently selected
            //if index 1 is clicked that's actually the value -1
            if(mSelectedArrayIndex !=-1) {
                if (spinnerIndex == 0) {
                    //reselected current selection
                }
                else if(spinnerIndex <= mSelectedArrayIndex)
                {
                    mSelectedArrayIndex = spinnerIndex-1;
                }
                else if(spinnerIndex > mSelectedArrayIndex)
                {
                    mSelectedArrayIndex = spinnerIndex;
                }
            }
            else
            {
                mSelectedArrayIndex = spinnerIndex;
            }
        }

        public void setSnapshotList(Snapshot[] values)
        {
            if(values!=null) {
                Snapshot latestSnapshot = null;

                for (Snapshot snapshot : values) {

                    //reset any flags that we have on a snapshot
                    snapshot.setIsLatest(false);

                    Date createdDate = snapshot.getCreatedAt();

                    if (latestSnapshot == null || createdDate!=null && snapshot.getCreatedAt().after(latestSnapshot.getCreatedAt())) {
                        latestSnapshot = snapshot;
                    }
                }

                if (latestSnapshot != null) {
                    latestSnapshot.setIsLatest(true);
                }
            }

            this.values = values;

            notifyDataSetChanged();
        }

        @Nullable
        @Override
        public Snapshot getItem(int position) {
            //pretend 4 is selected

            //if position = 0
            //return 4

            //if 1, return 0. if 2, return 1, if 3 return 2, if 4, return 3, if 5, return 5, if 6 return 6
            if(mSelectedArrayIndex !=-1) {
                if (position == 0) {
                    return values[mSelectedArrayIndex];
                }
                else if(position<= mSelectedArrayIndex)
                {
                    return values[position-1];
                }
                else if(position> mSelectedArrayIndex)
                {
                    return values[position];
                }
            }
            else return this.values[position];

            throw new IndexOutOfBoundsException();
        }

        @Override
        public int getCount() {
            if(this.values==null)
            {
                return 0;
            }
            else return this.values.length;
        }
    }
}
