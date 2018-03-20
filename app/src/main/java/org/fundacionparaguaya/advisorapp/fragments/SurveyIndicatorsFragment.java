package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.SurveyIndicatorAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.QuestionCallback;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.viewcomponents.NonSwipeableViewPager;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

import javax.inject.Inject;
import java.util.Set;


/**
 * Enables user to go through each of the indicators, skip indicators, select a red, yellow, green color etc.
 */

public class SurveyIndicatorsFragment extends AbstractSurveyFragment implements ViewPager.OnPageChangeListener, QuestionCallback<IndicatorQuestion, IndicatorOption> {

    private SurveyIndicatorAdapter mAdapter;
    private NonSwipeableViewPager mPager;

    protected LinearLayout mBackButton;
    protected TextView mBackButtonText;
    protected ImageView mBackButtonImage;

    protected LinearLayout mSkipButton;
    protected TextView mSkipButtonText;
    protected ImageView mSkippButtonImage;

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
        super.onCreate(savedInsanceState);
        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSurveyViewModel = ViewModelProviders
                .of(getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        setTitle(getString(R.string.survey_indicators_title));

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

        mBackButton = (LinearLayout) view.findViewById(R.id.indicatorsurvey_backbutton);
        mBackButtonText = (TextView) view.findViewById(R.id.indicatorsurvey_backbuttontext);
        mBackButtonImage = (ImageView) view.findViewById(R.id.indicatorsurvey_backbuttonimage);

        mSkipButton = (LinearLayout) view.findViewById(R.id.indicatorsurvey_skipbutton);
        mSkipButtonText = (TextView) view.findViewById(R.id.indicatorsurvey_skipbuttontext);
        mSkippButtonImage = (ImageView) view.findViewById(R.id.indicatorsurvey_skipbuttonimage);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousQuestion();
            }
        });

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        checkConditions();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void nextQuestion() {
        if (isPageChanged) {
            if (mPager.getCurrentItem() < mAdapter.getCount() - 1) {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                checkConditions();
            } else {
                mSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.SUMMARY);
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

    public IndicatorOption getResponses(IndicatorQuestion question) {
        return mSurveyViewModel.getResponseForIndicator(question);
    }

    public void addIndicatorResponse(IndicatorQuestion question, IndicatorOption option) {
        mSurveyViewModel.setIndicatorResponse(question, option);
    }

    public Set<IndicatorQuestion> getSkippedIndicators() {
        return mSurveyViewModel.getSkippedIndicators();
    }

    public void removeIndicatorResponse(IndicatorQuestion question) {
        mSurveyViewModel.removeIndicatorResponse(question);
        checkConditions();
    }

    public void checkConditions() {
        if (mSurveyViewModel.hasIndicatorResponse(mPager.getCurrentItem())) {
            mSkipButtonText.setText(R.string.navigate_next);
            mSkippButtonImage.setVisibility(View.VISIBLE);
        } else if (mAdapter.getQuestion(mPager.getCurrentItem()).isRequired()) {
            mSkipButtonText.setText(R.string.all_required);
            mSkippButtonImage.setVisibility(View.GONE);
        } else {
            mSkipButtonText.setText(R.string.navigate_skip);
            mSkippButtonImage.setVisibility(View.VISIBLE);
        }

        String question =   (mPager.getCurrentItem() + 1) + ". " +
                        mAdapter.getQuestion(mPager.getCurrentItem()).getDescription();

        mQuestionText.setText(question);
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
}
