package org.fundacionparaguaya.advisorapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.BackgroundQuestionCallback;
import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;

import java.util.List;

/**
 * Adapter for the responses on the review page.
 */

public class SurveyQuestionReviewAdapter extends RecyclerView.Adapter
{
    private BackgroundQuestionCallback mCallback;
    private List<BackgroundQuestion> mQuestions;

    SurveyQuestionReviewAdapter(List<BackgroundQuestion> questions, BackgroundQuestionCallback c)
    {
        super();
        mCallback = c;
        mQuestions = questions;

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_questionsreview_response, parent, false);

        return new QuestionResponseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BackgroundQuestion q = mQuestions.get(position);
        String response = mCallback.getResponseFor(q);

        ((QuestionResponseViewHolder)holder).setFields(q, response);
    }

    @Override
    public int getItemCount() {

        if(mQuestions==null)
        {
            return 0;
        }
        else return mQuestions.size();
    }

    static class QuestionResponseViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvQuestion;
        private TextView mTvResponse;

        QuestionResponseViewHolder(View itemView) {
            super(itemView);

            mTvResponse = itemView.findViewById(R.id.tv_questionresponse_response);
            mTvQuestion = itemView.findViewById(R.id.tv_questionresponse_question);
        }

        void setFields(BackgroundQuestion q, String response)
        {
            mTvQuestion.setText(q.getDescription());

            if(response==null || response.length()==0)
            {
                mTvResponse.setText(R.string.surveyreview_noresponse);
            }
            else mTvResponse.setText(response);
        }
    }

}