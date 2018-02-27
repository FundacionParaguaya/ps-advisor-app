package org.fundacionparaguaya.advisorapp.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.fundacionparaguaya.advisorapp.data.local.Converters;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static org.fundacionparaguaya.advisorapp.models.BackgroundQuestion.QuestionType.ECONOMIC;
import static org.fundacionparaguaya.advisorapp.models.BackgroundQuestion.QuestionType.PERSONAL;

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
public class Snapshot implements Comparable<Snapshot>{
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "remote_id")
    private Long remoteId;
    @ColumnInfo(name = "family_id")
    private Integer familyId;
    @ColumnInfo(name = "survey_id")
    private int surveyId;
    @ColumnInfo(name = "personal_responses")
    private Map<BackgroundQuestion, String> personalResponses;
    @ColumnInfo(name = "economic_responses")
    private Map<BackgroundQuestion, String> economicResponses;
    @ColumnInfo(name = "indicator_responses")
    private Map<IndicatorQuestion, IndicatorOption> indicatorResponses;
    @ColumnInfo(name = "priorities")
    private List<LifeMapPriority> priorities;
    @ColumnInfo(name = "created_at")
    private Date createdAt;


    @Ignore
    boolean mIsLatest;

    @Ignore
    public Snapshot(Survey survey) {
        this(null, survey);
    }

    @Ignore
    public Snapshot(Family family, Survey survey) {
        this(0, null, family == null ? null : family.getId(), survey.getId(),
                new HashMap<>(), new HashMap<>(), new HashMap<>(), new LinkedList<>(), new Date());
        if (family != null) {
            fillPersonalResponses(family, survey);
        }
    }

    public Snapshot(int id,
                    Long remoteId,
                    Integer familyId,
                    int surveyId,
                    Map<BackgroundQuestion, String> personalResponses,
                    Map<BackgroundQuestion, String> economicResponses,
                    Map<IndicatorQuestion, IndicatorOption> indicatorResponses,
                    List<LifeMapPriority> priorities,
                    Date createdAt) {
        this.id = id;
        this.remoteId = remoteId;
        this.familyId = familyId;
        this.surveyId = surveyId;
        this.personalResponses = personalResponses;
        this.economicResponses = economicResponses;
        this.indicatorResponses = indicatorResponses;
        if (indicatorResponses != null) {
            for (IndicatorQuestion question : indicatorResponses.keySet()) {
                IndicatorOption response = indicatorResponses.get(question);
                if (response != null)
                    response.setIndicator(question.getIndicator());
            }
        }
        this.priorities = priorities;
        this.createdAt = createdAt;
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

    public void setRemoteId(Long remoteId) {
        this.remoteId = remoteId;
    }

    /**
     * Gets the id of the family, or null if one hasn't been assigned yet.
     */
    public Integer getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int id) {
        familyId = id;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Map<BackgroundQuestion, String> getPersonalResponses() {
        return personalResponses;
    }

    private void fillPersonalResponses(Family family, Survey survey) {
        if (family.getMember() != null) {
            FamilyMember member = family.getMember();
            personalResponseByName(survey, "identificationType", member.getIdentificationType());
            personalResponseByName(survey, "identificationNumber", member.getIdentificationNumber());
            personalResponseByName(survey, "firstName", member.getFirstName());
            personalResponseByName(survey, "lastName", member.getLastName());
            personalResponseByName(survey, "birthdate", member.getBirthdate());
            personalResponseByName(survey, "countryOfBirth", member.getCountryOfBirth());
            personalResponseByName(survey, "gender", member.getGender());
            personalResponseByName(survey, "phoneNumber", member.getPhoneNumber());
        }
    }

    private void personalResponseByName(Survey survey, String name, String value) {
        for (BackgroundQuestion question : survey.getPersonalQuestions()) {
            if (question.getName().equals(name)) {
                response(question, value);
                return;
            }
        }

    }

    public Map<BackgroundQuestion, String> getEconomicResponses() {
        return economicResponses;
    }

    public List<LifeMapPriority> getPriorities() {
        return priorities;
    }

    /**
     * Get the response to the given background question.
     */
    public String getBackgroundResponse(BackgroundQuestion question) {
        if (question.getQuestionType() == PERSONAL) {
            return personalResponses.get(question);
        } else if (question.getQuestionType() == ECONOMIC) {
            return economicResponses.get(question);
        } else {
            throw new UnsupportedOperationException(
                    String.format("Unknown question type %s", question.getQuestionType().toString()));
        }
    }

    /**
     * Get all of the current responses to indicator questions.
     */
    public Map<IndicatorQuestion, IndicatorOption> getIndicatorResponses() {
        return indicatorResponses;
    }

    /**
     * Record a response for a background question.
     */
    public void response(BackgroundQuestion question, String response) {
        if (question.getQuestionType() == PERSONAL)
            personalResponses.put(question, response);
        else if (question.getQuestionType() == ECONOMIC)
            economicResponses.put(question, response);
        else
            throw new UnsupportedOperationException(
                    String.format("Unknown question type %s", question.getQuestionType().toString()));
    }

    /**
     * Record a response for a indicator question.
     */
    public void response(IndicatorQuestion question, IndicatorOption response) {
        indicatorResponses.put(question, response);
    }

    /**
     * Record a priority for the snapshot. The priority order is the order that they are recorded.
     */
    public void priority(LifeMapPriority priority) {
        priorities.add(priority);
    }

    @Override
    public String toString() {

        PrettyTime prettyTime = new PrettyTime();

        if(createdAt ==null) createdAt = new Date();

        if(mIsLatest)
        {
            return "Latest: " + prettyTime.format(createdAt);
        }
        else return DateFormat.format("MM/dd/yyyy", createdAt).toString();
    }

    /**Kinda hacky fix to the fact that it's easiest for spinners to hold objects
     * with a toString... and we want the toString to report "Latest" when the snapshot is the latest
     * (instead of the date)
     * //TODO someday implement this on the mapper side (or custom adapter for spinner) @krconv
     */
    public void setIsLatest(boolean isLatest)
    {
        mIsLatest = isLatest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Snapshot that = (Snapshot) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(remoteId, that.remoteId)
                .append(familyId, that.familyId)
                .append(surveyId, that.surveyId)
                .append(personalResponses, that.personalResponses)
                .append(economicResponses, that.economicResponses)
                .append(indicatorResponses, that.indicatorResponses)
                .append(priorities, that.priorities)
                .append(createdAt, that.createdAt)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(41, 59)
                .append(id)
                .append(remoteId)
                .append(familyId)
                .append(surveyId)
                .append(personalResponses)
                .append(economicResponses)
                .append(indicatorResponses)
                .append(priorities)
                .append(createdAt)
                .toHashCode();
    }

    @Override
    public int compareTo(@NonNull Snapshot snapshot) {
        return this.getCreatedAt().compareTo(snapshot.getCreatedAt());
    }
}
