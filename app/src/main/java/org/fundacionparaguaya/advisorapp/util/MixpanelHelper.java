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

    public static boolean isAnalyticsEnabled()
    {
        return !BuildConfig.DEBUG;
    }

    private static void track(Context c, String event_name)
    {
        if(isAnalyticsEnabled()) {
            getMixpanel(c).track(event_name);
        }
    }

    private static void track(Context c, String event_name, JSONObject props)
    {
        if(isAnalyticsEnabled()) {
            getMixpanel(c).track(event_name, props);
        }
    }

    private static void startTimedEvent(Context c, String event_name)
    {
        if(isAnalyticsEnabled()) {
            getMixpanel(c).timeEvent(event_name);
        }
    }

    public static class BugEvents
    {
        static String missedCache = "Survey Images Missed Cache";

        public static void imagesMissedCache(Context c, int missed)
        {
            JSONObject props = new JSONObject();

            try {
                props.put("num_missed", missed);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            getMixpanel(c).track(missedCache, props);
        }
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
        private static String personalEvent = "state = personal_questions";
        private static String economicEvent = "state = economic_questions";
        private static String indicatorsEvent = "state = indicators";
        private static String lifemapEvent = "state = lifemap";
        private static String skippedIndicatorReviewed = "skipped_indicator_reviewed";
        private static String resurvey = "Start Resurvey";
        private static String newFamily = "Survey New Family";
        private static String takeSurvey ="take_survey";
        private static String surveyStepper ="Survey Stepper Used";

        public static void surveyStepperUsed(Context c)
        {
            track(c, surveyStepper);
        }

        public static void openEconomicQuestions(Context c)
        {
            track(c, economicEvent);
        }

        public static void openBackgroundQuestions(Context c)
        {
            track(c, personalEvent);
        }

        public static void openIndicators(Context c)
        {
            track(c, indicatorsEvent);
        }

        public static void openLifeMap(Context c)
        {
            track(c, lifemapEvent);
        }

        public static void startResurvey(Context c) {
            track(c, resurvey);
            startTimedEvent(c, takeSurvey);
        }

        public static void newFamily(Context c) {
            track(c, newFamily);
            startTimedEvent(c, takeSurvey);
        }

        public static void quitSurvey(Context c, boolean isResurvey)
        {
            track(c, takeSurvey, buildFinishSurveyProps(isResurvey, "quit"));
        }

        public static void finishSurvey(Context c, boolean isResurvey)
        {
            track(c, takeSurvey, buildFinishSurveyProps(isResurvey, "finished"));
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
            track(c, LoginEvent.class.getSimpleName(), buildLoginProps("success"));
        }

        public static void validationError(Context c) {
            track(c, LoginEvent.class.getSimpleName(), buildLoginProps("validation_error"));
        }

        public static void unauthenticatedFail(Context c) {
            track(c, LoginEvent.class.getSimpleName(), buildLoginProps("unauthenticated_fail"));
        }

        public static void unknownFail(Context c) {
            track(c, LoginEvent.class.getSimpleName(), buildLoginProps("unknown_fail"));
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
            track(c, LogoutEvent.class.getSimpleName());
        }
    }

    public static class FamilyOpened {
        public static void openFamily(Context c){
            track(c, FamilyOpened.class.getSimpleName());
        }
    }

    public static void updateLastLogin(Context c, DateTime lastLogin)
    {
        if(isAnalyticsEnabled()) {
            getMixpanel(c).getPeople().set("last_login", lastLogin.toString());
        }
    }

    public static void identify(Context c)
    {
        if(isAnalyticsEnabled()) {
            getMixpanel(c).getPeople().identify(getMixpanel(c).getDistinctId());
            getMixpanel(c).getPeople().initPushHandling(BuildConfig.GCM_SENDER_ID);
        }
    }

}