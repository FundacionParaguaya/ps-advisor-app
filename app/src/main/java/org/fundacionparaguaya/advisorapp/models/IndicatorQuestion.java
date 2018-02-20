package org.fundacionparaguaya.advisorapp.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

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
        if (!super.equals(o)) return false;

        IndicatorQuestion that = (IndicatorQuestion) o;

        if (getIndicator() != null ? !getIndicator().equals(that.getIndicator()) : that.getIndicator() != null)
            return false;
        return getOptions() != null ? getOptions().equals(that.getOptions()) : that.getOptions() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getIndicator() != null ? getIndicator().hashCode() : 0);
        result = 31 * result + (getOptions() != null ? getOptions().hashCode() : 0);
        return result;
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
