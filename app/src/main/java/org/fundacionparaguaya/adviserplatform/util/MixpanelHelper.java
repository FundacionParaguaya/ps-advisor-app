package org.fundacionparaguaya.adviserplatform.util;

import android.app.Activity;
import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.fundacionparaguaya.assistantadvisor.BuildConfig;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MixpanelHelper {

    Context c;
    private static String SYNC_ERROR = "Sync Error";

    public MixpanelHelper(Context c)
    {
        this.c = c;
    }

    public void syncError(String message)
    {
        JSONObject props = new JSONObject();
        try {
            props.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        track(c, SYNC_ERROR, props);
    }

    private static void addOrientationToProps(Context c, JSONObject props)
    {
        String orientation;

        if(ScreenUtils.isLandscape(c)) orientation = "Landscape";
        else orientation = "Portrait";

        try {
            props.put("orientation", orientation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

            JSONObject props = new JSONObject();
            track(c, event_name, props);
        }
    }

    private static void track(Context c, String event_name, JSONObject props)
    {
        if(isAnalyticsEnabled()) {
            addOrientationToProps(c, props);
            getMixpanel(c).track(event_name, props);
        }
    }

    private static void startTimedEvent(Context c, String event_name)
    {
        if(isAnalyticsEnabled()) {
            getMixpanel(c).timeEvent(event_name);
        }
    }

    public static class SyncEvents
    {
        private static final String SYNC_BUTTON_PRESSED = "Sync Button Pressed";
        private static final String SYNC_PERFORMED = "Sync Performed";

        public static void syncButtonPressed(Context c)
        {
            track(c, SYNC_BUTTON_PRESSED);
        }

        public static void syncStarted(Context c)
        {
            startTimedEvent(c, SYNC_PERFORMED);
        }

        public static void syncEnded(Context c, boolean success) {
            JSONObject props = new JSONObject();

            try {
                props.put("success?", success);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            track(c, SYNC_PERFORMED, props);
        }
    }
    public static class PriorityEvents
    {
        private static String EDIT_PRIORITY = "Edit Priority";

        public static void changeSelectedPriority(Context c)
        {
            JSONObject props = new JSONObject();
            String orientation;

            track(c, "Selected Family Priority");
        }

        public static void startEditPriority(Context c)
        {
            startTimedEvent(c, EDIT_PRIORITY);
        }

        public static void finishEditPriority(Context c, int result, boolean isNew)
        {
            String resultDescription = "";
            JSONObject props = new JSONObject();

            if(result == Activity.RESULT_OK && isNew)
            {
                resultDescription = "Added priority";
            }
            else if(result == Activity.RESULT_OK)
            {
                resultDescription = "Priority Edited";
            }
            else if(result== Activity.RESULT_CANCELED)
            {
                resultDescription = "Cancelled";
            }

            try {
                props.put("result", resultDescription);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            track(c, EDIT_PRIORITY, props);
        }
    }

    public static class SurveyEvents
    {
        private static String personalEvent = "state = personal_questions";
        private static String economicEvent = "state = economic_questions";
        private static String indicatorsEvent = "state = indicators";
        private static String lifemapEvent = "state = lifemap";
        private static String skippedIndicatorReviewed = "Returned To Skipped Indicator";
        private static String resurvey = "Start Resurvey";
        private static String newFamily = "Survey New Family";
        private static String takeSurvey ="take_survey";
        private static String surveyStepper ="Survey Stepper Used";
        private static String surveyResumed = "Survey Resumed";

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

        public static void returnedToSkippedIndicator(Context c)
        {
            track(c, skippedIndicatorReviewed);
        }

        public static void startResurvey(Context c) {
            track(c, resurvey);
            startTimedEvent(c, takeSurvey);
        }

        public static void surveyResumed(Context c, Date createdAt) {
            JSONObject props = new JSONObject();

            try {
                props.put("Snapshot Created: ", createdAt.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            track(c, surveyResumed, props);
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
        public static void success(Context c, String username, String host) {
            track(c, LoginEvent.class.getSimpleName(), buildSuccessProps(username, host));
        }

        public static void validationError(Context c) {
            track(c, LoginEvent.class.getSimpleName(), buildLoginProps("validation_error"));
        }

        public static void unauthenticatedFail(Context c) {
            track(c, LoginEvent.class.getSimpleName(), buildLoginProps("unauthenticated_fail"));
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

        private static JSONObject buildSuccessProps(String username, String host) {
            JSONObject props = new JSONObject();

            try {
                props.put("result", "success");
                props.put("username", username);
                props.put("host", host);
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