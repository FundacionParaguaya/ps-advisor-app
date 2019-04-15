package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The intermediate representation of a life map priority from the remote database.
 */

public class PriorityIr {
    @SerializedName("snapshot_indicator_priority_id")
    private long id;
    @SerializedName("snapshot_indicator_id")
    private long snapshotIndicatorId;
    @SerializedName("indicator")
    private String indicatorTitle;
    @SerializedName("reason")
    private String reason;
    @SerializedName("action")
    private String action;
    @SerializedName("estimated_date")
    private String estimatedDate;
    @SerializedName("is_attainment")
    private boolean isAchievement;
    @SerializedName("json_key")
    private String jsonKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriorityIr that = (PriorityIr) o;

        return new EqualsBuilder()
                .append(getId(), that.getId())
                .append(getSnapshotIndicatorId(), that.getSnapshotIndicatorId())
                .append(getIndicatorTitle(), that.getIndicatorTitle())
                .append(getReason(), that.getReason())
                .append(getAction(), that.getAction())
                .append(getEstimatedDate(), that.getEstimatedDate())
                .append(isAchievement(), that.isAchievement())
                .append(getJsonKey(), that.getJsonKey())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(53, 19)
                .append(getId())
                .append(getSnapshotIndicatorId())
                .append(getIndicatorTitle())
                .append(getReason())
                .append(getAction())
                .append(getEstimatedDate())
                .append(isAchievement())
                .append(getJsonKey())
                .toHashCode();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSnapshotIndicatorId() {
        return snapshotIndicatorId;
    }

    public void setSnapshotIndicatorId(long snapshotIndicatorId) {
        this.snapshotIndicatorId = snapshotIndicatorId;
    }

    public String getIndicatorTitle() {
        return indicatorTitle;
    }

    public void setIndicatorTitle(String indicatorTitle) {
        this.indicatorTitle = indicatorTitle;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEstimatedDate() {
        return estimatedDate;
    }

    public void setEstimatedDate(String estimatedDate) {
        this.estimatedDate = estimatedDate;
    }

    public boolean isAchievement() {
        return isAchievement;
    }

    public void setAchievement(boolean achievement) {
        isAchievement = achievement;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public void setJsonKey(String jsonKey) {
        this.jsonKey = jsonKey;
    }
}
