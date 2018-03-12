package org.fundacionparaguaya.advisorapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.*;
import org.fundacionparaguaya.advisorapp.util.IndicatorUtilities;
import org.fundacionparaguaya.advisorapp.viewcomponents.NumberStepperView;
import org.fundacionparaguaya.advisorapp.viewmodels.EditPriorityViewModel;

import java.util.Date;

/**
 * Pop up window that allows the user to input some details about the priority..
 *
 * Three questions
 *
 * 1. Why they don't have the priority
 * 2. What will they do to get the priority
 * 3. When they will get the priority
 */

//TODO add in viewmodel for priority and stuff

public class EditPriorityActivity extends FragmentActivity implements View.OnClickListener, TextWatcher{

    private static String INDICATOR_LEVEL_ARG = "INDICATOR_LEVEL_KEY";
    private static String INDICATOR_INDEX_ARG = "INDICATOR_INDEX_ARG";
    private static String SURVEY_ID_ARG = "SURVEY_ID_ARG";
    private static String RESPONSE_REASON_ARG = "RESPONSE_WHY_KEY";
    private static String RESPONSE_ACTION_ARG = "RESPONSE_ACTION_KEY";
    private static String RESPONSE_DATE_ARG = "RESPONSE_DATE_KEY";

    private AppCompatImageView mIndicatorColor;
    private TextView mIndicatorTitle;

    private Button mBtnSubmit;
    private ImageButton mBtnExit;

    private EditText mEtWhy;
    private EditText mEtStrategy;

    private NumberStepperView mMonthsTillCompletion;
    private EditPriorityViewModel mViewModel;

    //TODO findViewById for months till completion
    //TODO inject view model
    //TODO add animation slide in from bottom https://stackoverflow.com/questions/23578059/make-activity-animate-from-top-to-bottom/23752530
    //TODO add onResult to fragment
    //TODO define date format to/from\

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_editpriority);

        mIndicatorColor = findViewById(R.id.iv_prioritypopup_color);
        mIndicatorTitle = findViewById(R.id.tv_prioritypopup_title);

        mEtWhy = findViewById(R.id.et_prioritypopup_why);
        mEtStrategy = findViewById(R.id.et_prioritypopup_strategy);

        mBtnSubmit = findViewById(R.id.btn_prioritypopup_submit);
        mBtnExit = findViewById(R.id.btn_prioritypopup_close);

        int surveyId = getIntent().getIntExtra(SURVEY_ID_ARG, -1);
        int questionIndex = getIntent().getIntExtra(INDICATOR_INDEX_ARG, -1);

        mViewModel.setIndicator(surveyId, questionIndex);

        IndicatorOption.Level level = IndicatorOption.Level.valueOf(getIntent().getStringExtra(INDICATOR_LEVEL_ARG));
        IndicatorUtilities.setViewColorFromLevel(level, mIndicatorColor);

        String reason = getIntent().getStringExtra(RESPONSE_REASON_ARG);
        if(reason !=null) {
            mViewModel.setReason(reason);
        }

        String action = getIntent().getStringExtra(RESPONSE_ACTION_ARG);
        if(action !=null) {
            mViewModel.setAction(action);
        }

        String dateString = getIntent().getStringExtra(RESPONSE_DATE_ARG);
        if(dateString !=null) {
            mViewModel.setCompletionDate(new Date(Date.parse(dateString)));
        }

        addListeners();
    }

    public void addListeners()
    {
        mMonthsTillCompletion.getValue().observe(this, mViewModel::setNumMonths);
        mBtnExit.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        mEtWhy.addTextChangedListener(this);
        mEtStrategy.addTextChangedListener(this);

        mViewModel.getIndicator().observe(this, indicator->
        {
            if(indicator!=null) {
                mIndicatorTitle.setText(indicator.getName());
            }
        });
    }


    @Override
    public void onClick(View view) {
        if(view.equals(mBtnExit))
        {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        else if(view.equals(mBtnSubmit))
        {
            Intent result = new Intent();

            result.putExtra(RESPONSE_ACTION_ARG, mViewModel.getAction());
            result.putExtra(RESPONSE_REASON_ARG, mViewModel.getReason());
            result.putExtra(RESPONSE_DATE_ARG, mViewModel.getCompletionDate());

            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }

    public Intent build(Context c, Survey s, IndicatorQuestion indicator, IndicatorOption.Level response) {
        return build(c, s, indicator, response, null);
    }

    public Intent build(Context c, Survey s, IndicatorQuestion indicator, IndicatorOption.Level response,
                        LifeMapPriority priority) {

        Intent intent = new Intent(c, EditPriorityActivity.class);

        intent.putExtra(INDICATOR_LEVEL_ARG, response.name());
        intent.putExtra(INDICATOR_INDEX_ARG, s.getIndicatorQuestions().indexOf(indicator));
        intent.putExtra(SURVEY_ID_ARG, s.getId());

        if(priority != null)
        {
            intent.putExtra(RESPONSE_ACTION_ARG, priority.getAction());
            intent.putExtra(RESPONSE_REASON_ARG, priority.getReason());
            intent.putExtra(RESPONSE_DATE_ARG, priority.getEstimatedDate().toString());
        }

        return intent;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.equals(mEtWhy.getText()))
        {
            mViewModel.setReason(s.toString());
        }
        else if(s.equals(mEtStrategy.getText()))
        {
            mViewModel.setAction(s.toString());
        }
    }
}
