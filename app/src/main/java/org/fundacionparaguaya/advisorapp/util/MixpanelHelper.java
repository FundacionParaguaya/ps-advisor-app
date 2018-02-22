package org.fundacionparaguaya.advisorapp.util;

import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.fundacionparaguaya.advisorapp.BuildConfig;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

public class MixpanelHelper {
    private static String API_KEY = BuildConfig.MIXPANEL_API_KEY_STRING;

    private static MixpanelAPI getMixpanel(Context c)
    {
        return MixpanelAPI.getInstance(c, API_KEY);
    }

    public static class PriorityEvents
    {
        public static void priorityAdded(Context c)
        {
            getMixpanel(c).track("priority_added");
        }

        public static void priorityChanged(Context c) {
            getMixpanel(c).track("priority_changed");
        }
    }

    public static class SurveyEvents
    {
        private static String personalEvent = "personal_questions";
        private static String economicEvent = "economic_questions";
        private static String indicatorsEvent = "indicators";
        private static String skippedIndicatorReviewed = "skipped_indicator_reviewed";

        public static void startEconomicQuestions(Context c)
        {
            getMixpanel(c).timeEvent(economicEvent);
        }

        public static void endEconomicQuestions(Context c)
        {
            getMixpanel(c).track(economicEvent);
        }

        public static void startPersonalQuestions(Context c)
        {
            getMixpanel(c).timeEvent(personalEvent);
        }

        public static void endPersonalQuestions(Context c)
        {
            getMixpanel(c).track(personalEvent);
        }

        public static void startIndicators(Context c)
        {
            getMixpanel(c).timeEvent(indicatorsEvent);
        }

        public static void endIndicators(Context c)
        {
            getMixpanel(c).track(indicatorsEvent);
        }

        public static void skippedIndicatorReviewed(Context c)
        {
            getMixpanel(c).track(skippedIndicatorReviewed);
        }

        public static void startResurvey(Context c) {
            getMixpanel(c).timeEvent(SurveyEvents.class.getSimpleName());
        }

        public static void startSurvey(Context c) {
            getMixpanel(c).timeEvent(SurveyEvents.class.getSimpleName());
        }

        public static void quitSurvey(Context c, boolean isResurvey)
        {
            getMixpanel(c).track(SurveyEvents.class.getSimpleName(), buildFinishSurveyProps(isResurvey, "quit"));
        }

        public static void finishSurvey(Context c, boolean isResurvey)
        {
            getMixpanel(c).track(SurveyEvents.class.getSimpleName(), buildFinishSurveyProps(isResurvey,"finished"));
        }

        private static JSONObject buildFinishSurveyProps(boolean isResurvey, String result) {
            JSONObject props = new JSONObject();
            try {
                if(isResurvey) props.put("type", "resurvey");
                else props.put("type", "new_family");

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