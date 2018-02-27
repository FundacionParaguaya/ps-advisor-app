package org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The intermediate representation of a life map priority from the remote database.
 */

public class PriorityIr {
    @SerializedName("snapshot_indicator_priority_id")
    long id;
    @SerializedName("snapshot_indicator_id")
    long snapshotId;
    @SerializedName("indicator")
    String indicatorTitle;
    @SerializedName("reason")
    String reason;
    @SerializedName("action")
    String action;
    @SerializedName("estimated_date")
    String estimatedDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriorityIr that = (PriorityIr) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(snapshotId, that.snapshotId)
                .append(indicatorTitle, that.indicatorTitle)
                .append(reason, that.reason)
                .append(action, that.action)
                .append(estimatedDate, that.estimatedDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(53, 19)
                .append(id)
                .append(snapshotId)
                .append(indicatorTitle)
                .append(reason)
                .append(action)
                .append(estimatedDate)
                .toHashCode();
    }
}
