package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * The intermediate representation of the survey question options from the remote database.
 */

public class IndicatorOptionsIr {
    @SerializedName("type")
    String type;
    @SerializedName("enum")
    List<IndicatorOptionIr> values;
}
