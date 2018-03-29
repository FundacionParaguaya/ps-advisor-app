package org.fundacionparaguaya.advisorapp.data.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * A question targeting an indicator which can be presented to a family and responded to from a
 * survey.
 */

public class IndicatorQuestion extends SurveyQuestion implements Comparable {
    @SerializedName("indicator")
    private Indicator indicator;
    @SerializedName("options")
    private List<IndicatorOption> options;

    public IndicatorQuestion(Indicator indicator, boolean required) {
        super(indicator.getName(), indicator.getDimension(), required, ResponseType.INDICATOR);
        this.indicator = indicator;
        this.options = indicator.getOptions();
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public List<IndicatorOption> getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndicatorQuestion that = (IndicatorQuestion) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(that))
                .append(indicator, that.indicator)
                .append(options, that.options)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 5)
                .appendSuper(super.hashCode())
                .append(indicator)
                .append(options)
                .toHashCode();
    }

    @Override
    public int compareTo(@NonNull Object o) {
        try {
            IndicatorQuestion other = (IndicatorQuestion)o;

            //if they are in the same dimension, sort by name
            if(other.getIndicator().getDimension().equals(this.getIndicator().getDimension()))
            {
                return this.getDescription().compareTo(other.getDescription());
            }
            else //sort by dimension
            {
                return this.getIndicator().getDimension().compareTo(other.getIndicator().getDimension());
            }
        }
        catch (ClassCastException e)
        {
            Log.e(this.getClass().getName(), e.getMessage());
            throw e;
        }
    }
}
