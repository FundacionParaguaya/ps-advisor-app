package org.fundacionparaguaya.advisorapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * An indicator option is a definition for one of the three levels an indicator can have. It's level is determined
 * by the parent indicator
 */

public class IndicatorOption {
    public enum Level {Red, Yellow, Green, None}

    @SerializedName("description")
    private String description;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("level")
    private Level level;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IndicatorOption that = (IndicatorOption) o;

        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null) {
            return false;
        }
        if (getImageUrl() != null ? !getImageUrl().equals(that.getImageUrl()) : that.getImageUrl() != null) {
            return false;
        }
        return getLevel() == that.getLevel();
    }

    @Override
    public int hashCode() {
        int result = getDescription() != null ? getDescription().hashCode() : 0;
        result = 31 * result + (getImageUrl() != null ? getImageUrl().hashCode() : 0);
        result = 31 * result + (getLevel() != null ? getLevel().hashCode() : 0);
        return result;
    }
}