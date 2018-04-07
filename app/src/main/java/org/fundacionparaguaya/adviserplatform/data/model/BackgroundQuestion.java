package org.fundacionparaguaya.adviserplatform.data.model;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

        BackgroundQuestion that = (BackgroundQuestion) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(that))
                .append(type, that.type)
                .append(options, that.options)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(41, 43)
                .appendSuper(super.hashCode())
                .append(type)
                .append(options)
                .toHashCode();
    }

    public enum QuestionType {
        PERSONAL,
        ECONOMIC
    }
}
