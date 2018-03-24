package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.stepstone.stepper.VerificationError;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.activities.SurveyActivity;
import org.fundacionparaguaya.advisorapp.adapters.SurveyIndicatorAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.QuestionCallback;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.ReviewCallback;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.viewcomponents.NonSwipeableViewPager;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

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
    protected ImageView mBackButtonImage;

    protected LinearLayout mSkipButton;
    protected TextView mSkipButtonText;
    protected ImageView mSkipButtonImage;

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
        ((AdvisorApplication) getActivity().getApplication())
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

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

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
            if (mAdapter.getQuestion(mPager.getCurrentItem()).isRequired()) {
                if (mSurveyViewModel.hasIndicatorResponse(mPager.getCurrentItem())) {
                    nextQuestion();
                }
            } else {
                if (mSurveyViewModel.hasIndicatorResponse(mPager.getCurrentItem())) {
                    nextQuestion();
                } else {
                    mSurveyViewModel.setIndicatorResponse(mPager.getCurrentItem(), null);
                    nextQuestion();
                }
            }
        });

        checkConditions();
        return view;
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
        if (mPager.getCurrentItem() != mAdapter.getCount() - 1) { //if not the review page
            if(mSkipButton.getVisibility()!=View.VISIBLE) {
                TransitionManager.beginDelayedTransition((ViewGroup)this.getView());
                mBackButton.setVisibility(View.VISIBLE);
                mSkipButton.setVisibility(View.VISIBLE);
                mQuestionText.setVisibility(View.VISIBLE);
            }

            if (mSurveyViewModel.hasIndicatorResponse(mPager.getCurrentItem())) {
                mSkipButtonText.setText(R.string.navigate_next);
                mSkipButtonImage.setVisibility(View.VISIBLE);
            } else if (mAdapter.getQuestion(mPager.getCurrentItem()).isRequired()) {
                mSkipButtonText.setText(R.string.all_required);
                mSkipButtonImage.setVisibility(View.GONE);
            } else {
                mSkipButtonText.setText(R.string.navigate_skip);
                mSkipButtonImage.setVisibility(View.VISIBLE);
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
//        if(position < mAdapter.getCount())
//        {
//
//        }
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
        checkConditions();
        if (nextPageTimer != null) {
            nextPageTimer.cancel();
            nextPageTimer = null;
        } else {
            nextPageTimer = new CountDownTimer(clickDelay, clickDelayInterval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //For future implementation if needed
                }

                @Override
                public void onFinish() {
                    if (s != null) {
                        nextQuestion();
                    }
                    nextPageTimer = null;
                }
            }.start();
        }
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
