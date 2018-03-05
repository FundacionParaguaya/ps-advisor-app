package org.fundacionparaguaya.advisorapp.fragments.callbacks;

import org.fundacionparaguaya.advisorapp.models.SurveyQuestion;

public interface QuestionCallback<QuestionT extends SurveyQuestion, ResponseT> {
    QuestionT getQuestion(int i);
    ResponseT getResponse(QuestionT question);
    void onResponse(QuestionT question, ResponseT s);
}
