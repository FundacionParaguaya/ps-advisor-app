package org.fundacionparaguaya.advisorapp.adapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.Indicator;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.util.IndicatorUtilities;
import org.zakariya.stickyheaders.SectioningAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Adapter for Priorities List Fragment
 *
 */

public class PrioritiesListAdapter extends RecyclerView.Adapter<PrioritiesListAdapter.PrioritiesListViewHolder> {

    private List<LifeMapPriority> mPriorities = new ArrayList<>();

    private ArrayList<PrioritiesListViewHolder> mViewHolderList = new ArrayList<>();

    private Snapshot mSelectedSnapshot;

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

        holder.setIndicator(IndicatorUtilities.getResponseForIndicator(
                mSelectedSnapshot.getPriorities().get(position).getIndicator(),
                mSelectedSnapshot.getIndicatorResponses()), position + 1);

        mViewHolderList.add(holder);
    }

    @Override
    public int getItemCount() {
        return mPriorities.size();
    }

    private void setSelected(int index){
        IndicatorOption indicator = mViewHolderList.get(index).getIndicator();

        for (PrioritiesListViewHolder viewHolder : mViewHolderList){
            if (viewHolder.getIndicator().equals(indicator)){
                viewHolder.setSelected(true);
            } else {
                viewHolder.setSelected(false);
            }
        }
    }

    static class PrioritiesListViewHolder extends RecyclerView.ViewHolder{
        private View mView;

        private ConstraintLayout mLayout;
        private TextView mIndicatorTitle;
        private AppCompatImageView mIndicatorColor;

        private boolean isSelected;

        private IndicatorOption mIndicator;
        PrioritiesListViewHolder(View view) {
            super(view);
            mView = view;
            mLayout = view.findViewById(R.id.item_familydetail_prioritieslist);
            mIndicatorTitle = view.findViewById(R.id.familydetail_prioritieslist_item_text);
            mIndicatorColor = view.findViewById(R.id.familydetail_prioritieslist_item_indicatorcolor);
        }

        void setIndicator(IndicatorOption indicator, int index){
            mIndicator = indicator;

            String title = index + ". " + mIndicator.getIndicator().getTitle();
            mIndicatorTitle.setText(title);

            IndicatorUtilities.setViewColorFromResponse(mIndicator, mIndicatorColor);
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