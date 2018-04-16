package org.fundacionparaguaya.adviserplatform.ui.survey;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.fundacionparaguaya.adviserplatform.R;
import org.fundacionparaguaya.adviserplatform.data.model.Survey;

import java.util.List;

/**
 * Adapter for a list of surveys to choose from
 */

public class SurveyListAdapter extends RecyclerView.Adapter<SurveyListAdapter.SurveySelectViewHolder> {

    private List<Survey> mSurveyList;

    private SurveyClickListener mClickListener;

    private Survey mSelectedSurvey;

    void setSurveyList(List<Survey> list){
        mSurveyList = list;

        notifyDataSetChanged();
    }

    @Override
    public SurveySelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_surveyoption, parent, false);
        SurveySelectViewHolder vh = new SurveySelectViewHolder(v);
        vh.setClickListener(mClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(SurveySelectViewHolder holder, int position) {
        holder.bindSurvey(mSurveyList.get(position));

        boolean isPosSelected = mSelectedSurvey!=null && mSelectedSurvey.equals(mSurveyList.get(position));
        holder.setSelected(isPosSelected);
    }

    @Override
    public int getItemCount() {
        return mSurveyList == null ? 0 : mSurveyList.size();
    }

    void setClickListener(SurveyClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    interface SurveyClickListener {
        void onItemClick(Survey survey);
    }

    void setSelectedSurvey(Survey survey)
    {
        mSelectedSurvey = survey;

        if(mSurveyList!=null) notifyDataSetChanged();
    }

    static class SurveySelectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitle;
        private TextView mDescription;
        private CardView mCard;

        private View view;
        private Survey mSurvey;

        private SurveyClickListener mClickListener;

        SurveySelectViewHolder(View view) {
            super(view);

            this.view = view;
            mTitle = view.findViewById(R.id.surveyoption_title);
            mDescription = view.findViewById(R.id.surveyoption_description);
            mCard = view.findViewById(R.id.surveyoption);

            view.setOnClickListener(this);
        }

        private void setClickListener(SurveyClickListener listener)
        {
            this.mClickListener = listener;
        }

        void bindSurvey(Survey survey){
            mTitle.setText(survey.getTitle());
            mDescription.setText(survey.getDescription());
            this.mSurvey = survey;
        }

        public View getView(){
            return view;
        }

        void setSelected(boolean setSelected){
            if (!setSelected) {
                mCard.setCardBackgroundColor(view.getResources().getColor(R.color.app_white));
                mTitle.setTextColor(view.getResources().getColor(R.color.app_black));
                mDescription.setTextColor(view.getResources().getColor(R.color.app_black));
            } else {
                mCard.setCardBackgroundColor(view.getResources().getColor(R.color.colorPrimary));
                mTitle.setTextColor(view.getResources().getColor(R.color.app_white));
                mDescription.setTextColor(view.getResources().getColor(R.color.app_white));
            }
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(mSurvey);
            }
        }
    }
}
