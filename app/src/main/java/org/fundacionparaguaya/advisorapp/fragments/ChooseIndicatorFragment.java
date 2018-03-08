package org.fundacionparaguaya.advisorapp.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.SurveyIndicatorAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.QuestionCallback;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.viewcomponents.IndicatorCard;

/**
 *
 */

public class ChooseIndicatorFragment extends Fragment {

    protected IndicatorCard mGreenCard;
    protected IndicatorCard mYellowCard;
    protected IndicatorCard mRedCard;

    protected IndicatorQuestion mQuestion;

    private static int clickDelay = 500;
    private static int clickDelayInterval = 100;

    @Nullable
    IndicatorCard selectedIndicatorCard;
    private CountDownTimer nextPageTimer;

    private IndicatorCard.IndicatorSelectedHandler handler = (card) ->
    {
        onCardSelected(card);
    };

    public static ChooseIndicatorFragment newInstance(IndicatorQuestion question) {

        ChooseIndicatorFragment fragment = new ChooseIndicatorFragment();
        fragment.mQuestion = question;

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chooseindicator, container, false);

        mGreenCard = (IndicatorCard) rootView.findViewById(R.id.indicatorcard_green);
        mYellowCard = (IndicatorCard) rootView.findViewById(R.id.indicatorcard_yellow);
        mRedCard = (IndicatorCard) rootView.findViewById(R.id.indicatorcard_red);


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

        IndicatorOption existingResponse = ((SurveyIndicatorsFragment)getParentFragment()).getResponses(mQuestion);

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

        mGreenCard.addIndicatorSelectedHandler(handler);
        mYellowCard.addIndicatorSelectedHandler(handler);
        mRedCard.addIndicatorSelectedHandler(handler);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        mRedCard.clearImageFromMemory();
        mYellowCard.clearImageFromMemory();
        mGreenCard.clearImageFromMemory();

        super.onDestroyView();
    }

    /**
     * Sets the desired selected indicator option
     *
     * @param indicatorCard
     */
    private void onCardSelected(@Nullable IndicatorCard indicatorCard) {

        if (indicatorCard.equals(selectedIndicatorCard)) {
            indicatorCard.setSelected(false);
            ((SurveyIndicatorsFragment)getParentFragment()).removeIndicatorResponse(mQuestion);
            selectedIndicatorCard = null;
            ((SurveyIndicatorsFragment) getParentFragment()).onResponse(this, mQuestion, indicatorCard.getOption());
        } else {
            mRedCard.setSelected(mRedCard.equals(indicatorCard));
            mYellowCard.setSelected(mYellowCard.equals(indicatorCard));
            mGreenCard.setSelected(mGreenCard.equals(indicatorCard));
            ((SurveyIndicatorsFragment)getParentFragment()).addIndicatorResponse(mQuestion, indicatorCard.getOption());
            ((SurveyIndicatorsFragment) getParentFragment()).onResponse(this, mQuestion, indicatorCard.getOption());
            selectedIndicatorCard = indicatorCard;
        }

    }

    public boolean isCardSelected() {
        if (selectedIndicatorCard == null) {
            return false;
        }
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
