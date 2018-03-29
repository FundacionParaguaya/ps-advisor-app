package org.fundacionparaguaya.advisorapp.ui.survey.indicators;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.ui.survey.QuestionCallback;
import org.fundacionparaguaya.advisorapp.data.model.IndicatorOption;
import org.fundacionparaguaya.advisorapp.data.model.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.ui.common.widget.IndicatorCard;
import org.fundacionparaguaya.advisorapp.ui.survey.AbstractSurveyFragment;

/**
 *
 */

public class ChooseIndicatorFragment extends AbstractSurveyFragment {

    protected IndicatorCard mGreenCard;
    protected IndicatorCard mYellowCard;
    protected IndicatorCard mRedCard;

    protected IndicatorQuestion mQuestion;
    private IndicatorCard.IndicatorClickedHandler handler = this::onCardSelected;

    private static String QUESTION_INDEX_KEY = "QUESTION_INDEX_KEY";

    public static ChooseIndicatorFragment build(int index) {

        ChooseIndicatorFragment fragment = new ChooseIndicatorFragment();
        Bundle b = new Bundle();
        b.putInt(QUESTION_INDEX_KEY, index);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int questionIndex = getArguments().getInt(QUESTION_INDEX_KEY, -1);

        if(questionIndex!=-1)
        {
            mQuestion = getCallback().getQuestion(questionIndex);
        }
        else
        {
            throw new IllegalArgumentException("ChooseIndicatorFragment must be provided with a question index");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chooseindicator, container, false);

        mGreenCard = rootView.findViewById(R.id.indicatorcard_green);
        mYellowCard = rootView.findViewById(R.id.indicatorcard_yellow);
        mRedCard = rootView.findViewById(R.id.indicatorcard_red);

        for (IndicatorOption option : mQuestion.getOptions()) {
            switch (option.getLevel()) {
                case Green:
                    mGreenCard.setOption(option);
                    break;
                case Yellow:
                    mYellowCard.setOption(option);
                    break;
                case Red:
                    mRedCard.setOption(option);
                    break;
            }
        }

        IndicatorOption existingResponse = getCallback().getResponse(mQuestion);

        if (existingResponse != null) {
            switch (existingResponse.getLevel()) {
                case Green:
                    mGreenCard.setSelected(true);
                    break;

                case Yellow:
                    mYellowCard.setSelected(true);
                    break;

                case Red:
                    mRedCard.setSelected(true);
                    break;
                default:
                    break;
            }
        }

        mGreenCard.addIndicatorClickedHandler(handler);
        mYellowCard.addIndicatorClickedHandler(handler);
        mRedCard.addIndicatorClickedHandler(handler);

        return rootView;
    }

    /**
     * Sets the desired selected indicator option
     *
     * @param indicatorCard
     */
    private void onCardSelected(@NonNull IndicatorCard indicatorCard) {

        if (indicatorCard.getSelected()) {
            indicatorCard.setSelected(false);
            getCallback().onResponse(mQuestion, null);
        } else {
            indicatorCard.setSelected(true);
            mRedCard.setSelected(mRedCard.equals(indicatorCard));
            mYellowCard.setSelected(mYellowCard.equals(indicatorCard));
            mGreenCard.setSelected(mGreenCard.equals(indicatorCard));
            getCallback().onResponse(mQuestion, indicatorCard.getOption());
        }
    }

    private QuestionCallback<IndicatorQuestion, IndicatorOption> getCallback() {
        try {
            @SuppressWarnings("unchecked")
            QuestionCallback<IndicatorQuestion, IndicatorOption> callback = (QuestionCallback<IndicatorQuestion, IndicatorOption>) getParentFragment();
            return callback;
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment of ChooseIndicatorFragment must implement interface " +
                    "QuestionCallback<IndicatorQuestion, IndicatorOption>");
        }
    }
}
