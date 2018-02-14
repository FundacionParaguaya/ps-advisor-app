package org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * The intermediate representation of overview information of snapshots from the remote database.
 */

public class SnapshotOverviewIr {
    @SerializedName("snapshot_indicator_id")
    long snapshotId;
    @SerializedName("family_id")
    long familyId;
    @SerializedName("survey_id")
    long surveyId;
    @SerializedName("indicators_priorities")
    List<PriorityIr> priorities;
    @SerializedName("count_red_indicators")
    int redIndicatorCount;
    @SerializedName("count_yellow_indicators")
    int yellowIndicatorCount;
    @SerializedName("count_green_indicators")
    int greenIndicatorCount;
    @SerializedName("created_at")
    String createdAt;
}
