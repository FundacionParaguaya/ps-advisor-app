package org.fundacionparaguaya.advisorapp.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;
import java.util.HashMap;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * A snapshot represents the family's level of poverty at a specific point in time. It is
 * defined largely by the survey that the family took. The responses are recorded as the family
 * takes the survey and placed in indicatorResponses. Families are able to skip questions, so
 * indicatorResponses might not have a response for every indicator in the survey.
 */

@Entity(tableName = "snapshots",
        indices = @Index("family_id") ,
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
public class Snapshot {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "family_id")
    private int familyId;
    @ColumnInfo(name = "survey_id")
    private int surveyId;
    @Ignore
    private HashMap<Indicator, IndicatorOption> indicatorResponses;
    @Ignore
    private HashMap<EconomicQuestion, String> economicResponses;
    @Ignore
    private Date date;

    public Snapshot(int id, int familyId, int surveyId) {
        this.id = id;
        this.familyId = familyId;
        this.surveyId = surveyId;
    }
}
