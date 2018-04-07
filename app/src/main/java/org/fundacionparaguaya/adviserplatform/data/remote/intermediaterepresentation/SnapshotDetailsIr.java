package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

/**
 * The intermediate representation of the snapshot details from the remote database.
 */

public class SnapshotDetailsIr {
    @SerializedName("snapshot_economic_id")
    long snapshotId;
    @SerializedName("survey_id")
    long surveyId;
    @SerializedName("family_id")
    long familyId;
    @SerializedName("family")
    FamilyIr family;
    @SerializedName("count_red_indicators")
    int redIndicatorCount;
    @SerializedName("count_yellow_indicators")
    int yellowIndicatorCount;
    @SerializedName("count_green_indicators")
    int greenIndicatorCount;
    @SerializedName("created_at")
    String createdAt;

    public FamilyIr getFamily() {
        return family;
    }
}
