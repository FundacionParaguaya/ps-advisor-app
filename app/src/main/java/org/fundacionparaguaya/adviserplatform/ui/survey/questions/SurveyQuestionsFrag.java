package org.fundacionparaguaya.adviserplatform.ui.survey.questions;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import com.stepstone.stepper.VerificationError;
import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.ui.survey.QuestionCallback;
import org.fundacionparaguaya.adviserplatform.ui.survey.ReviewCallback;
import org.fundacionparaguaya.adviserplatform.data.model.BackgroundQuestion;
import org.fundacionparaguaya.adviserplatform.ui.common.widget.NonSwipeableViewPager;
import org.fundacionparaguaya.adviserplatform.ui.survey.AbstractSurveyFragment;

import java.util.Map;

/**
 * Questions about Personal and Economic questions that are asked before the survey
 */

public abstract class SurveyQuestionsFrag extends AbstractSurveyFragment implements Observer<Map<BackgroundQuestion, String>>,
        ReviewCallback<BackgroundQuestion, String>, QuestionCallback<BackgroundQuestion, String> {

    protected SurveyQuestionAdapter mQuestionAdapter;
    private AppCompatImageView mNextButton;
    private AppCompatImageView mBackButton;
    private NonSwipeableViewPager mViewPager;
    private static final String QUESTION_INDEX_KEY = "QUESTION_KEY";

    protected int mCurrentIndex = 0;

    public SurveyQuestionsFrag() {
        super();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mQuestionAdapter = new SurveyQuestionAdapter(getChildFragmentManager());

        View view = inflater.inflate(R.layout.fragment_surveyquestions, container, false);

        mViewPager = view.findViewById(R.id.surveyquestion_viewpager);
        mViewPager.setAdapter(mQuestionAdapter);

        mBackButton = view.findViewById(R.id.btn_questionall_back);
        mBackButton.setOnClickListener(this::onBack);

        mNextButton = view.findViewById(R.id.btn_questionall_next);
        mNextButton.setOnClickListener(this::onNext);

        if(savedInstanceState!=null)
        {
            mCurrentIndex = savedInstanceState.getInt(QUESTION_INDEX_KEY);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initQuestionList();
    }

    protected void initQuestionList() {
        mQuestionAdapter.setQuestionsList(getQuestions());
        checkViewConditions();
    }

    public void onNext(View v) {
        if (mCurrentIndex < mQuestionAdapter.getCount() - 1 && questionRequirementsSatisfied(mCurrentIndex)){
            mCurrentIndex = mCurrentIndex + 1;
            goToQuestion(mCurrentIndex);
        }
        else if(mCurrentIndex == mQuestionAdapter.getCount()-1) //review page
        {
            onSubmit();
        }
    }

    public void onBack(View v) {
        if (mCurrentIndex != 0) {
            mCurrentIndex = mCurrentIndex - 1;
            goToQuestion(mCurrentIndex);
        }
    }

    protected void goToQuestion(int index) {
        mViewPager.setCurrentItem(index);
        checkViewConditions();
    }

    protected boolean questionRequirementsSatisfied(int index)
    {
        return (!getQuestions().get(index).isRequired() || getResponse(getQuestions().get(index)) != null);
    }

    /**
     * Should be called as the response to a question updates. Determines whether or not the question has been
     * answered (or not answered, if the question can be skipped) and changes the state of the next button
     * accordingly
     */
    protected void updateRequirementsSatisfied()
    {
        if(mCurrentIndex == mQuestionAdapter.getCount() -1) //if a review page hide the next button
        {
            mNextButton.setVisibility(View.VISIBLE);
        }
        else if (questionRequirementsSatisfied(mCurrentIndex)) {
            mNextButton.setVisibility(View.VISIBLE);
        }
        else mNextButton.setVisibility(View.INVISIBLE);
    }

    /**
     * This function is called when the user's responses to questions change.
     * @param backgroundQuestionStringMap Responses
     */
    @Override
    public void onChanged(@Nullable Map<BackgroundQuestion, String> backgroundQuestionStringMap) {
        updateRequirementsSatisfied(); //check if the current question has been satisfied
    }

    protected void checkViewConditions() {
        if (mCurrentIndex > 0 && mQuestionAdapter.getCount() > 0 && !mQuestionAdapter.shouldKeepKeyboardFor(mCurrentIndex)) {
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }

        if (mCurrentIndex > 0) {
            TransitionManager.beginDelayedTransition((ViewGroup) getView());
            mBackButton.setVisibility(View.VISIBLE);
        }
        else {
            TransitionManager.beginDelayedTransition((ViewGroup) getView());
            mBackButton.setVisibility(View.INVISIBLE);
        }

        updateRequirementsSatisfied(); //update whether or not the question needs to be answered
    }

    private boolean allQuestionsSatisifed()
    {
        boolean allSatisified = true;

        for(int i=0; i<getQuestions().size(); i++)
        {
            allSatisified &= questionRequirementsSatisfied(i);
        }

        return allSatisified;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(QUESTION_INDEX_KEY, mCurrentIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public VerificationError verifyStep() {
        if(allQuestionsSatisifed()) return null;
        else return new VerificationError("You big dummy");
    }
}
