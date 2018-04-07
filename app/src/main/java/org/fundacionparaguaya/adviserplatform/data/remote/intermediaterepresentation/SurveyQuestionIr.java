package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * The intermediate representation of the survey question from the remote database.
 */

public class SurveyQuestionIr {
    @SerializedName("type")
    String type;
    @SerializedName("title")
    Map<String, String> title;
    @SerializedName("items")
    IndicatorOptionsIr indicatorOptions;
    @SerializedName("enum")
    List<String> options;
    @SerializedName("format")
    String format;
    @SerializedName("enumNames")
    List<String> optionNames;

}
