package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * The intermediate representation of the survey UI schema from the remote database.
 */

public class SurveyUiSchemaIr {
    @SerializedName("ui:order")
    List<String> order;
    @SerializedName("ui:group:personal")
    List<String> personalQuestions;
    @SerializedName("ui:group:economics")
    List<String> economicQuestions;
    @SerializedName("ui:group:indicators")
    List<String> indicatorQuestions;
    @SerializedName("ui:custom:fields")
    Map<String, SurveyCustomFieldIr> customFields;
}
