package org.fundacionparaguaya.advisorapp.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.*;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.*;
import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.util.MixpanelHelper;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel.SurveyState;

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

        ((AdvisorApplication) this.getApplication())
                .getApplicationComponent()
                .inject(this);

        mSurveyViewModel = ViewModelProviders
                .of(this, mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        setContentView(R.layout.activity_survey);

        mExitButton = findViewById(R.id.btn_surveyactivity_close);

        mExitButton.setOnClickListener((event)->
        {
            if(mSurveyViewModel.getSurveyState().getValue()!=SurveyState.INTRO) {
                makeExitDialog().setConfirmClickListener((dialog) ->
                {
                    MixpanelHelper.SurveyEvents.quitSurvey(this, mSurveyViewModel.hasFamily());

                    this.finish();
                    dialog.dismissWithAnimation();
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
                .setTitleText(getString(R.string.surveyactivity_exit_confirmation))
                .setContentText(getString(R.string.surveyactivity_exit_explanation))
                .setCancelText(getString(R.string.all_cancel))
                .setConfirmText(getString(R.string.all_okay))
                .showCancelButton(true)
                .setCancelClickListener(SweetAlertDialog::cancel);
    }

    public void subscribeToViewModel()
    {
        mSurveyViewModel.getSurveyState().observe(this, state->
        {
            if(state!=null && state!=SurveyState.INTRO)
            {
                switchToFrag(TakeSurveyFragment.class);
                mSurveyViewModel.getSurveyState().removeObservers(this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(mSurveyViewModel.getSurveyState().getValue()!=null) {
            switch (mSurveyViewModel.getSurveyState().getValue()) {
                case INTRO:
                case NONE:
                {
                    super.onBackPressed();
                    break;
                }

                case BACKGROUND:
                case ECONOMIC_QUESTIONS: {
                    makeExitDialog().
                            setConfirmClickListener((dialog) ->
                            {
                                mSurveyViewModel.setSurveyState(SurveyState.INTRO);
                                dialog.dismiss();
                            })
                            .show();
                    break;
                }

                case INDICATORS: {
                    mSurveyViewModel.setSurveyState(SurveyState.ECONOMIC_QUESTIONS);
                    break;
                }
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
}
