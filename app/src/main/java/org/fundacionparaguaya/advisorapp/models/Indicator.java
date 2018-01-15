package org.fundacionparaguaya.advisorapp.models;

import java.util.List;

/**
 * An Indicator is asked during a survey. Each indicator has a red, yellow, and green level. When the family takes the
 * survey, they will choose one of those levels.
 */

public class Indicator
{
    private String name;
    private String dimension;

    private IndicatorOption redLevel;
    private IndicatorOption yellowLevel;
    private IndicatorOption greenLevel;

    public void setRedLevel(IndicatorOption redLevel)
    {
        this.redLevel = redLevel;
    }

    public void setYellowLevel(IndicatorOption yellowLevel)
    {
        this.yellowLevel = yellowLevel;
    }

    public void setGreenLevel(IndicatorOption greenLevel)
    {
        this.greenLevel = greenLevel;
    }

    public IndicatorOption.Level getLevelForOption(IndicatorOption option)
    {
        if(redLevel.equals(option))
        {
            return IndicatorOption.Level.Red;
        }
        else if(yellowLevel.equals(option))
        {
            return IndicatorOption.Level.Yellow;
        }
        else if(greenLevel.equals(option))
        {
            return IndicatorOption.Level.Green;
        }
        else
        {
            return IndicatorOption.Level.None;
        }
    }
}
