package org.fundacionparaguaya.advisorapp.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import org.fundacionparaguaya.advisorapp.data.local.Converters;

import java.util.Map;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * A snapshot represents the family's level of poverty at a specific point in time. It is
 * defined largely by the survey that the family took. The responses are recorded as the family
 * takes the survey and placed in indicatorResponses. Families are able to skip questions, so
 * indicatorResponses might not have a response for every indicator in the survey.
 */

@Entity(tableName = "snapshots",
        indices = {@Index("family_id"), @Index("survey_id")},
        foreignKeys = {
            @ForeignKey(entity = Family.class,
                    parentColumns = "id",
                    childColumns = "family_id",
                    onUpdate = CASCADE,
                    onDelete = CASCADE),
            @ForeignKey(entity = Survey.class,
                    parentColumns = "id",
                    childColumns = "survey_id",
                    onUpdate = CASCADE,
                    onDelete = CASCADE)})
@TypeConverters(Converters.class)
public class Snapshot {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "family_id")
    private int familyId;
    @ColumnInfo(name = "survey_id")
    private int surveyId;
    private Map<PersonalQuestion, String> personalResponses;
    private Map<EconomicQuestion, String> economicResponses;
    private Map<IndicatorQuestion, IndicatorOption> indicatorResponses;

    public Snapshot(int id, int familyId, int surveyId,
                    Map<PersonalQuestion, String> personalResponses,
                    Map<EconomicQuestion, String> economicResponses,
                    Map<IndicatorQuestion, IndicatorOption> indicatorResponses) {
        this.id = id;
        this.familyId = familyId;
        this.surveyId = surveyId;
        this.personalResponses = personalResponses;
        this.economicResponses = economicResponses;
        this.indicatorResponses = indicatorResponses;
    }

    public int getId() {
        return id;
    }

    public int getFamilyId() {
        return familyId;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public Map<PersonalQuestion, String> getPersonalResponses() {
        return personalResponses;
    }

    public Map<EconomicQuestion, String> getEconomicResponses() {
        return economicResponses;
    }

    public Map<IndicatorQuestion, IndicatorOption> getIndicatorResponses() {
        return indicatorResponses;
    }
}
