package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

/**
 * The intermediate representation of the survey custom fields from the remote database.
 */

public class SurveyCustomFieldIr {
    @SerializedName("ui:field")
    String field;
}
