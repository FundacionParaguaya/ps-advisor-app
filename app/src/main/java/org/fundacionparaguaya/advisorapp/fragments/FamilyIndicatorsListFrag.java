package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.SelectedFirstSpinnerAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.PriorityChangeCallback;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.SubTabFragmentCallback;
import org.fundacionparaguaya.advisorapp.models.*;
import org.fundacionparaguaya.advisorapp.util.IndicatorUtilities;
import org.fundacionparaguaya.advisorapp.viewcomponents.PriorityDetailPopupWindow;
import org.fundacionparaguaya.advisorapp.viewmodels.FamilyInformationViewModel;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.text.SimpleDateFormat;
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
        mRvIndicatorList.setHasFixedSize(true);
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
                Snapshot s = (Snapshot) mSpinnerAdapter.getDataAt(i);
                mFamilyInformationViewModel.setSelectedSnapshot(s);
                mSpinnerAdapter.setSelected(s);
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
                    mSpinnerAdapter.setValues(null);
                }
                else mSpinnerAdapter.setValues(snapshots.toArray(new Snapshot[snapshots.size()]));

                //has to be called after getSnapshots
                mFamilyInformationViewModel.getSelectedSnapshot().observe(this, mSpinnerAdapter::setSelected);
        });

        mFamilyInformationViewModel.getSnapshotPriorities().observe(this, mIndicatorAdapter::setPriorities);
        mFamilyInformationViewModel.getSnapshotIndicators().observe(this, mIndicatorAdapter::setIndicators);
    }

    static class FamilyIndicatorAdapter extends SectioningAdapter
    {
       List<IndicatorOption> mIndicators;

        private static final int PRIORITIES_SECTION_INDEX = 0;

        private static final int INDICATOR_TYPE = 0;
        private static final int PRIORITY_TYPE =1;

        private static class Section {
            IndicatorOption.Level mLevel;
            List<IndicatorOption> mOptions = new ArrayList<>();
        }

        private static class PrioritySection  extends  Section{
            List<LifeMapPriority> mPriorities = new ArrayList<>();
        }

        //section for each color
        PrioritySection mPriorities = new PrioritySection();

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

            mSections.add(mPriorities); //priorities is set first
            mSections.add(mRedSection);
            mSections.add(mYellowSection);
            mSections.add(mGreenSection);
        }

        @Override
        public int getItemViewType(int adapterPosition) {
            return super.getItemViewType(adapterPosition);
        }

        @Override
        public boolean doesSectionHaveHeader(int sectionIndex) {
            return true;
        }

        public void setPriorities(List<LifeMapPriority> p)
        {
            mPriorities.mPriorities = p;
            notifySectionDataSetChanged(0);
        }

        void setIndicators(List<IndicatorOption> indicators)
        {
            mIndicators = indicators;

            mRedSection.mOptions.clear();
            mYellowSection.mOptions.clear();
            mGreenSection.mOptions.clear();

            for(IndicatorOption option: mIndicators)
            {
                IndicatorOption.Level optionLevel = option.getLevel();

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
                    s.mOptions.add(option);
                }
            }

            notifyAllSectionsDataSetChanged();
        }


        public int getNumberOfSections() {
            return mSections.size();
        }

        @Override
        public int getNumberOfItemsInSection(int sectionIndex) {
            if(isPrioritiesSection(sectionIndex))
            {
                return ((PrioritySection) mSections.get(sectionIndex)).mPriorities.size();
            }
            else return mSections.get(sectionIndex).mOptions.size();
        }

        @Override
        public int getSectionItemUserType(int sectionIndex, int itemIndex) {
            if(isPrioritiesSection(sectionIndex))
            {
                return PRIORITY_TYPE;
            }
            else return INDICATOR_TYPE;
        }

        /**For creating the indicator items**/
        @Override
        public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
            View itemView;

            if(viewType == INDICATOR_TYPE)
            {
                itemView = LayoutInflater.
                        from(parent.getContext()).inflate(R.layout.item_familydetailindicator, parent, false);
                return new FamilyIndicatorViewHolder(itemView);
            }
            else
            {
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prioritylist, parent, false);
                return new PriorityViewHolder(itemView);
            }
        }

        @Override
        public int getSectionHeaderUserType(int sectionIndex) {
            if(isPrioritiesSection(sectionIndex))
            {
                return PRIORITY_TYPE;
            }
            else return INDICATOR_TYPE;
        }

        /**
         * Tests whether the given section index is equal to the priorities section index
         * @param sectionIndex section indext to test
         * @return
         */
        public boolean isPrioritiesSection(int sectionIndex)
        {
            return sectionIndex == PRIORITIES_SECTION_INDEX;
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
            if(itemType == INDICATOR_TYPE) {
                ((FamilyIndicatorViewHolder) viewHolder).setIndicatorResponse(
                        mSections.get(sectionIndex).mOptions.get(itemIndex));
            }
            else if (itemType == PRIORITY_TYPE)
            {
                LifeMapPriority p = mPriorities.mPriorities.get(itemIndex);

                for(IndicatorOption i: mIndicators)
                {
                    if (i.getIndicator().equals(p.getIndicator()))
                    {
                        ((PriorityViewHolder)viewHolder).setPriority(i, p);
                        break;
                    }
                }
            }
        }

        @Override
        public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
            Section s = mSections.get(sectionIndex);
            HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;

            if(headerType == PRIORITY_TYPE)
            {
                hvh.titleTextView.setText("Priorities");
            }
            else   hvh.titleTextView.setText(s.mLevel.name());
        }

        public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
            TextView titleTextView;

            HeaderViewHolder(View itemView) {
                super(itemView);
                titleTextView = (TextView) itemView.findViewById(R.id.tv_familydetail_indicators_sectionlabel);
            }
        }

        static class PriorityViewHolder extends SectioningAdapter.ItemViewHolder {
            private TextView mTitle;
            private TextView mProblem;
            private TextView mStrategy;
            private TextView mWhen;
            private AppCompatImageView mIndicatorColor;

            private IndicatorOption mResponse;
            private LifeMapPriority mPriority;

            PriorityViewHolder(View itemView) {
                super(itemView);

                mTitle = itemView.findViewById(R.id.tv_priorityitem_title);
                mProblem = itemView.findViewById(R.id.tv_priorityitem_problem);
                mStrategy = itemView.findViewById(R.id.tv_priorityitem_strategy);
                mWhen = itemView.findViewById(R.id.tv_priorityitem_when);
                mIndicatorColor = itemView.findViewById(R.id.iv_priorityitem_color);
            }


            void setPriority(IndicatorOption option, LifeMapPriority p) {

                mResponse = option;
                mPriority= p;

                IndicatorUtilities.setViewColorFromResponse(mResponse, mIndicatorColor);
                mTitle.setText(getAdapterPosition()+1 + ". " + mPriority.getIndicator().getTitle());

                if(mPriority.getReason()!=null) {
                    mProblem.setText(mPriority.getReason());
                }

                if(mPriority.getAction()!=null) {
                    mStrategy.setText(mPriority.getAction());
                }

                if(mPriority.getEstimatedDate()!=null) {
                    String when = SimpleDateFormat.getDateInstance().format(mPriority.getEstimatedDate());
                    mWhen.setText(when);
                }
            }
        }

        static class FamilyIndicatorViewHolder extends SectioningAdapter.ItemViewHolder
        {
            AppCompatImageView mLevelIndicator;
            TextView mTitle;
            TextView mLevelDescription;

            Indicator mIndicator;
            IndicatorOption mIndicatorOption;

            public FamilyIndicatorViewHolder(View itemView) {
                super(itemView);

                mTitle = itemView.findViewById(R.id.tv_familydetail_indicatoritem_title);
                mLevelDescription = itemView.findViewById(R.id.tv_familydetail_indicatoritem_description);
                mLevelIndicator = itemView.findViewById(R.id.iv_indicatoritem_color);
            }

            public void setIndicatorResponse(IndicatorOption indicatorResponse)
            {
                mIndicator= indicatorResponse.getIndicator();
                mIndicatorOption = indicatorResponse;

                mTitle.setText(mIndicator.getTitle());
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
                    ViewCompat.setBackgroundTintList(mLevelIndicator, ContextCompat.getColorStateList(itemView.getContext(), color));
                }
            }
        }
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
