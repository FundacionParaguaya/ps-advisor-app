package org.fundacionparaguaya.advisorapp.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.fundacionparaguaya.advisorapp.data.local.Converters;

import java.util.List;

/**
 * A survey is a set of immutable questions which can be presented to a family in order to
 * collect a snapshot.
 */

@Entity(tableName = "surveys",
        indices={@Index(value="remote_id", unique=true)})
@TypeConverters(Converters.class)
public class Survey {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name="remote_id")
    private Long remoteId;
    @ColumnInfo(name="title")
    private String title;
    @ColumnInfo(name="description")
    private String description;
    @ColumnInfo(name="personal_questions")
    private List<BackgroundQuestion> personalQuestions;
    @ColumnInfo(name="economic_questions")
    private List<BackgroundQuestion> economicQuestions;
    @ColumnInfo(name="indicator_questions")
    private List<IndicatorQuestion> indicatorQuestions;

    public Survey(int id,
                  Long remoteId,
                  String title,
                  String description,
                  List<BackgroundQuestion> personalQuestions,
                  List<BackgroundQuestion> economicQuestions,
                  List<IndicatorQuestion> indicatorQuestions) {
        this.id = id;
        this.remoteId = remoteId;
        this.title = title;
        this.description = description;
        this.personalQuestions = personalQuestions;
        this.economicQuestions = economicQuestions;
        this.indicatorQuestions = indicatorQuestions;

        if (indicatorQuestions != null) {
            for (IndicatorQuestion question : indicatorQuestions) {
                for (IndicatorOption option : question.getOptions()) {
                    option.setIndicator(question.getIndicator());
                }
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getRemoteId() {
        return remoteId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<BackgroundQuestion> getPersonalQuestions() {
        return personalQuestions;
    }

    public List<BackgroundQuestion> getEconomicQuestions() {
        return economicQuestions;
    }

    public List<IndicatorQuestion> getIndicatorQuestions() {
        return indicatorQuestions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Survey that = (Survey) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(remoteId, that.remoteId)
                .append(title, that.title)
                .append(description, that.description)
                .append(personalQuestions, that.personalQuestions)
                .append(economicQuestions, that.economicQuestions)
                .append(indicatorQuestions, that.indicatorQuestions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 11)
                .append(id)
                .append(remoteId)
                .append(title)
                .append(description)
                .append(personalQuestions)
                .append(economicQuestions)
                .append(indicatorQuestions)
                .toHashCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long remoteId;
        private String title;
        private String description;
        private List<BackgroundQuestion> personalQuestions;
        private List<BackgroundQuestion> economicQuestions;
        private List<IndicatorQuestion> indicatorQuestions;

        public Builder remoteId(Long remoteId) {
            this.remoteId = remoteId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder personalQuestions(List<BackgroundQuestion> personalQuestions) {
            this.personalQuestions = personalQuestions;
            return this;
        }

        public Builder economicQuestions(List<BackgroundQuestion> economicQuestions) {
            this.economicQuestions = economicQuestions;
            return this;
        }

        public Builder indicatorQuestions(List<IndicatorQuestion> indicatorQuestions) {
            this.indicatorQuestions = indicatorQuestions;
            return this;
        }

        public Survey build() {
            return new Survey(0, remoteId, title, description,
                    personalQuestions, economicQuestions, indicatorQuestions);
        }
    }
}
