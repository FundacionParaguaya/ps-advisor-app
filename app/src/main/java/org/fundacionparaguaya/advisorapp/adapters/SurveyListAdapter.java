package org.fundacionparaguaya.advisorapp.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.Survey;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alex on 2/12/2018.
 */

public class SurveyListAdapter extends RecyclerView.Adapter<SurveyListAdapter.SurveySelectViewHolder> {

    private Context mContext;

    private ArrayList<Survey> mSurveyList;

    private SurveyClickListener mClickListener;

    private HashMap<Survey, SurveySelectViewHolder> viewHolderHashMap = new HashMap<>();

    public SurveyListAdapter(Context context, ArrayList<Survey> arrayList) {
        mContext = context;
        if (arrayList != null) {
            mSurveyList = arrayList;
        } else {
            mSurveyList = new ArrayList<>();
        }
    }

    public void setSurveyList(ArrayList<Survey> list){
        mSurveyList = list;
    }

    public ArrayList<Survey> getSurveyList(){
        return mSurveyList;
    }

    public HashMap<Survey, SurveySelectViewHolder> getViewHolderHashMap(){
        return viewHolderHashMap;
    }

    @Override
    public SurveySelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_surveyoption, parent, false);
        return new SurveySelectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SurveySelectViewHolder holder, int position) {
        holder.setText(mSurveyList.get(position));
        viewHolderHashMap.put(mSurveyList.get(position), holder);

        //if 1 item in the list, automatically select it
        if (mSurveyList.size() == 1){
            holder.onClick(holder.getView());
        }
    }

    @Override
    public int getItemCount() {
        return mSurveyList.size();
    }

    public void setClickListener(SurveyClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface SurveyClickListener {
        void onItemClick(Survey survey, boolean isSelected);
    }

    public Survey getSurvey(int position) {
        return mSurveyList.get(position);
    }

    public class SurveySelectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitle;
        private TextView mDescription;
        private CardView mCard;

        private View view;

        private boolean isSelected = false;

        public SurveySelectViewHolder(View view) {
            super(view);

            this.view = view;
            mTitle = (TextView) view.findViewById(R.id.surveyoption_title);
            mDescription = (TextView) view.findViewById(R.id.surveyoption_description);
            mCard = (CardView) view.findViewById(R.id.surveyoption);

            view.setOnClickListener(this);
        }

        public void setText(Survey survey){
            mTitle.setText(survey.getTitle());
            mDescription.setText(survey.getDescription());
        }

        public View getView(){
            return view;
        }

        public void setSelected(boolean setSelected){
            if (!setSelected) {
                mCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.app_lightgray));
                mTitle.setTextColor(mContext.getResources().getColor(R.color.app_black));
                mDescription.setTextColor(mContext.getResources().getColor(R.color.app_black));
            } else {
                mCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.app_black));
                mTitle.setTextColor(mContext.getResources().getColor(R.color.app_white));
                mDescription.setTextColor(mContext.getResources().getColor(R.color.app_white));
            }
            this.isSelected = setSelected;
        }

        @Override
        public void onClick(View view) {
            if (isSelected) {
                setSelected(false);
            } else {
                setSelected(true);
            }
            if (mClickListener != null) {
                mClickListener.onItemClick(getSurvey(getAdapterPosition()), isSelected);
            }

        }

    }
}
