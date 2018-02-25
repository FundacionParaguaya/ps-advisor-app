package org.fundacionparaguaya.advisorapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.SurveyQuestionAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.QuestionCallback;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.ReviewCallback;
import org.fundacionparaguaya.advisorapp.viewcomponents.NonSwipeableViewPager;

/**
 * Questions about Personal and Economic questions that are asked before the survey
 */

public abstract class SurveyQuestionsFrag extends AbstractSurveyFragment implements ReviewCallback, QuestionCallback {

    protected SurveyQuestionAdapter mQuestionAdapter;
    private ImageButton mNextButton;
    private ImageButton mBackButton;
    private NonSwipeableViewPager mViewPager;

    protected int mCurrentIndex = 0;

    public SurveyQuestionsFrag() {
        super();

        //sets colors for parent activity (set by parent activity in SurveyActivity.switchSurveyFrag)
        setFooterColor(R.color.survey_darkyellow);
        setHeaderColor(R.color.survey_darkyellow);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQuestionAdapter = new SurveyQuestionAdapter(getChildFragmentManager());

        initQuestionList();
    }

    protected void initQuestionList() {
        mQuestionAdapter.setQuestionsList(getQuestions());
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

    protected boolean questionRequirementsSatisfied(int index)
    {
        return (!getQuestions().get(index).isRequired() || getResponse(getQuestions().get(index)) != null);
    }

    public void onBack(View v) {
        if (mCurrentIndex != 0) {
            mCurrentIndex = mCurrentIndex - 1;
            goToQuestion(mCurrentIndex);
        }
    }

    protected void goToQuestion(int index) {
        mViewPager.setCurrentItem(index);
        checkConditions();
    }

    protected void checkConditions() {
        if (mCurrentIndex > 0 && mQuestionAdapter.getCount() > 0 && !mQuestionAdapter.shouldKeepKeyboardFor(mCurrentIndex)) {
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }

        if (mCurrentIndex == mQuestionAdapter.getCount()){
            mBackButton.setVisibility(View.VISIBLE);
        } else if (mCurrentIndex == 0) {
            mBackButton.setVisibility(View.INVISIBLE);
        } else {
            mBackButton.setVisibility(View.VISIBLE);
        }

        if (mCurrentIndex == mQuestionAdapter.getCount()-1){ //if review page
            mNextButton.setVisibility(View.INVISIBLE);
        } else if (questionRequirementsSatisfied(mCurrentIndex)) {
            mNextButton.setVisibility(View.VISIBLE);
        } else {
            mNextButton.setVisibility(View.INVISIBLE);
        }
    }
}
