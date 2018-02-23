package org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation;

import android.test.suitebuilder.annotation.SmallTest;

import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;
import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.models.FamilyMember;
import org.fundacionparaguaya.advisorapp.models.Indicator;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.models.Survey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrUtils.familyIr;
import static org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrUtils.priorityIrs;
import static org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrUtils.snapshotIr;
import static org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrUtils.surveyIr;
import static org.fundacionparaguaya.advisorapp.models.BackgroundQuestion.QuestionType.ECONOMIC;
import static org.fundacionparaguaya.advisorapp.models.BackgroundQuestion.QuestionType.PERSONAL;
import static org.fundacionparaguaya.advisorapp.models.IndicatorOption.Level.Green;
import static org.fundacionparaguaya.advisorapp.models.IndicatorOption.Level.Red;
import static org.fundacionparaguaya.advisorapp.models.IndicatorOption.Level.Yellow;
import static org.fundacionparaguaya.advisorapp.models.ResponseType.STRING;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * A test for the functionality of the IrMapper.
 */

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class IrMapperTest {

    //region Family
    @Test
    public void family_ShouldMapFromIr() {
        FamilyIr ir = familyIr();
        Family family = IrMapper.mapFamily(ir);

        assertThat(family, is(family(member())));
    }

    @Test
    public void family_ShouldMapFromIr_nullMembers() {
        FamilyIr ir = mock(FamilyIr.class);
        Family family = IrMapper.mapFamily(ir);

        assertThat(family, is(notNullValue()));
    }

    @Test
    public void member_ShouldMapFromIr_nullMembers() {
        FamilyMemberIr ir = mock(FamilyMemberIr.class);
        FamilyIr familyIr = familyIr(ir);
        FamilyMember member = IrMapper.mapFamily(familyIr).getMember();

        assertThat(member, is(notNullValue()));
    }
    //endregion Family

    //region Survey
    @Test
    public void survey_ShouldMapFromIr() {
        SurveyIr ir = surveyIr();

        Survey survey = IrMapper.mapSurvey(ir);
        survey.setId(1);

        assertThat(survey, is(survey()));
    }

    @Test
    public void survey_ShouldMapFromIr_nullMembers() {
        SurveyIr ir = mock(SurveyIr.class);

        Survey survey = IrMapper.mapSurvey(ir);

        assertThat(survey, is(notNullValue()));
    }
    //endregion Survey

    //region Snapshot
    @Test
    public void snapshot_ShouldMapFromIr() {
        SnapshotIr ir = snapshotIr();
        List<PriorityIr> priorityIrs = priorityIrs();

        priorityIrs.get(0).estimatedDate = "2018-02-13";
        Snapshot snapshot = IrMapper.mapSnapshot(ir, priorityIrs, family(member()), survey());
        snapshot.setId(1);

        assertThat(snapshot, is(snapshot()));
    }

    @Test
    public void snapshot_ShouldMapFromIr_nullMembers() {
        SnapshotIr ir = mock(SnapshotIr.class);
        List<PriorityIr> priorityIrs = new ArrayList<>();
        priorityIrs.add(mock(PriorityIr.class));
        Family family = mock(Family.class);
        Survey survey = mock(Survey.class);

        Snapshot snapshot = IrMapper.mapSnapshot(ir, priorityIrs, family, survey);

        assertThat(snapshot, is(notNullValue()));
    }

    @Test
    public void snapshot_ShouldMapToIr() {
        Snapshot snapshot = snapshot();
        Survey survey = survey();

        SnapshotIr ir = IrMapper.mapSnapshot(snapshot, survey);
        List<PriorityIr> priorityIrs = IrMapper.mapPriorities(snapshot);
        priorityIrs.get(0).id = 1; // remote id not stored locally

        assertThat(ir, is(snapshotIr()));
        assertThat(priorityIrs, is(priorityIrs()));
    }
    //endregion Snapshot

    private Family family(FamilyMember member) {
        return Family.builder()
                .remoteId(1L)
                .name("George Watson")
                .isActive(true)
                .code("US.GW.20100427")
                .member(member)
                .build();
    }

    private FamilyMember member() {
        return FamilyMember.builder()
                .firstName("George")
                .lastName("Watson")
                .birthdate("2010-04-27")
                .phoneNumber("93089543423")
                .countryOfBirth("US")
                .gender("M")
                .identificationType("PASSPORT")
                .identificationNumber("29384583")
                .build();
    }

    private Survey survey() {
        List<BackgroundQuestion> personalQuestions = new ArrayList<>();
        personalQuestions.add(new BackgroundQuestion("firstName",
                "Ingrese su fecha de nacimiento.", true, STRING, PERSONAL));
        List<BackgroundQuestion> economicQuestions = new ArrayList<>();
        Map<String, String> activityMainOptions = new HashMap<>();
        activityMainOptions.put("Agricultura, Silvicultura y Pesca", "AGRICULTURE");
        activityMainOptions.put("Minas y Canteras", "MINING-QUARRYING");
        economicQuestions.add(new BackgroundQuestion("activityMain",
                "Ingrese su actividad principal.", false, STRING, ECONOMIC,
                activityMainOptions));
        List<IndicatorQuestion> indicatorQuestions = new ArrayList<>();
        List<IndicatorOption> incomeOptions = new ArrayList<>();
        incomeOptions.add(indicatorOption("1-3.jpg", "la línea de la pobreza", Green));
        incomeOptions.add(indicatorOption("1-2.jpg", "la línea de pobreza extrema", Yellow));
        incomeOptions.add(indicatorOption("1-1.jpg", "inferiores a la línea", Red));
        Indicator incomeIndicator = new Indicator(
                "income", "Cuál es su nivel de ingreso?", incomeOptions);
        indicatorQuestions.add(new IndicatorQuestion(incomeIndicator, true));
        return new Survey(1, 1L, "Paraguay Survey", "A super cool survey.",
                personalQuestions, economicQuestions, indicatorQuestions);
    }

    private Snapshot snapshot() {
        Survey survey = survey();
        Family family = family(member());

        Map<BackgroundQuestion, String> personalResponses = new HashMap<>();
        personalResponses.put(survey.getPersonalQuestions().get(0), family.getMember().getFirstName());
        Map<BackgroundQuestion, String> economicResponses = new HashMap<>();
        economicResponses.put(survey.getEconomicQuestions().get(0), "MINING-QUARRYING");
        Map<IndicatorQuestion, IndicatorOption> indicatorResponses = new HashMap<>();
        IndicatorOption yellowIncomeOption =
                survey.getIndicatorQuestions().get(0).getOptions().get(1);
        indicatorResponses.put(survey.getIndicatorQuestions().get(0), yellowIncomeOption);
        List<LifeMapPriority> priorities = new ArrayList<>();
        priorities.add(new LifeMapPriority(yellowIncomeOption.getIndicator(),
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                date("2018-02-13")));

        return new Snapshot(1, 1L, family.getId(), survey.getId(),
                personalResponses, economicResponses, indicatorResponses, priorities,
                time("2018-02-07T00:51:08"));
    }

    private Date time(String time) {
        return parseDate(time, "yyyy-MM-dd'T'HH:mm:ss");
    }

    private Date date(String date) {
        return parseDate(date, "yyyy-MM-dd");
    }

    private Date parseDate(String date, String format) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
        df.setTimeZone(tz);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private IndicatorOption indicatorOption(String image, String description,
                                            IndicatorOption.Level level)  {
        return new IndicatorOption(description,
                "https://s3.us-east-2.amazonaws.com/fp-psp-images/" + image, level);
    }
}
