package org.fundacionparaguaya.advisorapp.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion
import org.fundacionparaguaya.advisorapp.models.Survey
import org.fundacionparaguaya.advisorapp.repositories.SurveyRepository

import java.util.Calendar
import java.util.Date


/**
 * View model used for the page that creates new/edits existing priorities.
 */

class EditPriorityViewModel(repository: SurveyRepository) : ViewModel() {

    private var repo: SurveyRepository? = null
    private var mSurvey: Survey? = null

    private var surveyId:Int? = null
    private var indicatorIndex:Int? = null

    var reason: String? = null
    var action: String? = null
    var completionDate: Date? = null


    init {
        repo = repository
    }

    fun setIndicator(surveyId: Int, indicatorIndex: Int)
    {
        this.surveyId = surveyId
        this.indicatorIndex = indicatorIndex
    }
    fun getIndicator(): LiveData<IndicatorQuestion> {

        surveyId?.let {
            indicatorIndex?.let {
                return Transformations.map(repo!!.getSurvey(surveyId!!)) { value -> value.indicatorQuestions[indicatorIndex!!] }
            }
        }

        throw Exception("getIndicator called before surveyId and IndicatorIndex set")
    }

    fun setNumMonths(i: Int) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, i)

        completionDate = cal.time
    }
}
