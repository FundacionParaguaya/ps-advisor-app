package org.fundacionparaguaya.advisorapp.data.model;

import android.arch.persistence.room.ColumnInfo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

        return new EqualsBuilder()
                .append(name, that.name)
                .append(description, that.description)
                .append(required, that.required)
                .append(type, that.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 29)
                .append(name)
                .append(description)
                .append(required)
                .append(type)
                .toHashCode();
    }
}
