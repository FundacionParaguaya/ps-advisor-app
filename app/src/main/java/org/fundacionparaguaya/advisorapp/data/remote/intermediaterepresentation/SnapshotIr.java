package org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

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
}
