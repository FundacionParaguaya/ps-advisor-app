package org.fundacionparaguaya.advisorapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import com.yarolegovich.discretescrollview.transform.DiscreteScrollItemTransformer;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.QuestionFragment;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.BackgroundQuestionCallback;
import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;

import java.util.List;

import static java.lang.String.format;

public class SurveyQuestionAdapter extends FragmentStatePagerAdapter {

    private final static int STRING_INPUT = 1;
    private final static int LOCATION_INPUT = 2;
    private final static int PHOTO_INPUT = 3;
    private final static int DROPDOWN_INPUT = 4;
    private final static int DATE_INPUT = 5;
    private final static int REVIEW_PAGE = 6;

    private List<BackgroundQuestion> mQuestionsList;

    private BackgroundQuestionCallback mCallback;

    public SurveyQuestionAdapter(BackgroundQuestionCallback callback, FragmentManager fm){
        super(fm);

        mCallback = callback;
    }

    public void setQuestionsList(List<BackgroundQuestion> questionsList)
    {
        mQuestionsList = questionsList;
        notifyDataSetChanged();
    }

    public BackgroundQuestion getQuestion(int adapterPosition)
    {
        return mQuestionsList.get(adapterPosition);
    }

    /** Whether or not the keyboard should stay open for a viewholder at this position
     *
     * @param position Position of the viewholder
     * @return whether this viewholder takes text input
     */

    public boolean shouldKeepKeyboardFor(int position)
    {
        return (getItemViewType(position) == STRING_INPUT);
    }

    public int getItemViewType(int position) {

        if(position == mQuestionsList.size()){
            return REVIEW_PAGE;
        }
        else {
            BackgroundQuestion question = mQuestionsList.get(position);

            if (question.getOptions() != null && question.getOptions().size() > 1) {
                return DROPDOWN_INPUT;
            }
            else {
                int viewHolderType;

                switch (question.getResponseType()) {
                    case STRING:
                    case PHONE_NUMBER:
                    case INTEGER:
                        viewHolderType = STRING_INPUT;
                        break;
                    case DATE:
                        viewHolderType = DATE_INPUT;
                        break;
                    case PHOTO:
                        viewHolderType = PHOTO_INPUT;
                        break;
                    case LOCATION:
                        viewHolderType = STRING_INPUT; // TODO: implement LOCATION_INPUT;
                        break;
                    default:
                        viewHolderType = -1;
                        break;
                }

                return viewHolderType;
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        QuestionFragment questionFragment = null;

        switch (getItemViewType(position))
        {
            case STRING_INPUT:
                questionFragment = new QuestionFragment.TextQuestionFrag();
                break;

            case LOCATION_INPUT:
                questionFragment = new QuestionFragment.LocationQuestionFrag();
                break;

            case DATE_INPUT:
                questionFragment = new QuestionFragment.DateQuestionFrag();
                break;

            case DROPDOWN_INPUT:
                questionFragment = new QuestionFragment.DropdownQuestionFrag();
                break;

            case REVIEW_PAGE:
                return new QuestionFragment.ReviewPageViewHolder();
        }

        if(questionFragment!=null)
        {
            questionFragment.setCallback(mCallback);
            questionFragment.setQuestion(mQuestionsList.get(position));
        }

        return questionFragment;
    }

    @Override
    public int getCount() {
        if(mQuestionsList == null) return 0; //if no questions, no submit button
        else return mQuestionsList.size(); //+1 for the submit button
    }


    /**Fades the questions that are not centered in the Discrete Scroll View**/
    public static class QuestionFadeTransformer implements DiscreteScrollItemTransformer
    {
        @Override
        public void transformItem(View item, float position) {
            //pos inbetween -1 and 1, inclusive

            //first normalize so between 0 and 1
            //1 is max value

            if (item.getId() != R.id.submit_button_view) {
                float absPosition = Math.abs(position);
                absPosition = 1 - absPosition; //flip value.. so 1 is max

                float output = (absPosition); //in between 100% and 20% output

                item.setAlpha(output);
            }
        }
    }

}

