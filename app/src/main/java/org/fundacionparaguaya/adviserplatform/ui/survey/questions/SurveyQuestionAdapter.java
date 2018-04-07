package org.fundacionparaguaya.adviserplatform.ui.survey.questions;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import org.fundacionparaguaya.adviserplatform.data.model.BackgroundQuestion;

import java.util.List;

public class SurveyQuestionAdapter extends FragmentStatePagerAdapter  {

    private final static int STRING_INPUT = 1;
    private final static int LOCATION_INPUT = 2;
    private final static int PHOTO_INPUT = 3;
    private final static int DROPDOWN_INPUT = 4;
    private final static int DATE_INPUT = 5;
    private final static int REVIEW_PAGE = 6;

    private boolean mHideQuestions = false; //TODO see issue #445
    private List<BackgroundQuestion> mQuestionsList;

    public SurveyQuestionAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public void setQuestionsList(List<BackgroundQuestion> questionsList)
    {
        mQuestionsList = questionsList;
        notifyDataSetChanged();
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

        if(position == getCount()-1){
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
                        viewHolderType = LOCATION_INPUT;
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
        QuestionFragment questionFragment;

        switch (getItemViewType(position))
        {
            case STRING_INPUT:
                questionFragment = QuestionFragment.build(QuestionFragment.TextQuestionFrag.class, position);
                break;

            case LOCATION_INPUT:
                questionFragment = QuestionFragment.build(QuestionFragment.LocationQuestionFrag.class, position);
                break;

            case DATE_INPUT:
                questionFragment = QuestionFragment.build(QuestionFragment.DateQuestionFrag.class, position);
                break;

            case DROPDOWN_INPUT:
                questionFragment = QuestionFragment.build(QuestionFragment.DropdownQuestionFrag.class, position);
                break;

            case PHOTO_INPUT:
                questionFragment = QuestionFragment.build(QuestionFragment.PictureQuestionFrag.class, position);
                break;

            case REVIEW_PAGE:
                QuestionFragment.ReviewPageFragment reviewPageFragment = new QuestionFragment.ReviewPageFragment();
                return reviewPageFragment;

            default:
                questionFragment = null;
                break;
        }

        return questionFragment;
    }

    //https://stackoverflow.com/questions/30080045/fragmentpageradapter-notifydatasetchanged-not-working
    @Override
    public int getItemPosition(@NonNull Object object) {
        if (mHideQuestions) return POSITION_NONE; //TODO see issue #445
        else return super.getItemPosition(object);
    }

    /**
     * Hides all of the questions in a section and only shows the review page
     * //TODO see issue #445
     */
    public void hideQuestions()
    {
        mHideQuestions = true;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(mHideQuestions) return 1; //TODO see issue #445
        else if(mQuestionsList == null) return 0; //if no questions, no submit button
        else return mQuestionsList.size() +  1; //+1 for the review fragment button
    }
}

