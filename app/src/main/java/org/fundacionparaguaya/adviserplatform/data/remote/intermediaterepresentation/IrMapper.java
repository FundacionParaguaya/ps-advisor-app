package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import android.util.Log;

import org.fundacionparaguaya.adviserplatform.data.model.BackgroundQuestion;
import org.fundacionparaguaya.adviserplatform.data.model.Family;
import org.fundacionparaguaya.adviserplatform.data.model.FamilyMember;
import org.fundacionparaguaya.adviserplatform.data.model.Indicator;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorQuestion;
import org.fundacionparaguaya.adviserplatform.data.model.LifeMapPriority;
import org.fundacionparaguaya.adviserplatform.data.model.Login;
import org.fundacionparaguaya.adviserplatform.data.model.ResponseType;
import org.fundacionparaguaya.adviserplatform.data.model.Snapshot;
import org.fundacionparaguaya.adviserplatform.data.model.Survey;
import org.fundacionparaguaya.adviserplatform.util.IndicatorUtilities;

import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import timber.log.Timber;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.fundacionparaguaya.adviserplatform.data.model.BackgroundQuestion.QuestionType.ECONOMIC;
import static org.fundacionparaguaya.adviserplatform.data.model.BackgroundQuestion.QuestionType.PERSONAL;
import static org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption.Level.Green;
import static org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption.Level.None;
import static org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption.Level.Red;
import static org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption.Level.Yellow;

/**
 * A utility for mapping IR objects to their corresponding model objects, and vice versa.
 */

public class IrMapper {
    public static String TAG = "IrMapper";

    //region Login
    public static Login mapLogin(LoginIr ir) {
        return new Login(ir.accessToken, ir.tokenType, ir.expiresIn, ir.refreshToken, ir.user);
    }
    //endregion Login

    //region Family
    public static List<Family> mapFamilies(List<FamilyIr> ir) {
        if (ir == null) return Collections.emptyList();

        List<Family> families = new ArrayList<>(ir.size());
        for (FamilyIr familyIr : ir) {
            families.add(mapFamily(familyIr));
        }
        return families;
    }

    public static Family mapFamily(FamilyIr ir) {
        if (ir == null) return null;

        return Family.builder()
                .remoteId(ir.id)
                .name(ir.name)
                .code(ir.code)
                .member(mapFamilyMember(ir.member))
                .imageUrl(ir.imageUrl)
                .build();
    }

    private static FamilyMember mapFamilyMember(FamilyMemberIr ir) {
        if (ir == null) return null;

        return FamilyMember.builder()
                .firstName(ir.firstName)
                .lastName(ir.lastName)
                .birthdate(ir.birthdate)
                .countryOfBirth(ir.countryOfBirth != null ? ir.countryOfBirth.get("alfa2Code") : null)
                .phoneNumber(ir.phoneNumber)
                .identificationType(ir.identificationType)
                .identificationNumber(ir.identificationNumber)
                .gender(ir.gender)
                .profileUrl(ir.profileUrl)
                .build();
    }
    //endregion

    //region Survey
    public static List<Survey> mapSurveys(List<SurveyIr> ir) {
        if (ir == null) return Collections.emptyList();

        List<Survey> surveys = new ArrayList<>(ir.size());
        for (SurveyIr surveyIr : ir) {
            surveys.add(mapSurvey(surveyIr));
        }
        return surveys;
    }

    public static Survey mapSurvey(SurveyIr ir) {
        if (ir == null) return null;

        return Survey.builder()
                .remoteId(ir.id)
                .title(ir.title)
                .description(ir.description)
                .personalQuestions(mapPersonal(ir))
                .economicQuestions(mapEconomic(ir))
                .indicatorQuestions(mapIndicator(ir))
                .build();
    }

    private static List<BackgroundQuestion> mapPersonal(SurveyIr ir) {
        return mapBackground(PERSONAL, ir);
    }

    private static List<BackgroundQuestion> mapEconomic(SurveyIr ir) {
        return mapBackground(ECONOMIC, ir);
    }

    private static List<BackgroundQuestion> mapBackground(
            BackgroundQuestion.QuestionType type, SurveyIr ir) {
        if (ir.uiSchema == null) {
            return Collections.emptyList();
        }

        List<String> allQuestionsOrdered = ir.uiSchema.order;
        List<String> questionsToMap = type == PERSONAL ?
                ir.uiSchema.personalQuestions : ir.uiSchema.economicQuestions;
        if (allQuestionsOrdered == null || questionsToMap == null) return Collections.emptyList();

        List<BackgroundQuestion> mappedQuestions = new ArrayList<>();
        for (String questionName : allQuestionsOrdered) {
            if (!questionsToMap.contains(questionName)) {
                continue;
            }

            SurveyQuestionIr questionIr = ir.schema.questions.get(questionName);
            if (questionIr == null) {
                Timber.w( format("mapBackground: A non-existent question (%s) was referenced in "
                        + "survey (id: %d) UI schema!", questionName, ir.id));
                continue;
            }
            mappedQuestions.add(new BackgroundQuestion(
                    questionName,
                    questionIr.getTitle().get("es"),
                    ir.schema.requiredQuestions.contains(questionName),
                    mapResponseType(questionIr, ir.uiSchema.customFields.get(questionName)),
                    type,
                    mapBackgroundOptions(questionIr.getOptionNames(), questionIr.getOptions())));
        }
        return mappedQuestions;
    }

    private static List<IndicatorQuestion> mapIndicator(SurveyIr ir) {
        if (ir.uiSchema == null || ir.uiSchema.indicatorQuestions == null) {
            return Collections.emptyList();
        }

        List<IndicatorQuestion> questions = new ArrayList<>();
        for (String name : ir.uiSchema.indicatorQuestions) {
            SurveyQuestionIr questionIr = ir.schema.questions.get(name);

            final Map<String, String> description = questionIr.getDescription();
            final Map<String, String> title = questionIr.getTitle();
            Indicator indicator = new Indicator(name,
                    getLabel(questionIr, description, title,"es", "description:%s"),
                    getLabel(questionIr, title, description,"es", "title:%s"));
            List<IndicatorOption> options = new ArrayList<>();
            for (IndicatorOptionIr optionIr : questionIr.getIndicatorOptions().values) {
                options.add(new IndicatorOption(
                        optionIr.description,
                        IndicatorUtilities.generateUri(optionIr.url),
                        mapOptionLevel(optionIr.value)
                ));
            }
            indicator.setOptions(options);

            boolean required = ir.schema.requiredQuestions.contains(name);
            questions.add(new IndicatorQuestion(indicator, required));
        }
        return questions;
    }

    private static String getLabel(SurveyQuestionIr questionIr, Map<String, String> option1,
                                   Map<String, String> option2, String lang, String defaultString) {
        String val1 = String.format(defaultString, questionIr);
        if (option1 != null) {
            val1 = option1.get(lang);
        } else if (option2 != null) {
            val1 = option2.get(lang);
        }
        return val1;
    }

    private static Map<String, String> mapBackgroundOptions(List<String> names, List<String> values) {
        if (names == null || values == null) {
            return Collections.emptyMap();
        }

        HashMap<String, String> options = new LinkedHashMap<>();
        for (int i = 0; i < values.size(); i++) {
            options.put(names.get(i), values.get(i));
        }

        return options;
    }

    private static ResponseType mapResponseType(SurveyQuestionIr ir,
                                                SurveyCustomFieldIr fieldIr) {
        switch (ir.getType()) {
            case "string":
                if (ir.getFormat() != null && ir.getFormat().equals("date")) {
                    return ResponseType.DATE;
                } else if (fieldIr != null
                        && "gmap".equals(fieldIr.field)) {
                    return ResponseType.LOCATION;
                } else if (fieldIr != null
                        && "photo".equals(fieldIr.field)) {
                    return ResponseType.PHOTO;
                }

                return ResponseType.STRING;
            case "number":
                return ResponseType.INTEGER;
            case "integer":
                return ResponseType.INTEGER;
            default:
                throw new IllegalArgumentException("Response type not known!");
        }
    }

    private static IndicatorOption.Level mapOptionLevel(String ir) {
        switch (ir) {
            case "GREEN":
                return Green;
            case "YELLOW":
                return Yellow;
            case "RED":
                return Red;
            default:
                return None;
        }
    }
    //endregion

    //region Snapshot
    public static List<Snapshot> mapSnapshots(List<SnapshotIr> ir,
                                              List<SnapshotOverviewIr> overviewIrs,
                                              Family family,
                                              List<Survey> surveyList) {
        if (ir == null || overviewIrs == null) return Collections.emptyList();

        List<Snapshot> snapshots = new ArrayList<>(ir.size());
        for (SnapshotIr snapshotIr : ir) {
            SnapshotOverviewIr overviewIr = findOverview(snapshotIr, overviewIrs);

            Survey survey = getCurrentSurvey(surveyList, snapshotIr);

            if(survey != null) {
                snapshots.add(mapSnapshot(snapshotIr, overviewIr != null ? overviewIr.priorities : null,
                        family, survey));
            } else {
                Log.d("Snapshots",
                        String.format("Snapshot with id %s and survey id %s could not be added.",
                                snapshotIr.getId(), snapshotIr.getSurveyId()));
            }
        }
        return snapshots;
    }

    private static Survey getCurrentSurvey(List<Survey> surveyList, SnapshotIr snapshot) {
        Survey currentSurvey = null;
        for(Survey survey : surveyList) {
            if(survey.getRemoteId() == snapshot.getSurveyId()) {
                currentSurvey = survey;
                return currentSurvey;
            }
        }
        return currentSurvey;
    }

    private static SnapshotOverviewIr findOverview(SnapshotIr snapshotIr,
                                                   List<SnapshotOverviewIr> overviewIrs) {
        for (SnapshotOverviewIr overviewIr : overviewIrs) {
            if (overviewIr.snapshotId == snapshotIr.getId()) {
                return overviewIr;
            }
        }
        return null;
    }

    public static Snapshot mapSnapshot(SnapshotIr snapshotIr, List<PriorityIr> priorityIrs,
                                       Family family, Survey survey) {
        if (snapshotIr == null) return null;

        return new Snapshot(
                0,
                snapshotIr.getId(),
                family.getId(),
                survey.getId(),
                false,
                snapshotIr.getSnapshotIndicatorId(),
                mapPersonalResponses(snapshotIr, survey),
                mapEconomicResponses(snapshotIr, survey),
                mapIndicatorResponses(snapshotIr, survey),
                mapPriorities(priorityIrs, survey),
                mapDateTime(snapshotIr.getCreatedAt()));
    }

    public static SnapshotIr mapSnapshot(Snapshot snapshot, Survey survey) {
        SnapshotIr ir = new SnapshotIr();
        ir.setId(snapshot.getRemoteId() != null ? snapshot.getRemoteId() : -1);
        ir.setSurveyId(survey.getRemoteId());
        ir.setPersonalResponses(mapBackgroundResponses(snapshot.getPersonalResponses()));
        ir.setEconomicResponses(mapBackgroundResponses(snapshot.getEconomicResponses()));
        ir.setIndicatorResponses(mapIndicatorResponses(snapshot.getIndicatorResponses()));
        ir.setCreatedAt(mapDateTime(snapshot.getCreatedAt()));
        ir.setOrganizationId(snapshot.getOrganizationId());

        return ir;
    }

    public static List<PriorityIr> mapPriorities(Snapshot snapshot) {
        if (snapshot.getPriorities() == null) return Collections.emptyList();

        List<PriorityIr> ir = new ArrayList<>(snapshot.getPriorities().size());
        for (LifeMapPriority priority : snapshot.getPriorities()) {
            ir.add(mapPriority(priority, snapshot));
        }
        return ir;
    }

    private static PriorityIr mapPriority(LifeMapPriority priority, Snapshot snapshot) {
        PriorityIr ir = new PriorityIr();
        ir.setIndicatorTitle(priority.getIndicator().getDescription());
        ir.setSnapshotIndicatorId(snapshot.getSnapshotIndicatorId());
        ir.setReason(defaultIfEmpty(priority.getReason(), ""));
        ir.setAction(defaultIfEmpty(priority.getAction(), ""));
        ir.setEstimatedDate(mapDate(priority.getEstimatedDate()));
        ir.setAchievement(priority.isAchievement());
        ir.setJsonKey(priority.getIndicator().getName());
        return ir;
    }

    private static Map<String, Object> mapBackgroundResponses(Map<BackgroundQuestion, String> responses) {
        if (responses == null) return Collections.emptyMap();

        Map<String, Object> ir = new HashMap<>();
        for (BackgroundQuestion question : responses.keySet()) {
            Object response = responses.get(question);
            if (question.getResponseType() == ResponseType.INTEGER) {
                response = Integer.parseInt((String) response);
            }
            ir.put(question.getName(), response);
        }
        return ir;
    }

    private static Map<String, String> mapIndicatorResponses(Map<IndicatorQuestion, IndicatorOption> responses) {
        if (responses == null) return Collections.emptyMap();

        Map<String, String> ir = new HashMap<>();
        for (IndicatorQuestion question : responses.keySet()) {
            ir.put(question.getName(), mapIndicatorOptionLevel(responses.get(question).getLevel()));
        }
        return ir;
    }

    private static Map<BackgroundQuestion, String> mapPersonalResponses(SnapshotIr ir, Survey survey) {
        if (ir.getPersonalResponses() == null) return Collections.emptyMap();

        Map<BackgroundQuestion, String> responses = new HashMap<>();
        for (String question : ir.getPersonalResponses().keySet()) {
            responses.put(getBackgroundQuestion(survey.getPersonalQuestions(), question),
                    mapBackgroundResponseValue(ir.getPersonalResponses().get(question)));
        }
        return responses;
    }

    private static Map<BackgroundQuestion, String> mapEconomicResponses(SnapshotIr ir, Survey survey) {
        if (ir.getEconomicResponses() == null) return Collections.emptyMap();

        Map<BackgroundQuestion, String> responses = new HashMap<>();
        for (String question : ir.getEconomicResponses().keySet()) {
            responses.put(getBackgroundQuestion(survey.getEconomicQuestions(), question),
                    mapBackgroundResponseValue(ir.getEconomicResponses().get(question)));
        }
        return responses;

    }

    private static String mapBackgroundResponseValue(Object ir) {
        if (ir == null) return null;
        return ir.toString();
    }

    private static Map<IndicatorQuestion, IndicatorOption> mapIndicatorResponses(SnapshotIr ir, Survey survey) {
        if (ir.getIndicatorResponses() == null) return Collections.emptyMap();

        Map<IndicatorQuestion, IndicatorOption> responses = new HashMap<>();
        for (String question : ir.getIndicatorResponses().keySet()) {
            IndicatorQuestion indicatorQuestion =
                    getIndicatorQuestion(survey.getIndicatorQuestions(), question);
            if (indicatorQuestion != null) {
                responses.put(indicatorQuestion,
                        getIndicatorOption(indicatorQuestion.getOptions(),
                                mapIndicatorOptionLevel(ir.getIndicatorResponses().get(question))));
            }
        }
        return responses;
    }

    public static List<LifeMapPriority> mapPriorities(List<PriorityIr> ir, Survey survey) {
        if (ir == null) return Collections.emptyList();

        List<LifeMapPriority> priorities = new ArrayList<>(ir.size());
        for (PriorityIr priorityIr : ir) {
            priorities.add(mapPriority(priorityIr, survey));
        }
        return priorities;
    }

    private static LifeMapPriority mapPriority(PriorityIr ir, Survey survey) {
        if (ir == null) return null;


        //TODO SODEP: The current way of mapping a question is not suitable for surveys that are in spanish
        /**
         * If jsonKey is null, then search for the question the old way
         * if is not null, then use the jsonKey to find the question*/
        String keyToMatch = ir.getJsonKey() == null ? mapIndicatorName(ir.getIndicatorTitle()) : ir.getJsonKey();
        IndicatorQuestion question = getIndicatorQuestion(survey.getIndicatorQuestions(),
                keyToMatch);

        if (question == null) return null;

        return LifeMapPriority.builder()
                .indicator(question.getIndicator())
                .reason(ir.getReason())
                .action(ir.getAction())
                .estimatedDate(mapDate(ir.getEstimatedDate()))
                .isAchievement(ir.isAchievement())
                .build();
    }

    private static BackgroundQuestion getBackgroundQuestion(List<BackgroundQuestion> questions, String name) {
        for (BackgroundQuestion question : questions) {
            if (question.getName().equals(name))
                return question;
        }
        return null;
    }

    private static IndicatorQuestion getIndicatorQuestion(List<IndicatorQuestion> questions, String name) {
        for (IndicatorQuestion question : questions) {
            if (question.getName().equals(name))
                return question;
        }
        return null;
    }

    private static IndicatorOption getIndicatorOption(List<IndicatorOption> indicatorOptions, IndicatorOption.Level level) {
        for (IndicatorOption option : indicatorOptions) {
            if (option.getLevel() == level)
                return option;
        }
        return null;
    }

    private static Date mapDateTime(String ir) {
        if (ir == null) return null;

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        df.setTimeZone(tz);
        try {
            return df.parse(ir);
        } catch (ParseException e) {
            return null;
        }
    }

    private static String mapDateTime(Date date) {
        if (date == null) return null;

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        df.setTimeZone(tz);
        return df.format(date);
    }

    private static Date mapDate(String ir) {
        if (ir == null) return null;

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        df.setTimeZone(tz);
        try {
            return df.parse(ir);
        } catch (ParseException e) {
            return null;
        }
    }

    private static String mapDate(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        df.setTimeZone(tz);
        return df.format(date != null ? date : new Date());
    }

    private static IndicatorOption.Level mapIndicatorOptionLevel(String level) {
        if (level == null) return None;

        switch (level.toLowerCase()) {
            case "red":
                return Red;
            case "yellow":
                return Yellow;
            case "green":
                return Green;
            default:
                return None;
        }
    }

    private static String mapIndicatorOptionLevel(IndicatorOption.Level level) {
        switch (level) {
            case Red:
                return "RED";
            case Yellow:
                return "YELLOW";
            case Green:
                return "GREEN";
            default:
                return "";
        }
    }

    /**
     * Maps a "pretty" indicator name to it's referable value.
     */
    private static String mapIndicatorName(String title) {
        if (title == null) return "";
        String[] words = title.split(" ");
        if (words.length == 0) {
            throw new UnsupportedOperationException(
                    "The given indicator title couldn't be converted! " + title);
        }
        // convert from words to camel case
        StringBuilder result = new StringBuilder();
        result.append(words[0].toLowerCase());
        for (int i = 1; i < words.length; i++) {
            String word = words[i];
            result.append(word.substring(0, 1).toUpperCase());
            result.append(word.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * Maps a indicator to a "pretty" name.
     */
    private static String mapIndicatorName(Indicator indicator) {
        StringBuilder result = new StringBuilder();
        CharacterIterator it = new StringCharacterIterator(indicator.getName());
        for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(Character.toUpperCase(c));
            c = it.next();
            while (Character.isLowerCase(c)) {
                result.append(c);
                c = it.next();
            }
            it.previous();
        }
        return result.toString();
    }
    //endregion Snapshot
}
