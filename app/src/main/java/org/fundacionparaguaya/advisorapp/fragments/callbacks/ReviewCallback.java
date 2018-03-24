package org.fundacionparaguaya.advisorapp.fragments.callbacks;

import android.arch.lifecycle.LiveData;

import java.util.List;
import java.util.Map;

/**
 * A callback for the review fragment after background questions
 */

public interface ReviewCallback<QuestionType, ResponseType> {
    void onSubmit();
    List<QuestionType> getQuestions();
    LiveData<Map<QuestionType, ResponseType>> getResponses();
}
