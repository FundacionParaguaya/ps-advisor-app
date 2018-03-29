package org.fundacionparaguaya.advisorapp.data.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *  An indicator option is a definition for one of the three levels an indicator can have. It's level is determined
 *  by the parent indicator
 */

public class IndicatorOption implements Comparable<IndicatorOption> {
    public enum Level {None, Red, Yellow, Green,}

    @SerializedName("description")
    private String description;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("level")
    private Level level;
    private transient Indicator indicator;

    public IndicatorOption(String description, String imageUrl, Level level) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Level getLevel() {
        return level;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndicatorOption that = (IndicatorOption) o;

        return new EqualsBuilder()
                .append(description, that.description)
                .append(imageUrl, that.imageUrl)
                .append(level, that.level)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 7)
                .append(description)
                .append(imageUrl)
                .append(level)
                .toHashCode();
    }

    /**
     * Compares two indicator options based on their levels. Green is the "greatest" value and Red is the "lowest" value
     * (besides None)
     *
     * @param option
     * @return
     */
    @Override
    public int compareTo(@NonNull IndicatorOption option) {
        return Integer.valueOf(this.getLevel().ordinal()).compareTo(option.getLevel().ordinal());
    }
}