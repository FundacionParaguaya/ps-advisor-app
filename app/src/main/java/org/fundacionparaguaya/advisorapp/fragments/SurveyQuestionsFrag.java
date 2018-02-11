package org.fundacionparaguaya.advisorapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.SurveyQuestionAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.BackgroundQuestionCallback;
import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

/**
 * Questions about Personal and Economic questions that are asked before the survey
 */

public abstract class SurveyQuestionsFrag extends AbstractSurveyFragment implements BackgroundQuestionCallback {

    protected DiscreteScrollView mDsvQuestionList;
    protected SurveyQuestionAdapter mQuestionAdapter;
    protected SharedSurveyViewModel mSharedSurveyViewModel;

    private ImageButton mBackButton;
    private ImageButton mNextButton;

    private int mCurrentIndex = 0;

    public SurveyQuestionsFrag()
    {
        super();

        //sets colors for parent activity (set by parent activity in SurveyActivity.switchSurveyFrag)
        setFooterColor(R.color.survey_darkyellow);
        setHeaderColor(R.color.survey_darkyellow);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initQuestionList();
    }

    abstract protected void initQuestionList();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_surveyquestions, container, false);

        mDsvQuestionList = view.findViewById(R.id.rv_survey_questions);

        mDsvQuestionList.setAdapter(mQuestionAdapter);

        mDsvQuestionList.setSlideOnFling(true);
        mDsvQuestionList.setSlideOnFlingThreshold(2500);

        mDsvQuestionList.setItemTransformer(new SurveyQuestionAdapter.QuestionFadeTransformer());

        mDsvQuestionList.setRecyclerListener((holder) ->
        {
                if(holder instanceof SurveyQuestionAdapter.QuestionViewHolder)
                {
                   SurveyQuestionAdapter.QuestionViewHolder questionHolder=
                           (SurveyQuestionAdapter.QuestionViewHolder)holder;

                    if(questionHolder.itemView.hasFocus())
                    {
                        questionHolder.itemView.clearFocus(); //we can put it inside the second if as well, but it makes sense to do it to all scraped views
                        //Optional: also hide keyboard in that case
                        if ( questionHolder instanceof SurveyQuestionAdapter.TextQuestionViewHolder) {
                            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            }
        );

        mBackButton = view.findViewById(R.id.btn_questionall_back);
        mBackButton.setOnClickListener(this::onBack);

        mNextButton = view.findViewById(R.id.btn_questionall_next);
        mNextButton.setOnClickListener(this::onNext);

        mDsvQuestionList.addOnItemChangedListener((viewHolder, adapterPosition) -> {
            mCurrentIndex = adapterPosition;
            checkConditions();

            mBackButton.setEnabled(true);
            mNextButton.setEnabled(true);

            if(viewHolder!=null)
            {
                viewHolder.itemView.requestFocus();
            }
        });

        mQuestionAdapter = new SurveyQuestionAdapter(this);
        mDsvQuestionList.setAdapter(mQuestionAdapter);

        return view;
    }

    @Override
    public void onQuestionAnswered(BackgroundQuestion q, Object response) {
        try {
            //all responses to questions (for now) should be strings
            mSharedSurveyViewModel.addBackgroundResponse(q, (String)response);
            mQuestionAdapter.updateReviewPage();
        }
        catch (ClassCastException e)
        {
            Log.e(this.getClass().getName(), e.getMessage());
        }
    }

    @Override
    public void onNext(View v) {
        goToQuestion(mCurrentIndex+1);
    }

    @Override
    public void onBack(View v) {
        goToQuestion(mCurrentIndex-1);
    }

    protected void goToQuestion(int index)
    {
        View currentFocus;
        mDsvQuestionList.stopScroll();

        if(getActivity()!=null && (currentFocus=getActivity().getCurrentFocus())!=null)
        {
            currentFocus.clearFocus();
        }

        if(index >= 0 && index< mQuestionAdapter.getItemCount())
        {
            mDsvQuestionList.stopScroll();
            mDsvQuestionList.scrollToPosition(mCurrentIndex);
            mDsvQuestionList.smoothScrollToPosition(index);

            if(!mQuestionAdapter.shouldKeepKeyboardFor(index))
            {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }

            mBackButton.setEnabled(false);
            mNextButton.setEnabled(false);

            mCurrentIndex = index;

            checkConditions();
        }
    }

    protected void checkConditions()
    {
        if(mCurrentIndex > 0 && mQuestionAdapter.getItemCount()>0 && !mQuestionAdapter.shouldKeepKeyboardFor(mCurrentIndex))
        {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }

        if(mCurrentIndex==0)
        {
            mBackButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            mBackButton.setVisibility(View.VISIBLE);
        }

        if(mCurrentIndex==mQuestionAdapter.getItemCount()-1)
        {
            mNextButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            mNextButton.setVisibility(View.VISIBLE);
        }
    }
}
