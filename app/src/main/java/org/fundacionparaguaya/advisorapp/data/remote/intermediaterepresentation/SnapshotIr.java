package org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;

/**
 * The intermediate representation of the snapshot from the remote database.
 */

public class SnapshotIr {
    @SerializedName("snapshot_economic_id")
    long id;
    @SerializedName("survey_id")
    long surveyId;
    @SerializedName("personal_survey_data")
    Map<String, Object> personalResponses;
    @SerializedName("economic_survey_data")
    Map<String, Object> economicResponses;
    @SerializedName("indicator_survey_data")
    Map<String, String> indicatorResponses;
    @SerializedName("created_at")
    String createdAt;
    @SerializedName("user_id")
    long userId;
    @SerializedName("term_cond_id")
    long termCondId;
    @SerializedName("priv_pool_id")
    long privPoolId;

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SnapshotIr that = (SnapshotIr) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(surveyId, that.surveyId)
                .append(personalResponses, that.personalResponses)
                .append(economicResponses, that.economicResponses)
                .append(indicatorResponses, that.indicatorResponses)
                .append(createdAt, that.createdAt)
                .append(userId, that.userId)
                .append(termCondId, that.termCondId)
                .append(privPoolId, that.privPoolId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(31, 97)
                .append(id)
                .append(surveyId)
                .append(personalResponses)
                .append(economicResponses)
                .append(indicatorResponses)
                .append(createdAt)
                .append(userId)
                .append(termCondId)
                .append(privPoolId)
                .toHashCode();
    }
}
