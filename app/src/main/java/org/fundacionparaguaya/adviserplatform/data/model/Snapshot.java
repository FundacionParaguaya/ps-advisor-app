package org.fundacionparaguaya.adviserplatform.data.model;

import android.arch.persistence.room.*;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.fundacionparaguaya.adviserplatform.data.local.Converters;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.*;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static org.fundacionparaguaya.adviserplatform.data.model.BackgroundQuestion.QuestionType.ECONOMIC;
import static org.fundacionparaguaya.adviserplatform.data.model.BackgroundQuestion.QuestionType.PERSONAL;

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
    @ColumnInfo(name = "in_progress")
    private boolean inProgress;
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
    @ColumnInfo(name = "snapshot_indicator_id")
    private Long snapshotIndicatorId;
    @ColumnInfo(name = "priorities_synced")
    private boolean prioritiesSynced;

    @Ignore
    private boolean mIsLatest;

    @Ignore
    private Long organizationId;

    @Ignore
    public Snapshot(Survey survey) {
        this(null, survey);
    }

    @Ignore
    public Snapshot(Family family, Survey survey) {
        this(0, null, family == null ? null : family.getId(), survey.getId(), true, null,
                new HashMap<>(), new HashMap<>(), new LinkedHashMap<>(), new LinkedList<>(), new Date());
        if (family != null) {
            fillPersonalResponses(family, survey);
        }
    }

    public Snapshot(int id,
                    Long remoteId,
                    Integer familyId,
                    int surveyId,
                    boolean inProgress,
                    Long snapshotIndicatorId,
                    Map<BackgroundQuestion, String> personalResponses,
                    Map<BackgroundQuestion, String> economicResponses,
                    Map<IndicatorQuestion, IndicatorOption> indicatorResponses,
                    List<LifeMapPriority> priorities,
                    Date createdAt) {
        this.id = id;
        this.remoteId = remoteId;
        this.familyId = familyId;
        this.surveyId = surveyId;
        this.inProgress = inProgress;
        this.snapshotIndicatorId = snapshotIndicatorId;
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

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public boolean isInProgress() {
        return inProgress;
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
        //TODO Sodep: This should be a different method for ListModels. Pretty time is not a string
        //TODO Sodep: representation of this object
        String dateString;

        if(DateUtils.isToday(createdAt.getTime()))
        {
            PrettyTime prettyTime = new PrettyTime();
            dateString = prettyTime.format(createdAt);
        }
        else
        {
            dateString = new DateTime(createdAt).toString(DateTimeFormat.mediumDate());
        }

        return dateString;
    }

    /**Kinda hacky fix to the fact that it's easiest for spinners to hold objects
     * with a toString... and we want the toString to report "Latest" when the snapshot is the latest
     * (instead of the date)
     * //TODO someday implement this on the mapper side (or custom adapter for spinner) @krconv
     */
    public void setIsLatest(boolean isLatest)
    {
        setmIsLatest(isLatest);
    }


    @Override
    public int compareTo(@NonNull Snapshot snapshot) {
        return this.getCreatedAt().compareTo(snapshot.getCreatedAt());
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }


    public Long getSnapshotIndicatorId() {
        return snapshotIndicatorId;
    }

    public void setSnapshotIndicatorId(Long snapshotIndicatorId) {
        this.snapshotIndicatorId = snapshotIndicatorId;
    }

    public void setPriorities(List<LifeMapPriority> priorities) {
        this.priorities = priorities;
    }


    public boolean ismIsLatest() {
        return mIsLatest;
    }

    public void setmIsLatest(boolean mIsLatest) {
        this.mIsLatest = mIsLatest;
    }

    public boolean isPrioritiesSynced() {
        return prioritiesSynced;
    }

    public void setPrioritiesSynced(boolean prioritiesSynced) {
        this.prioritiesSynced = prioritiesSynced;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Snapshot rhs = (Snapshot) obj;
        return new EqualsBuilder()
                .append(this.id, rhs.id)
                .append(this.remoteId, rhs.remoteId)
                .append(this.familyId, rhs.familyId)
                .append(this.surveyId, rhs.surveyId)
                .append(this.inProgress, rhs.inProgress)
                .append(this.personalResponses, rhs.personalResponses)
                .append(this.economicResponses, rhs.economicResponses)
                .append(this.indicatorResponses, rhs.indicatorResponses)
                .append(this.priorities, rhs.priorities)
                .append(this.createdAt, rhs.createdAt)
                .append(this.snapshotIndicatorId, rhs.snapshotIndicatorId)
                .append(this.prioritiesSynced, rhs.prioritiesSynced)
                .append(this.mIsLatest, rhs.mIsLatest)
                .append(this.organizationId, rhs.organizationId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(remoteId)
                .append(familyId)
                .append(surveyId)
                .append(inProgress)
                .append(personalResponses)
                .append(economicResponses)
                .append(indicatorResponses)
                .append(priorities)
                .append(createdAt)
                .append(snapshotIndicatorId)
                .append(prioritiesSynced)
                .append(mIsLatest)
                .append(organizationId)
                .toHashCode();
    }


    public String toDebugString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("remoteId", remoteId)
                .append("familyId", familyId)
                .append("surveyId", surveyId)
                .append("inProgress", inProgress)
                .append("createdAt", createdAt)
                .append("snapshotIndicatorId", snapshotIndicatorId)
                .append("prioritiesSynced", prioritiesSynced)
                .append("mIsLatest", mIsLatest)
                .append("organizationId", organizationId)
                .toString();
    }
}
