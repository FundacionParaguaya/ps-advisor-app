package org.fundacionparaguaya.advisorapp.models;

import android.arch.persistence.room.ColumnInfo;

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

    public LifeMapPriority(Indicator indicator, String reason, String action, Date estimatedDate) {
        this.indicator = indicator;
        this.reason = reason;
        this.action = action;
        this.estimatedDate = estimatedDate;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public String getReason() {
        return reason;
    }

    public String getAction() {
        return action;
    }

    public Date getEstimatedDate() {
        return estimatedDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void setReason(String s)
    {
        this.reason = s;
    }

    public void setStrategy(String s)
    {
        this.action = s;
    }

    public void setWhen(Date when)
    {
        this.estimatedDate = when;
    }

    public static class Builder {
        private Indicator indicator;
        private String reason;
        private String action;
        private Date estimatedDate;

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

        public LifeMapPriority build() {
            return new LifeMapPriority(indicator, reason, action, estimatedDate);
        }
    }
}
