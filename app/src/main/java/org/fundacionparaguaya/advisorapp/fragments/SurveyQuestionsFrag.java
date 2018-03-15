package org.fundacionparaguaya.advisorapp.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.activities.SurveyActivity;
import org.fundacionparaguaya.advisorapp.adapters.SurveyQuestionAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.QuestionCallback;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.ReviewCallback;
import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;
import org.fundacionparaguaya.advisorapp.viewcomponents.NonSwipeableViewPager;

import java.util.Map;

/**
 * Questions about Personal and Economic questions that are asked before the survey
 */

public abstract class SurveyQuestionsFrag extends AbstractSurveyFragment implements Observer<Map<BackgroundQuestion, String>>, ReviewCallback, QuestionCallback<BackgroundQuestion, String> {

    protected SurveyQuestionAdapter mQuestionAdapter;
    private ImageButton mNextButton;
    private ImageButton mBackButton;
    private NonSwipeableViewPager mViewPager;
    private SurveyActivity mActivity;

    protected int mCurrentIndex = 0;

    public SurveyQuestionsFrag() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (SurveyActivity)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity=null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQuestionAdapter = new SurveyQuestionAdapter(getChildFragmentManager());
        initQuestionList();
    }

    protected void initQuestionList() {
        mQuestionAdapter.setQuestionsList(getQuestions());
        checkViewConditions();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_surveyquestions, container, false);

        mViewPager = view.findViewById(R.id.surveyquestion_viewpager);
        mViewPager.setAdapter(mQuestionAdapter);

        mBackButton = view.findViewById(R.id.btn_questionall_back);
        mBackButton.setOnClickListener(this::onBack);

        mNextButton = view.findViewById(R.id.btn_questionall_next);
        mNextButton.setOnClickListener(this::onNext);

        return view;
    }

    public void onNext(View v) {
        if (mCurrentIndex < mQuestionAdapter.getCount() - 1 && questionRequirementsSatisfied(mCurrentIndex)){
            mCurrentIndex = mCurrentIndex + 1;
            goToQuestion(mCurrentIndex);
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
            mNextButton.setVisibility(View.INVISIBLE);
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

        if(mCurrentIndex > 0)
        {
            TransitionManager.beginDelayedTransition((ViewGroup)getView());
            mBackButton.setVisibility(View.VISIBLE);
        }
        else
        {
            TransitionManager.beginDelayedTransition((ViewGroup)getView());
            mBackButton.setVisibility(View.INVISIBLE);
        }

        updateRequirementsSatisfied(); //update whether or not the question needs to be answered

        if (mCurrentIndex == mQuestionAdapter.getCount()-1){ //if review page
            mActivity.hideFooter();
        }
        else { //if a question
            if(isShowFooter() && !KeyboardVisibilityEvent.isKeyboardVisible(getActivity())) mActivity.showFooter();
        }
    }
}
