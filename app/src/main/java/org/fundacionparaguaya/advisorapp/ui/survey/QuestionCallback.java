package org.fundacionparaguaya.advisorapp.ui.survey;

import org.fundacionparaguaya.advisorapp.data.model.SurveyQuestion;
import org.fundacionparaguaya.advisorapp.ui.survey.indicators.ChooseIndicatorFragment;
import org.fundacionparaguaya.advisorapp.ui.survey.questions.QuestionFragment;

/**
 * This interface must be implemented by any fragments/activities containing a
 * {@link QuestionFragment} or
 * {@link ChooseIndicatorFragment}. It allows the fragment
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
