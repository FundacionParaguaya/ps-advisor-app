package org.fundacionparaguaya.adviserplatform.ui.survey.indicators;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IndicatorsSummaryAdapter extends RecyclerView.Adapter<IndicatorsSummaryAdapter.IndicatorViewHolder> {

    IndicatorClickListener mClickListener;

    List<IndicatorQuestion> mSkippedIndicators;

    @Override
    public int getItemCount(){
        if(mSkippedIndicators != null) return mSkippedIndicators.size();
        else return 0;
    }

    public String getIndicatorName(int i){
        return mSkippedIndicators.get(i).getIndicator().getTitle();
    }

    public void setSkippedIndicators(Set<IndicatorQuestion> skippedIndicators)
    {
        mSkippedIndicators = new ArrayList<>();
        mSkippedIndicators.addAll(skippedIndicators);

        notifyDataSetChanged();
    }

    public IndicatorQuestion getValue(int i)
    {
        return mSkippedIndicators.get(i);
    }

    @Override
    public IndicatorViewHolder onCreateViewHolder(ViewGroup viewGroup, int position){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_surveysummary_row, viewGroup, false);
        return new IndicatorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(IndicatorViewHolder viewHolder, int position) {
        viewHolder.indicatorName.setText(getIndicatorName(position));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }


    public class IndicatorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardView;
        TextView indicatorName;

        public IndicatorViewHolder(View view){
            super(view);
            cardView = view.findViewById(R.id.surveysummary_skippedindicatorcard);
            indicatorName = view.findViewById(R.id.surveysummary_skippedindicatortext);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

    }

    public void setClickListener(IndicatorClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface IndicatorClickListener {
        void onItemClick(View v, int position);
    }


}
