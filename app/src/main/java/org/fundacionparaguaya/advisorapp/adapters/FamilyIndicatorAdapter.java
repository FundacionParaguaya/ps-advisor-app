package org.fundacionparaguaya.advisorapp.adapters;

import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.Indicator;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;
import org.fundacionparaguaya.advisorapp.util.IndicatorUtilities;
import org.zakariya.stickyheaders.SectioningAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays both indicators and priorities from a snapshot
 */

public class FamilyIndicatorAdapter extends SectioningAdapter
{
    private List<IndicatorOption> mIndicators;

    //section for each color
    private PrioritySection mPriorities = new PrioritySection();

    private Section mRedSection = new Section();
    private Section mYellowSection = new Section();
    private Section mGreenSection = new Section();

    private ArrayList<Section> mSections;

    private static final int PRIORITIES_SECTION_INDEX = 0;
    private static final int INDICATOR_TYPE = 0;
    private static final int PRIORITY_TYPE = 1;

    private static class Section {
        IndicatorOption.Level mLevel;
        private List<IndicatorOption> mOptions = new ArrayList<>();
    }

    private static class PrioritySection  extends Section {
        private List<LifeMapPriority> mPriorities = new ArrayList<>();
    }

    public FamilyIndicatorAdapter()
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

    public void setIndicators(List<IndicatorOption> indicators)
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
            return ((FamilyIndicatorAdapter.PrioritySection) mSections.get(sectionIndex)).mPriorities.size();
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

    /**
     * Creates an arbitrary priority viewholder. Allows this class to be extended more easily and return
     * alternative priority views.
     * @param parent
     * @return
     */
    public PriorityViewHolder createPriorityViewHolder(ViewGroup parent)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prioritylist, parent, false);
        return new PriorityViewHolder(itemView);
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
            return createPriorityViewHolder(parent);
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
    public FamilyIndicatorAdapter.HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_familydetail_indicatoritem_header, parent, false);
        return new FamilyIndicatorAdapter.HeaderViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        if(itemType == INDICATOR_TYPE) {
            ((FamilyIndicatorAdapter.FamilyIndicatorViewHolder) viewHolder).setIndicatorResponse(
                    mSections.get(sectionIndex).mOptions.get(itemIndex));
        }
        else if (itemType == PRIORITY_TYPE)
        {
            LifeMapPriority p = mPriorities.mPriorities.get(itemIndex);

            for(IndicatorOption i: mIndicators)
            {
                if (i.getIndicator().equals(p.getIndicator()))
                {
                    ((FamilyIndicatorAdapter.PriorityViewHolder)viewHolder).setPriority(i, p);
                    break;
                }
            }
        }
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        Section s = mSections.get(sectionIndex);
        FamilyIndicatorAdapter.HeaderViewHolder hvh = (FamilyIndicatorAdapter.HeaderViewHolder) viewHolder;

        if(headerType == PRIORITY_TYPE)
        {
            hvh.titleTextView.setText("Priorities");
        }
        else   hvh.titleTextView.setText(s.mLevel.name());
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        private TextView titleTextView;

        HeaderViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.tv_familydetail_indicators_sectionlabel);
        }
    }

    public static class PriorityViewHolder extends SectioningAdapter.ItemViewHolder {
        protected TextView mTitle;
        protected TextView mProblem;
        protected TextView mStrategy;
        protected TextView mWhen;
        protected AppCompatImageView mIndicatorColor;

        protected IndicatorOption mResponse;
        protected LifeMapPriority mPriority;

        public PriorityViewHolder(View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.tv_priorityitem_title);
            mProblem = itemView.findViewById(R.id.tv_priorityitem_problem);
            mStrategy = itemView.findViewById(R.id.tv_priorityitem_strategy);
            mWhen = itemView.findViewById(R.id.tv_priorityitem_when);
            mIndicatorColor = itemView.findViewById(R.id.iv_priorityitem_color);
        }


        public void setPriority(IndicatorOption option, LifeMapPriority p) {

            mResponse = option;
            mPriority= p;

            IndicatorUtilities.setViewColorFromResponse(mResponse, mIndicatorColor);
            //first item in adapter is the header.. this is hacky, but it works.
            mTitle.setText(getAdapterPosition()-1 + ". " + mPriority.getIndicator().getTitle());

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
        private AppCompatImageView mLevelIndicator;
        private TextView mTitle;
        private TextView mLevelDescription;

        private Indicator mIndicator;
        private IndicatorOption mIndicatorOption;

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

            IndicatorUtilities.setViewColorFromResponse(indicatorResponse, mLevelIndicator);
        }
    }
}