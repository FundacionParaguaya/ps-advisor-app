package org.fundacionparaguaya.advisorapp.fragments.callbacks;

import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;

public interface QuestionCallback {
    BackgroundQuestion getQuestion(int i);
    String getResponse(BackgroundQuestion question);
    void onResponse(BackgroundQuestion question, String s);
}
