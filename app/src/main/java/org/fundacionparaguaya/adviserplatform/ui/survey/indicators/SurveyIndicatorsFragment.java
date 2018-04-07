package org.fundacionparaguaya.adviserplatform.ui.survey.indicators;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.stepstone.stepper.VerificationError;
import org.fundacionparaguaya.adviserplatform.AdviserApplication;
import org.fundacionparaguaya.adviserplatform.R;
import org.fundacionparaguaya.adviserplatform.ui.survey.SurveyActivity;
import org.fundacionparaguaya.adviserplatform.ui.survey.QuestionCallback;
import org.fundacionparaguaya.adviserplatform.ui.survey.ReviewCallback;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorQuestion;
import org.fundacionparaguaya.adviserplatform.ui.common.widget.NonSwipeableViewPager;
import org.fundacionparaguaya.adviserplatform.ui.survey.AbstractSurveyFragment;
import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.adviserplatform.ui.survey.SharedSurveyViewModel;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;


/**
 * Enables user to go through each of the indicators, skip indicators, select a red, yellow, green color etc.
 */

public class SurveyIndicatorsFragment extends AbstractSurveyFragment implements ViewPager.OnPageChangeListener,
        QuestionCallback<IndicatorQuestion, IndicatorOption>, ReviewCallback<IndicatorQuestion, IndicatorOption> {

    private SurveyIndicatorAdapter mAdapter;
    private NonSwipeableViewPager mPager;

    protected LinearLayout mBackButton;
    protected TextView mBackButtonText;
    protected AppCompatImageView mBackButtonImage;

    protected LinearLayout mSkipButton;
    protected TextView mSkipButtonText;
    protected AppCompatImageView mSkipButtonImage;

    private AppCompatTextView mQuestionText;

    private boolean isPageChanged = true;

    @Inject
    InjectionViewModelFactory mViewModelFactory;

    SharedSurveyViewModel mSurveyViewModel;

    private static int clickDelay = 500;
    private static int clickDelayInterval = 100;

    private CountDownTimer nextPageTimer;

    @Override
    public void onCreate(@Nullable Bundle savedInsanceState) {
        ((AdviserApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSurveyViewModel = ViewModelProviders
                .of((SurveyActivity)getContext(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        super.onCreate(savedInsanceState);

        setTitle("");
        setShowHeader(false);
        setShowFooter(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surveyindicators, container, false);

        mAdapter = new SurveyIndicatorAdapter(getChildFragmentManager(), mSurveyViewModel.getSurveyInProgress().getIndicatorQuestions());
        mPager = (NonSwipeableViewPager) view.findViewById(R.id.indicatorsurvey_viewpager);
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(1);
        mPager.addOnPageChangeListener(this);

        mQuestionText = view.findViewById(R.id.indicatorsurvey_questiontext);

        mBackButton = view.findViewById(R.id.indicatorsurvey_backbutton);
        mBackButtonText = view.findViewById(R.id.indicatorsurvey_backbuttontext);
        mBackButtonImage = view.findViewById(R.id.indicatorsurvey_backbuttonimage);

        mSkipButton = view.findViewById(R.id.indicatorsurvey_skipbutton);
        mSkipButtonText = view.findViewById(R.id.indicatorsurvey_skipbuttontext);
        mSkipButtonImage = view.findViewById(R.id.indicatorsurvey_skipbuttonimage);

        mBackButton.setOnClickListener(v -> previousQuestion());

        mSkipButton.setOnClickListener(v -> {
            int index = mPager.getCurrentItem();

            boolean canProceed = !isReviewPage(index) &&  //not a review page and...
                    (!mAdapter.getQuestion(mPager.getCurrentItem()).isRequired() || //not required
                            mSurveyViewModel.hasIndicatorResponse(mPager.getCurrentItem())); //or has an answer

            if(canProceed && !mSurveyViewModel.hasIndicatorResponse(mPager.getCurrentItem()))
            {
                mSurveyViewModel.setIndicatorResponse(mPager.getCurrentItem(), null);
            }

            if(canProceed){
                nextQuestion();
            }
        });

        checkConditions();
        return view;
    }

    private boolean isReviewPage(int index)
    {
        return index==mAdapter.getCount()-1;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSurveyViewModel.FocusedQuestion().observe(this, question -> {
            if(question!=null)
            {
                int index = mSurveyViewModel.getSurveyInProgress().getIndicatorQuestions().indexOf(question);

                if(index != mPager.getCurrentItem())
                {
                    mPager.setCurrentItem(index);
                    checkConditions();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void nextQuestion() {
        if (isPageChanged) {
            if (mPager.getCurrentItem() == mAdapter.getCount() - 2 && mSurveyViewModel.getSkippedIndicators().size() == 0) //last question before review page and no skipped indicators
            {
                mSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.LIFEMAP);
            }
            else if (mPager.getCurrentItem() < mAdapter.getCount() - 1) {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                checkConditions();
            }
        }
    }

    public void previousQuestion() {
        if (isPageChanged) {
            if (mPager.getCurrentItem() < 1) {
                //Goes back when on the first survey question
                mSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.ECONOMIC_QUESTIONS);
            } else {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                checkConditions();
            }
        }
    }

    public void checkConditions() {
        if (!isReviewPage(mPager.getCurrentItem())) { //if not the review page
            if(mSkipButton.getVisibility()!=View.VISIBLE) {
                TransitionManager.beginDelayedTransition((ViewGroup)this.getView());
                mBackButton.setVisibility(View.VISIBLE);
                mSkipButton.setVisibility(View.VISIBLE);
                mQuestionText.setVisibility(View.VISIBLE);
            }

            if (mSurveyViewModel.hasIndicatorResponse(mPager.getCurrentItem())) {
                mSkipButtonText.setText(R.string.navigate_next);
                mSkipButtonImage.setVisibility(View.VISIBLE);
                setSkippable(false);

            } else if (mAdapter.getQuestion(mPager.getCurrentItem()).isRequired()) {
                mSkipButtonText.setText(R.string.all_required);
                mSkipButtonImage.setVisibility(View.GONE);
                setSkippable(false);

            } else {
                mSkipButtonText.setText(R.string.navigate_skip);
                mSkipButtonImage.setVisibility(View.VISIBLE);
                setSkippable(true);
            }

            String question =   (mPager.getCurrentItem() + 1) + ". " +
                    mAdapter.getQuestion(mPager.getCurrentItem()).getDescription();

            mQuestionText.setText(question);
            mSurveyViewModel.setFocusedIndicator(mPager.getCurrentItem());
        }
        else //is review page
        {
            TransitionManager.beginDelayedTransition((ViewGroup) this.getView());
            mBackButton.setVisibility(View.INVISIBLE);
            mSkipButton.setVisibility(View.INVISIBLE);
            mQuestionText.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("RestrictedApi")
    private void setSkippable(boolean skippable)
    {
        if(skippable)
        {
            mSkipButtonText.setTextColor(ContextCompat.getColor(getContext(), R.color.app_orange));
            mSkipButtonImage.setSupportImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.app_orange)));
        }
        else
        {
            mSkipButtonText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            mSkipButtonImage.setSupportImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
        }
    }

    /**
     * Checks to make sure that all indicators have either been answered or skipped
     */
    @Override
    public VerificationError verifyStep() {
        boolean allIndicatorsSatisfied = true;

        for (IndicatorQuestion question: mSurveyViewModel.getSurveyInProgress().getIndicatorQuestions())
        {
            allIndicatorsSatisfied &=
                    (mSurveyViewModel.hasResponse(question) || mSurveyViewModel.getSkippedIndicators().contains(question));
        }

        if(allIndicatorsSatisfied)
        {
            return null;
        }
        else
        {
            return new VerificationError("There are more indicators left!"); //TODO show toast
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //For future implementation
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                isPageChanged = true;
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                isPageChanged = false;
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                isPageChanged = false;
                break;
            default:
                isPageChanged = false;
                break;
        }

    }

    //region Callback for ChooseIndicatorFragment
    @Override
    public IndicatorQuestion getQuestion(int i) {
        return mSurveyViewModel.getIndicator(i);
    }

    @Override
    public IndicatorOption getResponse(IndicatorQuestion question) {
        return mSurveyViewModel.getResponseForIndicator(question);
    }

    @Override
    public void onResponse(IndicatorQuestion question, IndicatorOption s) {
        mSurveyViewModel.setIndicatorResponse(question, s);

        if (nextPageTimer != null) {
            nextPageTimer.cancel();
            nextPageTimer = null;
        }
        else if(s!=null){
            nextPageTimer = new CountDownTimer(clickDelay, clickDelayInterval) {
                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    nextPageTimer = null;
                    nextQuestion();
                }
            }.start();
        }

        if(nextPageTimer==null) checkConditions(); //if we're not currently waiting on the page to change
    }
    //endregion

    @Override
    public void onSubmit() {
        mSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.LIFEMAP);
    }

    @Override
    public List<IndicatorQuestion> getQuestions() {
        return mSurveyViewModel.getSurveyInProgress().getIndicatorQuestions();
    }

    @Override
    public LiveData<Map<IndicatorQuestion, IndicatorOption>> getResponses() {
        return mSurveyViewModel.getIndicatorResponses();
    }
}
