package org.fundacionparaguaya.advisorapp.fragments.callbacks;

import android.arch.lifecycle.LiveData;
import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;

import java.util.List;
import java.util.Map;

/**
 * A callback for the review fragment after background questions
 */

public interface ReviewCallback {
    void onSubmit();
    List<BackgroundQuestion> getQuestions();
    LiveData<Map<BackgroundQuestion, String>> getResponses();
}
