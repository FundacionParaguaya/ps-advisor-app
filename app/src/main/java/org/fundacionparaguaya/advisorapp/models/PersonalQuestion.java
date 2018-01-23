package org.fundacionparaguaya.advisorapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * A question targeting a personal data which can be presented to a family and responded to from a
 * survey.
 */

public class PersonalQuestion extends SurveyQuestion {
    @SerializedName("options")
    private List<String> options;

    public PersonalQuestion(String name, String description, ResponseType type) {
        this(name, description, type, new ArrayList<>());
    }

    public PersonalQuestion(String name, String description, ResponseType type, List<String> options) {
        super(name, description, type);
        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        PersonalQuestion that = (PersonalQuestion) o;

        return getOptions() != null ? getOptions().equals(that.getOptions()) : that.getOptions() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getOptions() != null ? getOptions().hashCode() : 0);
        return result;
    }
}
