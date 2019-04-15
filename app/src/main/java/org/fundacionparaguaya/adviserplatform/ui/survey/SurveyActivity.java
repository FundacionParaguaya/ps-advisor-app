package org.fundacionparaguaya.adviserplatform.ui.survey;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageButton;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import org.fundacionparaguaya.assistantadvisor.AdviserAssistantApplication;
import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.data.model.Family;
import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.adviserplatform.ui.common.AbstractFragSwitcherActivity;
import org.fundacionparaguaya.adviserplatform.ui.survey.SharedSurveyViewModel.SurveyState;
import org.fundacionparaguaya.adviserplatform.util.MixpanelHelper;

import javax.inject.Inject;

/**
 * Activity for surveying a family's situation. Displays the fragments that record background info and allows
 * the family to select indicators
 */

public class SurveyActivity extends AbstractFragSwitcherActivity
{
    static String FAMILY_ID_KEY = "FAMILY_ID";

    private ImageButton mExitButton;

    @Inject
    protected InjectionViewModelFactory mViewModelFactory;
    private SharedSurveyViewModel mSurveyViewModel;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ((AdviserAssistantApplication) this.getApplication())
                .getApplicationComponent()
                .inject(this);

        mSurveyViewModel = ViewModelProviders
                .of(this, mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        initRotation();

        setContentView(R.layout.activity_survey);

        mExitButton = findViewById(R.id.btn_surveyactivity_close);

        mExitButton.setOnClickListener((event)->
        {
            if(mSurveyViewModel.getSurveyState()!= SurveyState.INTRO) {
                makeExitDialog().setConfirmClickListener((dialog) ->
                {
                    MixpanelHelper.SurveyEvents.quitSurvey(this, mSurveyViewModel.hasFamily());

                    dialog.dismissWithAnimation();
                    this.finish();
                }).show();
            }
            else
            {
                MixpanelHelper.SurveyEvents.quitSurvey(this, mSurveyViewModel.hasFamily());
                this.finish();
            }
        });

        setFragmentContainer(R.id.fragment_container);

        if(savedInstanceState==null) {
            //familyId can never equal -1 if retrieved from the database, so it is used as the default value
            int familyId = getIntent().getIntExtra(FAMILY_ID_KEY, -1);

            mSurveyViewModel.setFamily(familyId);
            mSurveyViewModel.setSurveyState(SurveyState.INTRO);
            switchToFrag(ChooseSurveyFragment.class);
        }

        subscribeToViewModel();
    }

    SweetAlertDialog makeExitDialog()
    {
       return new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.exit_confirmation))
                .setContentText(getString(R.string.surveyactivity_exit_explanation))
                .setCancelText(getString(R.string.all_cancel))
                .setConfirmText(getString(R.string.all_okay))
                .showCancelButton(true)
                .setCancelClickListener(SweetAlertDialog::cancel);
    }

    private void initRotation()
    {
        SurveyState currentState = mSurveyViewModel.getSurveyState();

        if(currentState!=null)
        {
            setOrientation(currentState);
        }

        mSurveyViewModel.SurveyState().observe(this, surveyState -> {
            assert surveyState != null;
            setOrientation(surveyState);
        });
    }

    public void subscribeToViewModel()
    {
        mSurveyViewModel.SurveyState().observe(this, state->
        {
            if(state != SurveyState.INTRO)
            {
                switchToFrag(TakeSurveyFragment.class);
                mSurveyViewModel.SurveyState().removeObservers(this);
            }
        });
    }

    private void setOrientation(@NonNull SurveyState surveyState)
    {
        int orientation;

        switch (surveyState)
        {
            case INDICATORS:
            case LIFEMAP:
                orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                break;

            default:
                orientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
                break;
        }

        if(this.getRequestedOrientation() != orientation)
        {
            setRequestedOrientation(orientation);
        }
    }

    @Override
    public void onBackPressed() {
        switch (mSurveyViewModel.getSurveyState()) {
            case NONE:
            case INTRO:
            {
                super.onBackPressed();
                break;
            }

            case BACKGROUND:
            {
                makeExitDialog().
                setConfirmClickListener((dialog) ->
                {
                    mSurveyViewModel.setSurveyState(SurveyState.INTRO);
                    dialog.dismiss();
                })
                .show();
            }

            case ECONOMIC_QUESTIONS: {
                mSurveyViewModel.setSurveyState(SurveyState.BACKGROUND);
                break;
            }

            case INDICATORS: {
                mSurveyViewModel.setSurveyState(SurveyState.ECONOMIC_QUESTIONS);
                break;
            }

            case LIFEMAP: {
                mSurveyViewModel.setSurveyState(SurveyState.INDICATORS);
                break;
            }
        }
    }
    //Returns and intent to open this activity, with an extra for the family's Id.
    public static Intent build(Context c, Family family)
    {
        Intent intent = new Intent(c, SurveyActivity.class);
        intent.putExtra(FAMILY_ID_KEY, family.getId());

        return intent;
    }

    public static int getFamilyId(Intent result)
    {
        return result.getIntExtra(TakeSurveyFragment.FAMILY_ID_KEY, -1);
    }
}
