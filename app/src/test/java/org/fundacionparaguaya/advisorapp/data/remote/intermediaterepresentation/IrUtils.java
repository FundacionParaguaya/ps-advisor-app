package org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Testing utilities for IR classes.
 */

public class IrUtils {

    public static LoginIr loginSuccess() {
        LoginIr ir = new LoginIr();
        ir.tokenType = accessTokenType();
        ir.accessToken = accessToken();
        ir.expiresIn = 10580;
        ir.refreshToken = "d87e6156-b5fc-49b8-9b1c-45c4e3b48607";
        return ir;
    }

    public static String accessTokenType() {
        return "bearer";
    }

    public static String accessToken() {
        return "9cd7d634-fd3f-4b60-b96a-994733d72a19";
    }

    public static FamilyIr familyIr() {
        return familyIr(memberIr());
    }

    public static FamilyIr familyIr(FamilyMemberIr memberIr) {
        FamilyIr ir = new FamilyIr();
        ir.id = 1;
        ir.name = "George Watson";
        ir.active = true;
        ir.code = "US.GW.20100427";
        ir.member = memberIr();
        return ir;
    }

    public static FamilyMemberIr memberIr() {
        FamilyMemberIr ir = new FamilyMemberIr();
        ir.id = 1;
        ir.firstName = "George";
        ir.lastName = "Watson";
        ir.birthdate = "2010-04-27";
        ir.phoneNumber = "93089543423";
        ir.countryOfBirth = new HashMap<>();
        ir.countryOfBirth.put("alfa2Code", "US");
        ir.gender = "M";
        ir.identificationType = "PASSPORT";
        ir.identificationNumber = "29384583";
        return ir;
    }

    public static SurveyIr surveyIr() {
        SurveyIr ir = new SurveyIr();
        ir.id = 1;
        ir.title = "Paraguay Survey";
        ir.description = "A super cool survey.";
        ir.schema = surveySchemaIr();
        ir.uiSchema = surveyUiSchemaIr();
        ir.createdAt = "2018-01-20";
        ir.lastModifiedAt = "2018-02-21";
        return ir;
    }

    public static SurveySchemaIr surveySchemaIr() {
        SurveySchemaIr ir = new SurveySchemaIr();
        ir.title = "Really, the Paraguay Survey";
        ir.description = "A suuuper cool survey.";
        ir.requiredQuestions = new ArrayList<>();
        ir.requiredQuestions.add("firstName");
        ir.requiredQuestions.add("income");
        ir.questions = new HashMap<>();
        ir.questions.put("firstName", firstNameQuestionIr());
        ir.questions.put("income", incomeQuestionIr());
        ir.questions.put("activityMain", activityMainQuestionIr());
        return ir;
    }

    public static SurveyQuestionIr firstNameQuestionIr() {
        SurveyQuestionIr ir = new SurveyQuestionIr();
        ir.type = "string";
        ir.title = new HashMap<>();
        ir.title.put("es", "Ingrese su fecha de nacimiento.");
        return ir;
    }

    public static SurveyQuestionIr incomeQuestionIr() {
        SurveyQuestionIr ir = new SurveyQuestionIr();
        ir.type = "array";
        ir.title = new HashMap<>();
        ir.title.put("es", "Cuál es su nivel de ingreso?");
        ir.indicatorOptions = new IndicatorOptionsIr();
        ir.indicatorOptions.type = "object";
        ir.indicatorOptions.values = new ArrayList<>();
        ir.indicatorOptions.values.add(indicatorOptionIr("1-3.jpg", "GREEN",
                "la línea de la pobreza"));
        ir.indicatorOptions.values.add(indicatorOptionIr("1-2.jpg", "YELLOW",
                "la línea de pobreza extrema"));
        ir.indicatorOptions.values.add(indicatorOptionIr("1-1.jpg", "RED",
                "inferiores a la línea"));
        return ir;
    }

    public static SurveyQuestionIr activityMainQuestionIr() {
        SurveyQuestionIr ir = new SurveyQuestionIr();
        ir.type = "string";
        ir.title = new HashMap<>();
        ir.title.put("es", "Ingrese su actividad principal.");
        ir.options = new ArrayList<>();
        ir.options.add("AGRICULTURE");
        ir.options.add("MINING-QUARRYING");
        ir.optionNames = new ArrayList<>();
        ir.optionNames.add("Agricultura, Silvicultura y Pesca");
        ir.optionNames.add("Minas y Canteras");
        return ir;
    }

    public static SurveyUiSchemaIr surveyUiSchemaIr() {
        SurveyUiSchemaIr ir = new SurveyUiSchemaIr();
        ir.order = new ArrayList<>();
        ir.order.add("firstName");
        ir.order.add("activityMain");
        ir.order.add("income");
        ir.personalQuestions = new ArrayList<>();
        ir.personalQuestions.add("firstName");
        ir.economicQuestions = new ArrayList<>();
        ir.economicQuestions.add("activityMain");
        ir.indicatorQuestions = new ArrayList<>();
        ir.indicatorQuestions.add("income");
        ir.customFields = new HashMap<>();
        return ir;
    }

    private static IndicatorOptionIr indicatorOptionIr(String image, String value, String description) {
        IndicatorOptionIr ir = new IndicatorOptionIr();
        ir.url = "https://s3.us-east-2.amazonaws.com/fp-psp-images/" + image;
        ir.value = value;
        ir.description = description;
        return ir;
    }

    public static SnapshotIr snapshotIr() {
        SnapshotIr ir = new SnapshotIr();
        ir.id = 1;
        ir.surveyId = 1;
        ir.personalResponses = new HashMap<>();
        ir.personalResponses.put("firstName", memberIr().firstName);
        ir.economicResponses = new HashMap<>();
        ir.economicResponses.put("activityMain", "MINING-QUARRYING");
        ir.indicatorResponses = new HashMap<>();
        ir.indicatorResponses.put("income", "YELLOW");
        ir.createdAt = "2018-02-07T00:51:08";
        return ir;
    }

    public static List<PriorityIr> priorityIrs() {
        List<PriorityIr> irs = new ArrayList<>();
        PriorityIr ir = new PriorityIr();
        ir.id = 1;
        ir.snapshotId = 1;
        ir.indicatorTitle = "Income";
        ir.reason = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
        ir.action = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
        ir.estimatedDate = "13/02/2018";
        irs.add(ir);
        return irs;
    }
}
