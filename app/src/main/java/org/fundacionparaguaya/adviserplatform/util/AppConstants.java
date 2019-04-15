package org.fundacionparaguaya.adviserplatform.util;

import org.fundacionparaguaya.assistantadvisor.BuildConfig;

public class AppConstants {
    public static final String FIRST_TIME_USER_KEY = "FIRST_TIME_USER_KEY";
    public static final String KEY_LAST_SYNC_TIME = "lastSyncTime";
    public static final int HTTP_SC_UNAUTHORIZED = 401;
    public static final int HTTP_SC_BAD_REQUEST = 400;
    public static final int TOTAL_TYPES_OF_INDICATORS = 3;
    public static final String SHARED_PREFS_NAME = "advisor_app";
    public static final String KEY_REFRESH_TOKEN = "refreshToken";
    public static final String KEY_USERNAME = "username";
    public static final String AUTH_KEY = "Basic " + BuildConfig.POVERTY_STOPLIGHT_API_KEY_STRING;
    public static final String KEY_TOKEN_EXPIRATION = "KEY_TOKEN_EXPIRATION";
    public static final String ORGANIZATION_ID = "ORGANIZATION_ID";
    public static final int HD_RESOLUTION_HEIGHT = 1280;
    public static final float INDICATOR_TABLET_TEXT_SIZE = 20f;
    public static final String BUCKET_FPPSP = "https://s3.us-east-2.amazonaws.com/fp-psp-";
    public static final String BUCKET_ENDPOINT_1 = "http://fp-psp-images.s3-website.us-east-2.amazonaws.com/";
    public static final String BUCKET_ENDPOINT_2 = "http://py.org.fundacionparaguaya.psp.images.s3-website.eu-west-2.amazonaws.com/";
    public static final String RESIZE_IMAGE_SIZE = "200x200";
    public static final String USER_ROLE = "ROLE_SURVEY_USER";
    public static final String EMPTY_URL = "NONE";
    public static final int MAXIMUM_CAPACITY = 1000;
    public static final int MEDIUM_CAPACITY = (int) (MAXIMUM_CAPACITY * 0.5);
    public static final int HIGH_CAPACITY = (int) (MAXIMUM_CAPACITY * 0.8);

    private AppConstants() {
        //Utilities classes don't have public constructors
    }
}
