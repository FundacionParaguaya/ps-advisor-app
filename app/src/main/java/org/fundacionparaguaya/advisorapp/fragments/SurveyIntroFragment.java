package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.SurveyListAdapter;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.models.Survey;
import org.fundacionparaguaya.advisorapp.viewcomponents.ResumeSnapshotPopupWindow;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel.SurveyState;

import java.util.ArrayList;

import javax.inject.Inject;

import static android.view.View.VISIBLE;

/**
 * Intro page on a new survey
 */

public class SurveyIntroFragment extends AbstractSurveyFragment {
    private static String FRAGMENT_TAG = "SurveyIntroFragment";

    @Inject
    InjectionViewModelFactory mViewModelFactory;

    private RecyclerView mSurveyOptionList;
    private Button mSubmitButton;

    private LinearLayout mInProgressSnapshotWarningLayout;

    private ArrayList<Survey> mSurveyList;

    private SurveyListAdapter mAdapter;

    private Survey selectedSurvey = null;

    SharedSurveyViewModel mSurveyViewModel;

    //need the family name

    public SurveyIntroFragment() {
        setShowFooter(false);
        setShowHeader(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inject view model factory and load view model

        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSurveyViewModel = ViewModelProviders
                .of(getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        setTitle("");

        setFooterColor(R.color.app_lightgrey);
        setHeaderColor(R.color.app_lightgrey);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surveyintro, container, false);


        TextView familyNameTv = view.findViewById(R.id.tv_surveyintro_familyname);
        mSubmitButton = view.findViewById(R.id.btn_surveyintro_submit);

        mSubmitButton.setOnClickListener((event) -> onSubmit());

        mSurveyOptionList = (RecyclerView) view.findViewById(R.id.surveyintro_surveyoptionlist);
        mSurveyOptionList.setLayoutManager(new LinearLayoutManager(getContext()));

        mSurveyViewModel.getCurrentFamily().observe(this, (family ->
        {
            if(family!=null) familyNameTv.setText(family.getName() + getResources().getString(R.string.familytab_title));
        }));

        TextView title = view.findViewById(R.id.tv_surveyintro_title);

        if(mSurveyViewModel.hasFamily())
        {
            title.setText(R.string.surveyintro_newsnapshot);
        }
        else
        {
            title.setText(R.string.addfamily_new_family_title);
            familyNameTv.setVisibility(View.GONE);
        }

        mInProgressSnapshotWarningLayout = view.findViewById(R.id.linearLayout_surveyintro_inprogresswarning);

        mAdapter = new SurveyListAdapter(getContext(), mSurveyList);
        mSurveyOptionList.setAdapter(mAdapter);
        mAdapter.setClickListener(this::onItemClick);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSurveyViewModel.getPendingSnapshot().observe(this, (pendingSnapshot) -> {
            if (pendingSnapshot != null) {
                mSurveyViewModel.getPendingSnapshot().removeObservers(this);

                openInProgressSnapshotPopup(pendingSnapshot);

                mInProgressSnapshotWarningLayout.setOnClickListener(
                        (event) -> openInProgressSnapshotPopup(pendingSnapshot));
                mInProgressSnapshotWarningLayout.setVisibility(VISIBLE);
            }
        });
    }

    private void openInProgressSnapshotPopup(Snapshot pendingSnapshot) {
        new ResumeSnapshotPopupWindow.Builder(getContext())
                .snapshot(pendingSnapshot)
                .onContinue((popup, snapshot, survey, family) -> {
                    mSurveyViewModel.resumeSnapshot(snapshot, survey, family);
                    popup.dismiss();
                })
                .onDismiss((popup, snapshot, survey, family) -> popup.dismiss())
                .build()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSurveyViewModel.getSurveys().observe(this, (surveys) ->
        {
            mSurveyList = (ArrayList<Survey>) surveys;
            mAdapter.setSurveyList(mSurveyList);
            mSurveyOptionList.setAdapter(mAdapter);
        });

    }

    private void onItemClick(Survey survey, boolean isSelected) {
        if (isSelected) {
            if (selectedSurvey != null){
                for(Survey loopSurvey : mAdapter.getSurveyList()){
                    if (!loopSurvey.equals(survey)){
                        //Unselect all who are not selected currently
                        if(mAdapter.getViewHolderHashMap().get(loopSurvey)!=null)
                        {
                            mAdapter.getViewHolderHashMap().get(loopSurvey).setSelected(false);
                        }
                    }
                }
                selectedSurvey = null;
            }
            selectedSurvey = survey;
            mSubmitButton.setVisibility(VISIBLE);
        } else {
            selectedSurvey = null;
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    void onSubmit() {
        if (selectedSurvey != null) {
            mSurveyViewModel.makeSnapshot(selectedSurvey); //assumes family livedata object has value

            /**when snapshot is made**/
            mSurveyViewModel.getSnapshot().observe(this, (snapshot -> {
                if(mSurveyViewModel.hasFamily()) {
                    mSurveyViewModel.setSurveyState(SurveyState.ECONOMIC_QUESTIONS);
                }
                else
                {
                    mSurveyViewModel.setSurveyState(SurveyState.NEW_FAMILY);
                }
            }));
        } else {
            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.survey_error_no_surveys_title))
                    .setContentText(getString(R.string.survey_error_no_surveys_description))
                    .setConfirmText(getString(R.string.all_okay))
                    .setConfirmClickListener((dialog) -> {
                        getActivity().finish();
                    })
                    .show();
        }

        if (mSurveyList.size() == 0) {
            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.survey_error_no_surveys_title))
                    .setContentText(getString(R.string.survey_error_no_surveys_description))
                    .setConfirmText(getString(R.string.all_okay))
                    .setConfirmClickListener((dialog) ->
                    {
                        getActivity().finish();
                    })
                    .show();
        }
    }

    public static String getFragmentTag() {
        return FRAGMENT_TAG;
    }

    public static SurveyIntroFragment build() {
        SurveyIntroFragment fragment = new SurveyIntroFragment();

        return fragment;
    }
}
