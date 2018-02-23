package org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

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

        if (id != that.id) return false;
        if (snapshotId != that.snapshotId) return false;
        if (indicatorTitle != null ? !indicatorTitle.equals(that.indicatorTitle) : that.indicatorTitle != null)
            return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
        if (action != null ? !action.equals(that.action) : that.action != null) return false;
        return estimatedDate != null ? estimatedDate.equals(that.estimatedDate) : that.estimatedDate == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (snapshotId ^ (snapshotId >>> 32));
        result = 31 * result + (indicatorTitle != null ? indicatorTitle.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (estimatedDate != null ? estimatedDate.hashCode() : 0);
        return result;
    }
}
