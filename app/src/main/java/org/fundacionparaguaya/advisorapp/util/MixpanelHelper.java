package org.fundacionparaguaya.advisorapp.util;

import android.content.Context;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import org.fundacionparaguaya.advisorapp.BuildConfig;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MixpanelHelper {
    private static String API_KEY = BuildConfig.MIXPANEL_API_KEY_STRING;

    private static MixpanelAPI getMixpanel(Context c)
    {
        return MixpanelAPI.getInstance(c, API_KEY);
    }
    public static class SurveyEvent
    {
        public static void startResurvey(Context c) {
            getMixpanel(c).timeEvent(SurveyEvent.class.getSimpleName());
        }

        public static void startSurvey(Context c) {
            getMixpanel(c).timeEvent(SurveyEvent.class.getSimpleName());
        }

        public static void quitSurvey(Context c, String result, int skippedQuestions)
        {
            getMixpanel(c).track(SurveyEvent.class.getSimpleName(), buildSurveyProps(result, skippedQuestions));
        }

        public static void finishSurvey(Context c, String result, int skippedQuestions)
        {
            getMixpanel(c).track(SurveyEvent.class.getSimpleName(), buildSurveyProps(result, skippedQuestions));
        }

        private static JSONObject buildSurveyProps(String result, int skipped) {
            JSONObject props = new JSONObject();
            try {
                props.put("numSkipped", Integer.toString(skipped));
                props.put("result", result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return props;
        }
    }

    public static class LoginEvent {
        public static void success(Context c) {
            getMixpanel(c).track(LoginEvent.class.getSimpleName(), buildLoginProps("success"));
        }

        public static void validationError(Context c) {
            getMixpanel(c).track(LoginEvent.class.getSimpleName(), buildLoginProps("validation_error"));
        }

        public static void unauthenticatedFail(Context c) {
            getMixpanel(c).track(LoginEvent.class.getSimpleName(), buildLoginProps("unauthenticated_fail"));
        }

        public static void unknownFail(Context c) {
            getMixpanel(c).track(LoginEvent.class.getSimpleName(), buildLoginProps("unknown_fail"));
        }

        private static JSONObject buildLoginProps(String result) {
            JSONObject props = new JSONObject();
            try {
                props.put("result", result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return props;
        }
    }

    public static class LogoutEvent {
        public static void logout(Context c){
            getMixpanel(c).track(LogoutEvent.class.getSimpleName());
        }
    }

    public static class SnapshotChanged {
        public static void changeSnap(Context c){
            getMixpanel(c).track(SnapshotChanged.class.getSimpleName());
        }
    }

    public static class FamilyOpened {
        public static void openFamily(Context c){
            getMixpanel(c).track(FamilyOpened.class.getSimpleName());
        }
    }

    public static class SurveyQuestionsFinished {
        public static void surveyFinished(Context c){
            getMixpanel(c).track(SurveyQuestionsFinished.class.getSimpleName());
        }
    }

    public static class PrioritiesEvent {
        public static void prioritiesSet(Context c){
            getMixpanel(c).track(PrioritiesEvent.class.getSimpleName());
        }
    }

    public static void updateLastLogin(Context c, DateTime lastLogin)
    {
        getMixpanel(c).getPeople().set("last_login", lastLogin.toString());
    }

    public static void identify(Context c)
    {
        getMixpanel(c).getPeople().identify(getMixpanel(c).getDistinctId());
    }

    public static class ReviewingSnapshotEvent {
        public static void snapshotReviewed(Context c){
            getMixpanel(c).track(ReviewingSnapshotEvent.class.getSimpleName());
        }
    }

}