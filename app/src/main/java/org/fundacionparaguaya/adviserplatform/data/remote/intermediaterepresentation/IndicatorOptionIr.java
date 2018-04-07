package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

/**
 * The intermediate representation of the survey question option from the remote database.
 */

public class IndicatorOptionIr {
    @SerializedName("url")
    String url;
    @SerializedName("value")
    String value;
    @SerializedName("description")
    String description;
}
