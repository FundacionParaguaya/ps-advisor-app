package org.fundacionparaguaya.advisorapp.fragments.callbacks;

import android.view.View;

import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;

/**
 * A callback for fragments that are displaying a list of background questions
 */

public interface BackgroundQuestionCallback {
    void onQuestionAnswered(BackgroundQuestion q, Object response);
    void onNext(View v);
    void onBack(View v);
    void onSubmit();

    void setAnswerRequired(boolean answerRequired);

    String getResponseFor(BackgroundQuestion q);
}
