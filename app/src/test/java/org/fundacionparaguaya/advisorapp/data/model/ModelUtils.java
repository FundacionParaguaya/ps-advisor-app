package org.fundacionparaguaya.advisorapp.data.model;

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

import static org.fundacionparaguaya.advisorapp.data.model.BackgroundQuestion.QuestionType.ECONOMIC;
import static org.fundacionparaguaya.advisorapp.data.model.BackgroundQuestion.QuestionType.PERSONAL;
import static org.fundacionparaguaya.advisorapp.data.model.IndicatorOption.Level.Green;
import static org.fundacionparaguaya.advisorapp.data.model.IndicatorOption.Level.Red;
import static org.fundacionparaguaya.advisorapp.data.model.IndicatorOption.Level.Yellow;
import static org.fundacionparaguaya.advisorapp.data.model.ResponseType.STRING;

/**
 * Utilities for testing models.
 */

public class ModelUtils {

    public static Family family(FamilyMember member) {
        return Family.builder()
                .id(1)
                .remoteId(1L)
                .name("George Watson")
                .isActive(true)
                .code("US.GW.20100427")
                .member(member)
                .lastModified(time())
                .build();
    }

    public static FamilyMember member() {
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

    public static List<Survey> surveyList()
    {
        ArrayList<Survey> surveys = new ArrayList<>();
        surveys.add(survey());
        surveys.add(survey());
        surveys.add(survey());

        return surveys;
    }

    public static Survey survey() {
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

    public static Snapshot snapshot() {
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

        return new Snapshot(1, 1L, family.getId(), survey.getId(), false,
                personalResponses, economicResponses, indicatorResponses, priorities,
                time());
    }

    public static Map<BackgroundQuestion, String> personalResponses() {
        HashMap<BackgroundQuestion, String> personalResponses = new HashMap<>();
        personalResponses.put(bq("firstName"), member().getFirstName());
        personalResponses.put(bq("lastName"), member().getLastName());
        personalResponses.put(bq("birthdate"), member().getBirthdate());
        personalResponses.put(bq("countryOfBirth"), member().getCountryOfBirth());
        personalResponses.put(bq("identificationType"), member().getIdentificationType());
        personalResponses.put(bq("identificationNumber"), member().getIdentificationNumber());
        personalResponses.put(bq("phoneNumber"), member().getPhoneNumber());
        personalResponses.put(bq("gender"), member().getGender());
        return personalResponses;
    }

    private static BackgroundQuestion bq(String name) {
        return new BackgroundQuestion(name, "", true, STRING, PERSONAL);
    }

    public static Date time() {
        return time("2018-02-07T00:51:08");
    }

    public static Date time(String time) {
        return parseDate(time, "yyyy-MM-dd'T'HH:mm:ss");
    }

    public static Date date(String date) {
        return parseDate(date, "yyyy-MM-dd");
    }

    public static Date parseDate(String date, String format) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
        df.setTimeZone(tz);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static IndicatorOption indicatorOption(String image, String description,
                                            IndicatorOption.Level level)  {
        return new IndicatorOption(description,
                "https://s3.us-east-2.amazonaws.com/fp-psp-images/" + image, level);
    }
}
