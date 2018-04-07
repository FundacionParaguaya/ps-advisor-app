package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

/**
 * The intermediate representation of the survey from the remote database.
 */

public class SurveyIr {
    @SerializedName("id")
    long id;
    @SerializedName("title")
    String title;
    @SerializedName("description")
    String description;
    @SerializedName("survey_schema")
    SurveySchemaIr schema;
    @SerializedName("survey_ui_schema")
    SurveyUiSchemaIr uiSchema;
    @SerializedName("created_at")
    String createdAt;
    @SerializedName("last_modified_at")
    String lastModifiedAt;
    @SerializedName("user_created")
    UserIr author;
}
