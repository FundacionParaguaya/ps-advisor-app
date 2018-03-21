package org.fundacionparaguaya.advisorapp.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fundacionparaguaya.advisorapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class SurveySummaryAdapter extends RecyclerView.Adapter<SurveySummaryAdapter.IndicatorViewHolder> {

    List<String> indicatorNames = new ArrayList<>();

    Context mContext;

    InterfaceClickListener mClickListener;

    public SurveySummaryAdapter(Context context, List<String> names){
        this.mContext = context;
        indicatorNames = names;
    }

    @Override
    public int getItemCount(){
        return indicatorNames.size();
    }

    public String getIndicatorName(int i){
        return indicatorNames.get(i);
    }

    @Override
    public IndicatorViewHolder onCreateViewHolder(ViewGroup viewGroup, int position){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_surveysummary_row, viewGroup, false);
        return new IndicatorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(IndicatorViewHolder viewHolder, int position) {
        viewHolder.indicatorName.setText(indicatorNames.get(position));
//        viewHolder.cardView.setOnClickListener;
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
            cardView = (CardView) view.findViewById(R.id.surveysummary_skippedindicatorcard);
            indicatorName = (TextView) view.findViewById(R.id.surveysummary_skippedindicatortext);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

    }

    public void setClickListener(InterfaceClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface InterfaceClickListener {
        void onItemClick(View v, int position);
    }


}
