package org.fundacionparaguaya.adviserplatform.ui.common;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption;
import org.fundacionparaguaya.adviserplatform.data.model.LifeMapPriority;
import org.fundacionparaguaya.adviserplatform.data.model.Snapshot;
import org.fundacionparaguaya.adviserplatform.util.IndicatorUtilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Adapter for Priorities List Fragment
 *
 */

public class PrioritiesListAdapter extends RecyclerView.Adapter<PrioritiesListAdapter.PrioritiesListViewHolder> {

    private static final String LOG_TAG = PrioritiesListAdapter.class.getSimpleName();
    private List<LifeMapPriority> mPriorities = new ArrayList<>();

    private Snapshot mSelectedSnapshot;

    private LifeMapPriority mSelectedPriority = null;

    private ArrayList<PriorityClickedHandler> mPrioritySelectedHandlers = new ArrayList<>();

    public void setSnapshot(Snapshot snapshot){
        mSelectedSnapshot = snapshot;
        mPriorities = mSelectedSnapshot.getPriorities();

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

        //TODO Sodep: During testing, a false attempt to load a green indicator as a priority
        //TODO Sodep: made a post to the server, with emtpy date and action
        //TODO Sodep: this condition, leads to have null elements inside priorities
        //TODO Sodep: further investigation is required to reproduce and eliminate this bug
        final LifeMapPriority lifeMapPriority = mSelectedSnapshot.getPriorities().get(position);
        if(lifeMapPriority != null) {
            holder.bind(mPriorities.get(position),
                    IndicatorUtilities.getResponseForIndicator(
                            lifeMapPriority.getIndicator(),
                            mSelectedSnapshot.getIndicatorResponses()), position + 1);

            holder.itemView.setOnClickListener(v -> notifyHandlers(mPriorities.get(position)));
            if(mPriorities.get(position) == mSelectedPriority) holder.setSelected(true);
            else holder.setSelected(false);
        } else {
            Timber.d(String.format("%s: this is a bug that POSTs null/empty priorities", LOG_TAG));
        }
    }

    @Override
    public int getItemCount() {
        return mPriorities.size();
    }

    //TODO: this causes views not to be recycled (issue #
    public void setSelected(LifeMapPriority priority){
        mSelectedPriority = priority;
        this.notifyDataSetChanged();
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
        View mView;

        protected TextView mIndicatorTitle;
        protected AppCompatImageView mIndicatorColor;

        private boolean isSelected;

        protected LifeMapPriority mPriority;
        private IndicatorOption mIndicator;

        PrioritiesListViewHolder(View view) {
            super(view);
            mView = view;
            mIndicatorTitle = view.findViewById(R.id.tv_priorityitem_title);
            mIndicatorColor = view.findViewById(R.id.iv_priorityitem_color);
        }

        void bind(LifeMapPriority priority, IndicatorOption indicator, int index){
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

            return new SurveyPriorityViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PrioritiesListViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            holder.itemView.setOnClickListener(v ->
                    mClickHandler.onPrioritySelected(new PriorityClickedEvent(holder.getPriority())));
        }

        @Override
        public void setSelected(LifeMapPriority priority) {
            //don't do anything
        }

        public static class SurveyPriorityViewHolder extends PrioritiesListViewHolder{
            TextView mProblem;
            TextView mStrategy;
            TextView mWhen;

            IndicatorOption mResponse;

            public SurveyPriorityViewHolder(View itemView) {
                super(itemView);

                mProblem = itemView.findViewById(R.id.tv_priorityitem_problem);
                mStrategy = itemView.findViewById(R.id.tv_priorityitem_strategy);
                mWhen = itemView.findViewById(R.id.tv_priorityitem_when);
            }

            @Override
            void bind(LifeMapPriority priority, IndicatorOption indicatorResponse, int index) {
                mResponse =indicatorResponse;
                mPriority= priority;

                IndicatorUtilities.setViewColorFromResponse(mResponse, mIndicatorColor);
                //first item in adapter is the header.. this is hacky, but it works.
                mIndicatorTitle.setText(getAdapterPosition()+1 + ". " + mPriority.getIndicator().getTitle());

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
    }
}
