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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SnapshotIr that = (SnapshotIr) o;

        if (getId() != that.getId()) return false;
        if (surveyId != that.surveyId) return false;
        if (userId != that.userId) return false;
        if (termCondId != that.termCondId) return false;
        if (privPoolId != that.privPoolId) return false;
        if (personalResponses != null ? !personalResponses.equals(that.personalResponses) : that.personalResponses != null)
            return false;
        if (economicResponses != null ? !economicResponses.equals(that.economicResponses) : that.economicResponses != null)
            return false;
        if (indicatorResponses != null ? !indicatorResponses.equals(that.indicatorResponses) : that.indicatorResponses != null)
            return false;
        return createdAt != null ? createdAt.equals(that.createdAt) : that.createdAt == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (int) (surveyId ^ (surveyId >>> 32));
        result = 31 * result + (personalResponses != null ? personalResponses.hashCode() : 0);
        result = 31 * result + (economicResponses != null ? economicResponses.hashCode() : 0);
        result = 31 * result + (indicatorResponses != null ? indicatorResponses.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + (int) (termCondId ^ (termCondId >>> 32));
        result = 31 * result + (int) (privPoolId ^ (privPoolId >>> 32));
        return result;
    }
}
