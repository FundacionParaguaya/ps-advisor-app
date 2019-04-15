package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;

/**
 * The intermediate representation of the snapshot from the remote database.
 */

public class SnapshotIr {
    @SerializedName("snapshot_economic_id")
    private long id;
    @SerializedName("survey_id")
    private long surveyId;
    @SerializedName("personal_survey_data")
    private Map<String, Object> personalResponses;
    @SerializedName("economic_survey_data")
    private Map<String, Object> economicResponses;
    @SerializedName("indicator_survey_data")
    private Map<String, String> indicatorResponses;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("user_id")
    private long userId;
    @SerializedName("term_cond_id")
    private long termCondId;
    @SerializedName("priv_pool_id")
    private long privPoolId;
    @SerializedName("organization_id")
    private long organizationId;
    @SerializedName("snapshot_indicator_id")
    private long snapshotIndicatorId;

    public long getId() {
        return id;
    }

    public long getSnapshotIndicatorId() {
        return snapshotIndicatorId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(long surveyId) {
        this.surveyId = surveyId;
    }

    public Map<String, Object> getPersonalResponses() {
        return personalResponses;
    }

    public void setPersonalResponses(Map<String, Object> personalResponses) {
        this.personalResponses = personalResponses;
    }

    public Map<String, Object> getEconomicResponses() {
        return economicResponses;
    }

    public void setEconomicResponses(Map<String, Object> economicResponses) {
        this.economicResponses = economicResponses;
    }

    public Map<String, String> getIndicatorResponses() {
        return indicatorResponses;
    }

    public void setIndicatorResponses(Map<String, String> indicatorResponses) {
        this.indicatorResponses = indicatorResponses;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTermCondId() {
        return termCondId;
    }

    public void setTermCondId(long termCondId) {
        this.termCondId = termCondId;
    }

    public long getPrivPoolId() {
        return privPoolId;
    }

    public void setPrivPoolId(long privPoolId) {
        this.privPoolId = privPoolId;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public void setSnapshotIndicatorId(long snapshotIndicatorId) {
        this.snapshotIndicatorId = snapshotIndicatorId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        SnapshotIr rhs = (SnapshotIr) obj;
        return new EqualsBuilder()
                .append(this.getId(), rhs.getId())
                .append(this.getSurveyId(), rhs.getSurveyId())
                .append(this.getPersonalResponses(), rhs.getPersonalResponses())
                .append(this.getEconomicResponses(), rhs.getEconomicResponses())
                .append(this.getIndicatorResponses(), rhs.getIndicatorResponses())
                .append(this.getCreatedAt(), rhs.getCreatedAt())
                .append(this.getUserId(), rhs.getUserId())
                .append(this.getTermCondId(), rhs.getTermCondId())
                .append(this.getPrivPoolId(), rhs.getPrivPoolId())
                .append(this.getOrganizationId(), rhs.getOrganizationId())
                .append(this.getSnapshotIndicatorId(), rhs.getSnapshotIndicatorId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getId())
                .append(getSurveyId())
                .append(getPersonalResponses())
                .append(getEconomicResponses())
                .append(getIndicatorResponses())
                .append(getCreatedAt())
                .append(getUserId())
                .append(getTermCondId())
                .append(getPrivPoolId())
                .append(getOrganizationId())
                .append(getSnapshotIndicatorId())
                .toHashCode();
    }
}
