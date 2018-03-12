package org.fundacionparaguaya.advisorapp.adapters;

import android.renderscript.RenderScript;
import android.support.constraint.ConstraintLayout;
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

    private ArrayList<SelectedPriorityHandler> mPrioritySelectedHandlers = new ArrayList<>();

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

        holder.setupViewHolder(mPriorities.get(position),
                IndicatorUtilities.getResponseForIndicator(
                mSelectedSnapshot.getPriorities().get(position).getIndicator(),
                mSelectedSnapshot.getIndicatorResponses()), position + 1);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(position);
                notifyHandlers(mSelectedPriority);
            }
        });

        mViewHolderList.add(holder);

        if (mSelectedPriority == null && mViewHolderList.size() == 1) {
            holder.setSelected(true);
        }
    }

    @Override
    public int getItemCount() {
        return mPriorities.size();
    }

    public void setSelected(int index){
        IndicatorOption indicator = mViewHolderList.get(index).getIndicator();
        mSelectedPriority = mPriorities.get(index);

        //Set only 1 to selected, everything else is not selected
        for (PrioritiesListViewHolder viewHolder : mViewHolderList){
            if (viewHolder.getIndicator().equals(indicator)){
                viewHolder.setSelected(true);
            } else {
                viewHolder.setSelected(false);
            }
        }
    }

    //***** Observer Listener Pattern for ItemSelect **********************************************

    public void addSelectedPriorityHandler(SelectedPriorityHandler handler){
        mPrioritySelectedHandlers.add(handler);
    }

    private void notifyHandlers(LifeMapPriority priority){
        for (SelectedPriorityHandler handler : mPrioritySelectedHandlers){
            handler.onPrioritySelected(new PrioritySelectedEvent(priority));
        }
    }

    public interface SelectedPriorityHandler{
        void onPrioritySelected(PrioritySelectedEvent event);
    }

    public class PrioritySelectedEvent {
        private LifeMapPriority mPriority;
        PrioritySelectedEvent(LifeMapPriority priority){
            this.mPriority = priority;
        }
        public LifeMapPriority getPriority (){
            return mPriority;
        }
    }
    //*********************************************************************************************

    static class PrioritiesListViewHolder extends RecyclerView.ViewHolder{
        View mView;

        private ConstraintLayout mLayout;
        private TextView mIndicatorTitle;
        private AppCompatImageView mIndicatorColor;

        private boolean isSelected;

        private LifeMapPriority mPriority;
        private IndicatorOption mIndicator;

        PrioritiesListViewHolder(View view) {
            super(view);
            mView = view;
            mLayout = view.findViewById(R.id.item_familydetail_prioritieslist);
            mIndicatorTitle = view.findViewById(R.id.familydetail_prioritieslist_item_text);
            mIndicatorColor = view.findViewById(R.id.familydetail_prioritieslist_item_indicatorcolor);
        }

        void setupViewHolder(LifeMapPriority priority, IndicatorOption indicator, int index){
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
                mLayout.setBackgroundColor(mView.getResources()
                        .getColor(R.color.familypriority_list_itemselected));
            } else {
                mLayout.setBackgroundColor(mView.getResources()
                        .getColor(R.color.familypriority_list_itemnotselected));
            }
        }
    }

}