package org.fundacionparaguaya.advisorapp.data.model;

import android.arch.persistence.room.ColumnInfo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

/**
 * A goal that a family has set for their life map in a snapshot.
 */

public class LifeMapPriority {
    @ColumnInfo(name = "indicator")
    private Indicator indicator;
    @ColumnInfo(name = "reason")
    private String reason;
    @ColumnInfo(name = "action")
    private String action;
    @ColumnInfo(name = "estimated_date")
    private Date estimatedDate;
    @ColumnInfo(name = "is_achievement")
    private boolean isAchievement;

    public LifeMapPriority(Indicator indicator, String reason, String action, Date estimatedDate,
                           boolean isAchievement) {
        this.indicator = indicator;
        this.reason = reason;
        this.action = action;
        this.estimatedDate = estimatedDate;
        this.isAchievement = isAchievement;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String s) {
        this.reason = s;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String s) {
        this.action = s;
    }

    public Date getEstimatedDate() {
        return estimatedDate;
    }

    public void setEstimatedDate(Date when) {
        this.estimatedDate = when;
    }

    public boolean isAchievement() {
        return isAchievement;
    }

    public void setAchievement(boolean achievement) {
        isAchievement = achievement;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LifeMapPriority that = (LifeMapPriority) o;

        return new EqualsBuilder()
                .append(indicator, that.indicator)
                .append(reason, that.reason)
                .append(action, that.action)
                .append(estimatedDate, that.estimatedDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(53, 31)
                .append(indicator)
                .append(reason)
                .append(action)
                .append(estimatedDate)
                .toHashCode();
    }

    public static class Builder {
        private Indicator indicator;
        private String reason;
        private String action;
        private Date estimatedDate;
        private boolean isAchievement;

        public Builder indicator(Indicator indicator) {
            this.indicator = indicator;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder estimatedDate(Date estimatedDate) {
            this.estimatedDate = estimatedDate;
            return this;
        }

        public Builder isAchievement(boolean isAchievement) {
            this.isAchievement = isAchievement;
            return this;
        }

        public LifeMapPriority build() {
            return new LifeMapPriority(indicator, reason, action, estimatedDate, isAchievement);
        }
    }
}
