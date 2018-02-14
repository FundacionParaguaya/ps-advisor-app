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
}
