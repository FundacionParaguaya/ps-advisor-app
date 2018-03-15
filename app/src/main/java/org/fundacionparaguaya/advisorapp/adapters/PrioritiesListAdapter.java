package org.fundacionparaguaya.advisorapp.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.util.IndicatorUtilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for Priorities List Fragment
 *
 */

public class PrioritiesListAdapter extends RecyclerView.Adapter<PrioritiesListAdapter.PrioritiesListViewHolder> {

    private List<LifeMapPriority> mPriorities = new ArrayList<>();

    private ArrayList<PrioritiesListViewHolder> mViewHolderList = new ArrayList<>();

    private Snapshot mSelectedSnapshot;

    private LifeMapPriority mSelectedPriority;

    private ArrayList<PriorityClickedHandler> mPrioritySelectedHandlers = new ArrayList<>();

    public void setSnapshot(Snapshot snapshot){
        mSelectedSnapshot = snapshot;
        mPriorities = mSelectedSnapshot.getPriorities();
        mSelectedPriority = null;
        mViewHolderList = new ArrayList<>(); //Remove the current list to prevent rewriting
        this.notifyDataSetChanged();
    }

    @Override
    public PrioritiesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_familydetail_prioritieslist, parent, false);
        return new PrioritiesListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PrioritiesListViewHolder holder, int position) {

        holder.bindViewHolder(mPriorities.get(position),
                IndicatorUtilities.getResponseForIndicator(
                mSelectedSnapshot.getPriorities().get(position).getIndicator(),
                mSelectedSnapshot.getIndicatorResponses()), position + 1);

        holder.itemView.setOnClickListener(v -> setSelected(holder.getPriority()));

        mViewHolderList.add(holder);

        if (mSelectedPriority == null && mViewHolderList.size() == 1) {
            setSelected(holder.getPriority());
        }
    }

    @Override
    public int getItemCount() {
        return mPriorities.size();
    }

    public void setSelected(LifeMapPriority priority){
        mSelectedPriority = priority;

        //Set only 1 to selected, everything else is not selected
        for (PrioritiesListViewHolder viewHolder : mViewHolderList){
            if (viewHolder.getPriority().equals(priority)){
                viewHolder.setSelected(true);
            } else {
                viewHolder.setSelected(false);
            }
        }

        notifyHandlers(mSelectedPriority);
    }

    //region Item Selection
    public void addSelectedPriorityHandler(PriorityClickedHandler handler){
        mPrioritySelectedHandlers.add(handler);
    }

    private void notifyHandlers(LifeMapPriority priority){
        for (PriorityClickedHandler handler : mPrioritySelectedHandlers){
            handler.onPrioritySelected(new PriorityClickedEvent(priority));
        }
    }

    public interface PriorityClickedHandler {
        void onPrioritySelected(PriorityClickedEvent event);
    }

    public static class PriorityClickedEvent {
        private LifeMapPriority mPriority;
        PriorityClickedEvent(LifeMapPriority priority){
            this.mPriority = priority;
        }
        public LifeMapPriority getPriority (){
            return mPriority;
        }
    }
    //endregion Item Selection

    static class PrioritiesListViewHolder extends RecyclerView.ViewHolder{
        private TextView mIndicatorTitle;
        private AppCompatImageView mIndicatorColor;

        private boolean isSelected;

        private LifeMapPriority mPriority;
        private IndicatorOption mIndicator;

        PrioritiesListViewHolder(View view) {
            super(view);

            mIndicatorTitle = view.findViewById(R.id.familydetail_prioritieslist_item_text);
            mIndicatorColor = view.findViewById(R.id.familydetail_prioritieslist_item_indicatorcolor);
        }

        void bindViewHolder(LifeMapPriority priority, IndicatorOption indicator, int index){
            mPriority = priority;
            setIndicator(indicator, index);
        }

        private void setIndicator(IndicatorOption indicator, int index){
            mIndicator = indicator;

            String title = index + ". " + mIndicator.getIndicator().getTitle();
            mIndicatorTitle.setText(title);

            IndicatorUtilities.setViewColorFromResponse(mIndicator, mIndicatorColor);
        }

        public LifeMapPriority getPriority(){
            return mPriority;
        }

        public IndicatorOption getIndicator(){
            return mIndicator;
        }

        public boolean isSelected(){
            return isSelected;
        }

        public void setSelected(boolean isSelected){
            this.isSelected = isSelected;

            if (isSelected){
                ViewCompat.setBackground(itemView, ContextCompat.getDrawable(itemView.getContext(),
                        R.drawable.list_item_background_selected));
                mIndicatorTitle.setTextColor(itemView.getResources()
                        .getColor(R.color.app_white));
            } else {
                ViewCompat.setBackground(itemView, ContextCompat.getDrawable(itemView.getContext(),
                        R.drawable.list_item_background_unselected));
                mIndicatorTitle.setTextColor(itemView.getResources()
                        .getColor(R.color.app_black));
            }
        }

    }

    public static class SurveyPriorities extends PrioritiesListAdapter {

        PriorityClickedHandler mClickHandler;

        public void setCallback(PriorityClickedHandler callback) {
            mClickHandler = callback;
        }

        @Override
        public PrioritiesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prioritylist, parent, false);

            return new SurveyPriorityViewHolder(mClickHandler, itemView);
        }

        @Override
        public void setSelected(LifeMapPriority priority) {
            //don't do anything
        }

        public static class SurveyPriorityViewHolder extends PrioritiesListViewHolder{
            protected TextView mTitle;
            protected TextView mProblem;
            protected TextView mStrategy;
            protected TextView mWhen;
            protected AppCompatImageView mIndicatorColor;

            protected IndicatorOption mResponse;
            protected LifeMapPriority mPriority;
            private PriorityClickedHandler mHandler;

            public SurveyPriorityViewHolder(PriorityClickedHandler handler, View itemView) {
                super(itemView);

                mTitle = itemView.findViewById(R.id.tv_priorityitem_title);
                mProblem = itemView.findViewById(R.id.tv_priorityitem_problem);
                mStrategy = itemView.findViewById(R.id.tv_priorityitem_strategy);
                mWhen = itemView.findViewById(R.id.tv_priorityitem_when);
                mIndicatorColor = itemView.findViewById(R.id.iv_priorityitem_color);

                mHandler = handler;
            }


            @Override
            void bindViewHolder(LifeMapPriority priority, IndicatorOption indicatorResponse, int index) {
                mResponse =indicatorResponse;
                mPriority= priority;

                IndicatorUtilities.setViewColorFromResponse(mResponse, mIndicatorColor);
                //first item in adapter is the header.. this is hacky, but it works.
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

                mIndicatorColor.setOnClickListener(event->
                {
                    mHandler.onPrioritySelected(new PriorityClickedEvent(mPriority));
                });

                itemView.setOnClickListener((event) ->
                {
                    mHandler.onPrioritySelected(new PriorityClickedEvent(mPriority));
                });
            }
        }
    }
}
