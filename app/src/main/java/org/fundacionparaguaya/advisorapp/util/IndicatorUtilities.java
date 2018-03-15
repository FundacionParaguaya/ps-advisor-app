package org.fundacionparaguaya.advisorapp.util;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.Indicator;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;

import java.util.*;

public class IndicatorUtilities {

    /**
     * Returns the responses from the map in a new array, sorted by color (from Red to Green)
     * @param responses map of questions to responses
     * @return a sorted list of responses
     */
    public static List<IndicatorOption> getResponsesAscending(Collection<IndicatorOption> responses) {

        List<IndicatorOption> responseList = new ArrayList<>(responses);
        Collections.sort(responseList);

        return responseList;
    }

    /**
     *
     * @return index in priority list if indicator is a priority, -1 otherwise
     */
    public static int isPriority(Indicator indicator, List<LifeMapPriority> priorityList)
    {
        int index = -1;

        if(priorityList != null) {
            for (int i = 0; i < priorityList.size(); i++) {
                if (priorityList.get(i).getIndicator().equals(indicator)) {
                    index =i;
                    break;
                }
            }
        }

        return index;
    }

    /**
     * Retrieves the response for the given indicator out of the given responses map
     *
     * Deals with a slight bug in the model... Responses are stored as a map of indicatorQuestions to IndicatorOptions.
     * But, Priorities only have a reference to an Indicator
     *
     * @param i
     * @param responses
     * @return response for given indicator
     */
    public static IndicatorOption getResponseForIndicator(Indicator i, Map<IndicatorQuestion, IndicatorOption> responses)
    {
        for(Map.Entry<IndicatorQuestion, IndicatorOption> entry: responses.entrySet())
        {
            if(entry.getKey().getIndicator().equals(i))
            {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * Sets a view to the color of the indicator option
     */
    public static void setViewColorFromLevel(IndicatorOption.Level level, View v) {
        int color;

        switch (level) {
            case Red:
                color = R.color.indicator_red;
                break;

            case Yellow:
                color = R.color.indicator_yellow;
                break;

            case Green:
                color = R.color.indicator_green;
                break;

            default:
                color = -1;
                break;
        }

        if (color != -1) {
            ViewCompat.setBackgroundTintList(v, ContextCompat.getColorStateList(v.getContext(), color));
        }
    }
    /**
     * Sets a view to the color of the indicator option
     */
    public static void setViewColorFromResponse(IndicatorOption option, View v) {
       setViewColorFromLevel(option.getLevel(), v);
    }
}
