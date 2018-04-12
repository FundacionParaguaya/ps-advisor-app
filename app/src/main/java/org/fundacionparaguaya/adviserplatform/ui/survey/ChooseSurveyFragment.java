package org.fundacionparaguaya.adviserplatform.ui.survey;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.fundacionparaguaya.adviserplatform.AdviserApplication;
import org.fundacionparaguaya.adviserplatform.R;
import org.fundacionparaguaya.adviserplatform.data.model.Survey;
import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.adviserplatform.ui.survey.resume.PendingSnapshotViewModel;
import org.fundacionparaguaya.adviserplatform.ui.survey.resume.ResumeSnapshotPopupWindow;

import javax.inject.Inject;

import static android.view.View.VISIBLE;

/**
 * Intro page on a new survey
 */

public class ChooseSurveyFragment extends Fragment {
    private static String FRAGMENT_TAG = "ChooseSurveyFragment";

    @Inject
    InjectionViewModelFactory mViewModelFactory;

    private RecyclerView mSurveyOptionList;
    private Button mSubmitButton;

    private LinearLayout mInProgressSnapshotWarningLayout;
    private ResumeSnapshotPopupWindow mResumeSnapshotPopupWindow;

    private SurveyListAdapter mAdapter;

    SharedSurveyViewModel mSurveyViewModel;
    PendingSnapshotViewModel mPendingSnapshotViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inject view model factory and load view model

        ((AdviserApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSurveyViewModel = ViewModelProviders
                .of(getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);
        mPendingSnapshotViewModel = ViewModelProviders
                .of(getActivity(), mViewModelFactory)
                .get(PendingSnapshotViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surveyintro, container, false);


        TextView familyNameTv = view.findViewById(R.id.tv_surveyintro_familyname);
        mSubmitButton = view.findViewById(R.id.btn_surveyintro_submit);

        mSubmitButton.setOnClickListener((event) -> onSubmit());

        mSurveyOptionList = view.findViewById(R.id.surveyintro_surveyoptionlist);
        mSurveyOptionList.setLayoutManager(new LinearLayoutManager(getContext()));

        mSurveyViewModel.CurrentFamily().observe(this, (family ->
        {
            if (family != null)
                familyNameTv.setText(getResources().getString(
                        R.string.surveyintro_family, family.getName()));
        }));

        TextView title = view.findViewById(R.id.tv_surveyintro_title);

        if (mSurveyViewModel.hasFamily()) {
            title.setText(R.string.surveyintro_newsnapshot);
        } else {
            title.setText(R.string.survey_newfamily);
            familyNameTv.setVisibility(View.GONE);
        }

        mInProgressSnapshotWarningLayout = view.findViewById(R.id.linearLayout_surveyintro_inprogresswarning);

        mPendingSnapshotViewModel.snapshot().observe(this, snapshot -> {
            if (snapshot != null)
                mResumeSnapshotPopupWindow.setSnapshot(snapshot);
        });
        mPendingSnapshotViewModel.family().observe(this, family ->
            mResumeSnapshotPopupWindow.setFamily(family)
        );
        mPendingSnapshotViewModel.survey().observe(this, survey -> {
            if (survey != null)
                mResumeSnapshotPopupWindow.setSurvey(survey);
        });

        mAdapter = new SurveyListAdapter();
        mSurveyOptionList.setAdapter(mAdapter);
        mAdapter.setClickListener(this::onItemClick);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mResumeSnapshotPopupWindow = ResumeSnapshotPopupWindow.builder(getContext())
                .onContinue((popup, snapshot, survey, family) -> {
                    mSurveyViewModel.resumeSnapshot(snapshot, survey, family);
                    popup.dismiss();
                })
                .onDismiss((popup, snapshot, survey, family) -> popup.dismiss())
                .build();

        mSurveyViewModel.getPendingSnapshot().observe(this, (pendingSnapshot) -> {
            if (pendingSnapshot != null) {

                mPendingSnapshotViewModel.setSnapshot(pendingSnapshot);

                mInProgressSnapshotWarningLayout.setOnClickListener((event) -> {
                    // create a new popup from the old one and show it
                    mResumeSnapshotPopupWindow = ResumeSnapshotPopupWindow.builder(getContext())
                            .popup(mResumeSnapshotPopupWindow)
                            .build();

                    mResumeSnapshotPopupWindow.show();
                });
                mInProgressSnapshotWarningLayout.setVisibility(VISIBLE);

                view.post(mResumeSnapshotPopupWindow::show);
            }

            mSurveyViewModel.getPendingSnapshot().removeObservers(this); //only shown once
        });

        mSurveyViewModel.SelectedSurvey().observe(this, survey -> {
            mAdapter.setSelectedSurvey(survey);

            if(survey == null) mSubmitButton.setVisibility(View.GONE);
            else mSubmitButton.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSurveyViewModel.getSurveys().observe(this, (surveys) ->
        {
            mAdapter.setSurveyList(surveys);
        });

    }

    private void onItemClick(Survey survey) {

        Survey previouslySelected = mSurveyViewModel.getSelectedSurvey();

        if(previouslySelected == null || !previouslySelected.equals(survey))
        {
            mSurveyViewModel.setSelectedSurvey(survey);
        }
        else mSurveyViewModel.setSelectedSurvey(null);
    }

    void onSubmit() {
        mSurveyViewModel.makeSnapshot(); //assumes family livedata object has value
    }

    public static String getFragmentTag() {
        return FRAGMENT_TAG;
    }
}
