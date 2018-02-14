package org.fundacionparaguaya.advisorapp.data.local;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Some converter utilities for serializing objects that need to be stored in the local database.
 */

public class Converters {
    private static Gson gson() {
        return new GsonBuilder().enableComplexMapKeySerialization().create();
    }

    @TypeConverter
    public static String fromBackgroundQuestions(List<BackgroundQuestion> questions) {
        return gson().toJson(questions);
    }

    @TypeConverter
    public static List<BackgroundQuestion> toBackgroundQuestions(String value) {
        Type listType = new TypeToken<ArrayList<BackgroundQuestion>>() {}.getType();
        return gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromIndicatorQuestions(List<IndicatorQuestion> questions) {
        return gson().toJson(questions);
    }

    @TypeConverter
    public static List<IndicatorQuestion> toIndicatorQuestions(String value) {
        Type listType = new TypeToken<ArrayList<IndicatorQuestion>>() {}.getType();
        return gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromBackgroundResponses(Map<BackgroundQuestion, String> responses) {
        return gson().toJson(responses);
    }

    @TypeConverter
    public static Map<BackgroundQuestion, String> toBackgroundResponses(String value) {
        Type listType = new TypeToken<Map<BackgroundQuestion, String>>() {}.getType();
        return gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromIndicatorResponse(Map<IndicatorQuestion, IndicatorOption> responses) {
        return gson().toJson(responses);
    }

    @TypeConverter
    public static Map<IndicatorQuestion, IndicatorOption> toIndicatorResponse(String value) {
        Type listType = new TypeToken<Map<IndicatorQuestion, IndicatorOption>>() {}.getType();
        return gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromPriorities(List<LifeMapPriority> priorities) {
        return gson().toJson(priorities);
    }

    @TypeConverter
    public static List<LifeMapPriority> toPriorities(String value) {
        Type listType = new TypeToken<List<LifeMapPriority>>() {}.getType();
        return gson().fromJson(value, listType);
    }

    @TypeConverter
    public Long fromDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    @TypeConverter
    public Date toDate(Long value) {
        if (value == null) {
            return null;
        }
        return new Date(value);
    }
}
