package org.fundacionparaguaya.advisorapp.models;

/**
 * An economic question is asked during a survey, before the indicator questions
 */

public class EconomicQuestion
{
    enum QuestionType {Age, Date, Number, Text, Currency}

    private String mQuestion;
    private QuestionType mExpectedType;
}
