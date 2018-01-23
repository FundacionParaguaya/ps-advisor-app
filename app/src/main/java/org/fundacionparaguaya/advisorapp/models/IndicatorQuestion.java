package org.fundacionparaguaya.advisorapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * A question targeting an indicator which can be presented to a family and responded to from a
 * survey.
 */

public class IndicatorQuestion extends SurveyQuestion {
    @SerializedName("indicator")
    private Indicator indicator;
    @SerializedName("options")
    private List<IndicatorOption> options;

    public IndicatorQuestion(Indicator indicator) {
        super(indicator.getName(), indicator.getDimension(), ResponseType.Indicator);
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        IndicatorQuestion that = (IndicatorQuestion) o;

        if (getIndicator() != null ? !getIndicator().equals(that.getIndicator()) : that.getIndicator() != null) {
            return false;
        }
        return getOptions() != null ? getOptions().equals(that.getOptions()) : that.getOptions() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getIndicator() != null ? getIndicator().hashCode() : 0);
        result = 31 * result + (getOptions() != null ? getOptions().hashCode() : 0);
        return result;
    }
}
