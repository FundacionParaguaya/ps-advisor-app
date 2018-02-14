package org.fundacionparaguaya.advisorapp.models;

import android.arch.persistence.room.ColumnInfo;

/**
 * A question which can be presented to a family and responded to from a survey.
 */

public class SurveyQuestion {
    @ColumnInfo(name="name")
    private String name;
    @ColumnInfo(name="description")
    private String description;
    @ColumnInfo(name="required")
    private boolean required;
    @ColumnInfo(name="type")
    private ResponseType type;

    public SurveyQuestion(String name, String description, boolean required, ResponseType type) {
        this.name = name;
        this.description = description;
        this.required = required;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public ResponseType getResponseType(){
        return type;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SurveyQuestion that = (SurveyQuestion) o;

        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
            return false;
        return getDescription() != null ? getDescription().equals(that.getDescription()) : that.getDescription() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }
}
