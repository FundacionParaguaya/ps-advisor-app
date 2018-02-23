package org.fundacionparaguaya.advisorapp.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

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
    @ColumnInfo(name="title")
    private String title;
    @ColumnInfo(name="description")
    private String description;
    @ColumnInfo(name="remote_id")
    private Long remoteId;
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

        Survey survey = (Survey) o;

        if (getId() != survey.getId()) return false;
        if (getTitle() != null ? !getTitle().equals(survey.getTitle()) : survey.getTitle() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(survey.getDescription()) : survey.getDescription() != null)
            return false;
        if (getRemoteId() != null ? !getRemoteId().equals(survey.getRemoteId()) : survey.getRemoteId() != null)
            return false;
        if (getPersonalQuestions() != null ? !getPersonalQuestions().equals(survey.getPersonalQuestions()) : survey.getPersonalQuestions() != null)
            return false;
        if (getEconomicQuestions() != null ? !getEconomicQuestions().equals(survey.getEconomicQuestions()) : survey.getEconomicQuestions() != null)
            return false;
        return getIndicatorQuestions() != null ? getIndicatorQuestions().equals(survey.getIndicatorQuestions()) : survey.getIndicatorQuestions() == null;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getRemoteId() != null ? getRemoteId().hashCode() : 0);
        result = 31 * result + (getPersonalQuestions() != null ? getPersonalQuestions().hashCode() : 0);
        result = 31 * result + (getEconomicQuestions() != null ? getEconomicQuestions().hashCode() : 0);
        result = 31 * result + (getIndicatorQuestions() != null ? getIndicatorQuestions().hashCode() : 0);
        return result;
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
