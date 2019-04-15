package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

/**
 * The intermediate representation of the survey question from the remote database.
 */

public class SurveyQuestionIr {
    @SerializedName("type")
    private String type;
    @SerializedName("title")
    private Map<String, String> title;
    @SerializedName("items")
    private IndicatorOptionsIr indicatorOptions;
    @SerializedName("enum")
    private List<String> options;
    @SerializedName("format")
    private String format;
    @SerializedName("enumNames")
    private List<String> optionNames;
    @SerializedName("description")
    private Map<String, String> description;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public IndicatorOptionsIr getIndicatorOptions() {
        return indicatorOptions;
    }

    public void setIndicatorOptions(IndicatorOptionsIr indicatorOptions) {
        this.indicatorOptions = indicatorOptions;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<String> getOptionNames() {
        return optionNames;
    }

    public void setOptionNames(List<String> optionNames) {
        this.optionNames = optionNames;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
                .append("title", title)
                .append("indicatorOptions", indicatorOptions)
                .append("options", options)
                .append("format", format)
                .append("optionNames", optionNames)
                .append("description", description)
                .toString();
    }
}
