package org.fundacionparaguaya.advisorapp.fragments;

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

import com.instabug.library.Instabug;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.SurveyQuestionReviewAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.ReviewCallback;

public class ReviewPageFragment extends Fragment {

    private Button mSubmitButton;
    private RecyclerView mRv;
    private SurveyQuestionReviewAdapter mSurveyReviewAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSurveyReviewAdapter = new SurveyQuestionReviewAdapter();
        mSurveyReviewAdapter.setQuestions(((ReviewCallback) getParentFragment()).getQuestions());
        ((ReviewCallback) getParentFragment()).getResponses().observe(this, mSurveyReviewAdapter::setResponses);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.item_questionsreview, container, false);

        mRv = v.findViewById(R.id.rv_questionsreview);
        mRv.setLayoutManager(new LinearLayoutManager(v.getContext()));
        mRv.setAdapter(mSurveyReviewAdapter);

        mSubmitButton = v.findViewById(R.id.btn_surveyquestions_submit);
        mSubmitButton.setOnClickListener((view) -> ((ReviewCallback) getParentFragment()).onSubmit());

        return v;
    }

    @Override
    public void onDestroy() {
        try {
            ((ReviewCallback) getParentFragment()).getResponses().removeObservers(this);
        } catch (NullPointerException e) {
            Instabug.reportException(e);
        }

        super.onDestroy();
    }
}
