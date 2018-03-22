package org.fundacionparaguaya.advisorapp.dependencyinjection;

import android.app.Application;

import android.content.Context;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.activities.DashActivity;
import org.fundacionparaguaya.advisorapp.activities.EditPriorityActivity;
import org.fundacionparaguaya.advisorapp.activities.SurveyActivity;
import org.fundacionparaguaya.advisorapp.fragments.*;
import org.fundacionparaguaya.advisorapp.jobs.JobCreator;
import org.fundacionparaguaya.advisorapp.repositories.ImageRepository;
import org.fundacionparaguaya.advisorapp.viewcomponents.ResumeSnapshotPopupWindow;

import javax.inject.Singleton;

import dagger.Component;

/**
 * The main application component.
 */

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class})
public interface ApplicationComponent {

    Application application();

    void inject(AdvisorApplication application);
    void inject(LoginFragment loginFragment);

    void inject(DashActivity dashActivity);
    void inject(AllFamiliesStackedFrag allFamiliesFragment);
    void inject(FamilyDetailFrag familyDetailFrag);
    void inject(SurveyActivity surveyActivity);
    void inject(SurveyIntroFragment surveyIntroFragment);

    void inject(SurveyIndicatorsFragment surveyIndicatorsFragment);

    void inject(SurveyNewFamilyFrag frag);

    void inject(SurveyEconomicQuestionsFragment frag);

    void inject(SurveySummaryIndicatorsFragment surveySummaryIndicatorsFragment);

    void inject(JobCreator jobCreator);

    void inject(LifeMapFragment lifeMapFragment);

    void inject(PriorityListFrag lifeMapFragment);

    void inject(SettingsStackedFrag settingsFragment);

    void inject(SurveyChoosePrioritiesFragment fragment);

    void inject(FamilyPriorityDetailFragment familyPriorityDetailFragment);

    void inject(FamilyLifeMapFragment familyLifeMapFragment);

    void inject(FamilySidePrioritiesListFrag familySidePrioritiesListFrag);

    void inject(QuestionFragment.DateQuestionFrag dateQuestionFrag);

    void inject(QuestionFragment.LocationQuestionFrag locationQuestionFrag);

    void inject(QuestionFragment.ReviewPageFragment reviewPageFragment);

    void inject(EditPriorityActivity editPriorityActivity);

    void inject(ResumeSnapshotPopupWindow resumeSnapshotPopupWindow);

    void inject(SurveyIndicatorsSummary surveyIndicatorsSummary);
}
