package org.fundacionparaguaya.advisorapp.fragments.callbacks;

import org.fundacionparaguaya.advisorapp.models.SurveyQuestion;

/**
 * This interface must be implemented by any fragments/activities containing a
 * {@link org.fundacionparaguaya.advisorapp.fragments.QuestionFragment} or
 * {@link org.fundacionparaguaya.advisorapp.fragments.ChooseIndicatorFragment}. It allows the fragment
 * to retrieve/update responses to a question in a lifecycle friendly manager.
 *
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface QuestionCallback<QuestionT extends SurveyQuestion, ResponseT> {
    QuestionT getQuestion(int i);
    ResponseT getResponse(QuestionT question);
    void onResponse(QuestionT question, ResponseT s);
}
