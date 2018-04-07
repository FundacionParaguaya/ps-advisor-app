package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * The intermediate representation of the survey schema from the remote database.
 */

public class SurveySchemaIr {
    @SerializedName("title")
    String title;
    @SerializedName("description")
    String description;
    @SerializedName("required")
    List<String> requiredQuestions;
    @SerializedName("properties")
    Map<String, SurveyQuestionIr> questions;
}
