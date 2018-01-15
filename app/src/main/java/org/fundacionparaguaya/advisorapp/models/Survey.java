package org.fundacionparaguaya.advisorapp.models;

import java.util.List;

/**
 * A survey has two sections: socioeconomic questions and indicator questions. It should not be mutated after creation.
 */

public class Survey {

    private List<EconomicQuestion> economicQuestions; //questions asked before the indicators are started
    private List<Indicator> indicators; //should be ordered
    private List<Indicator> requiredIndicators; //indicators required for the survey to be completed

    public List<EconomicQuestion> getEconomicQuestions()
    {
        return economicQuestions;
    }

    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    public List<Indicator> getRequiredIndicators()
    {
        return requiredIndicators;
    }
}
