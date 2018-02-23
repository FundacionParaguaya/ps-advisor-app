package org.fundacionparaguaya.advisorapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * A question targeting a background data which can be presented to a family and responded to from a
 * survey.
 */

public class BackgroundQuestion extends SurveyQuestion {
    @SerializedName("question_type")
    private QuestionType type;
    @SerializedName("options")
    private Map<String, String> options;

    public BackgroundQuestion(String name,
                              String description,
                              boolean required,
                              ResponseType responseType,
                              QuestionType questionType) {
        this(name, description, required, responseType, questionType, new HashMap<>());
    }
  
    public BackgroundQuestion(String name,
                              String description,
                              boolean required,
                              ResponseType responseType,
                              QuestionType questionType,
                              Map<String, String> options) {
        super(name, description, required, responseType);
        this.type = questionType;
        this.options = options;
    }

    public QuestionType getQuestionType() {
        return type;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BackgroundQuestion that = (BackgroundQuestion) o;

        if (type != that.type) return false;
        return getOptions() != null ? getOptions().equals(that.getOptions()) : that.getOptions() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (getOptions() != null ? getOptions().hashCode() : 0);
        return result;
    }

    public enum QuestionType {
        PERSONAL,
        ECONOMIC
    }
}
