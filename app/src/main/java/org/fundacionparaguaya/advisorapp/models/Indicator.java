package org.fundacionparaguaya.advisorapp.models;

import java.util.List;

/**
 * An Indicator is asked during a survey. Each indicator has a red, yellow, and green level. When the family takes the
 * survey, they will choose one of those levels.
 */

public class Indicator {
    private String name;
    private String dimension;
    private List<IndicatorOption> options;

    public Indicator(String name, String dimension, List<IndicatorOption> options) {
        this.name = name;
        this.dimension = dimension;
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public String getDimension() {
        return dimension;
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

        Indicator indicator = (Indicator) o;

        if (getName() != null ? !getName().equals(indicator.getName()) : indicator.getName() != null) {
            return false;
        }
        if (getDimension() != null ? !getDimension().equals(indicator.getDimension()) : indicator.getDimension() != null) {
            return false;
        }
        return getOptions() != null ? getOptions().equals(indicator.getOptions()) : indicator.getOptions() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getDimension() != null ? getDimension().hashCode() : 0);
        result = 31 * result + (getOptions() != null ? getOptions().hashCode() : 0);
        return result;
    }
}
