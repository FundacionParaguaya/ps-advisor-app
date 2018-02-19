package org.fundacionparaguaya.advisorapp.dependencyinjection;

import android.app.Application;

import org.fundacionparaguaya.advisorapp.activities.DashActivity;
import org.fundacionparaguaya.advisorapp.activities.SurveyActivity;
import org.fundacionparaguaya.advisorapp.fragments.AllFamiliesStackedFrag;
import org.fundacionparaguaya.advisorapp.fragments.FamilyDetailFrag;
import org.fundacionparaguaya.advisorapp.fragments.FamilyIndicatorsListFrag;
import org.fundacionparaguaya.advisorapp.fragments.LifeMapFragment;
import org.fundacionparaguaya.advisorapp.fragments.LoginFragment;
import org.fundacionparaguaya.advisorapp.fragments.PriorityListFrag;
import org.fundacionparaguaya.advisorapp.fragments.SettingsStackedFrag;
import org.fundacionparaguaya.advisorapp.fragments.SurveyEconomicQuestionsFragment;
import org.fundacionparaguaya.advisorapp.fragments.SurveyIndicatorsFragment;
import org.fundacionparaguaya.advisorapp.fragments.SurveyIntroFragment;
import org.fundacionparaguaya.advisorapp.fragments.SurveyNewFamilyFrag;
import org.fundacionparaguaya.advisorapp.fragments.SurveySummaryFragment;
import org.fundacionparaguaya.advisorapp.fragments.SurveySummaryIndicatorsFragment;
import org.fundacionparaguaya.advisorapp.jobs.JobCreator;

import javax.inject.Singleton;

import dagger.Component;

/**
 * The main application component.
 */

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class})
public interface ApplicationComponent {

    Application application();

    void inject(LoginFragment loginFragment);

    void inject(DashActivity dashActivity);
    void inject(AllFamiliesStackedFrag allFamiliesFragment);
    void inject(FamilyDetailFrag familyDetailFrag);
    void inject(FamilyIndicatorsListFrag familIndicatorsFrag);

    void inject(SurveyActivity surveyActivity);
    void inject(SurveyIntroFragment surveyIntroFragment);

    void inject(SurveySummaryFragment surveySummaryFragment);
    void inject(SurveyIndicatorsFragment surveyIndicatorsFragment);

    void inject(SurveyNewFamilyFrag frag);

    void inject(SurveyEconomicQuestionsFragment frag);

    void inject(SurveySummaryIndicatorsFragment surveySummaryIndicatorsFragment);

    void inject(JobCreator jobCreator);

    void inject(LifeMapFragment lifeMapFragment);

    void inject(PriorityListFrag lifeMapFragment);

    void inject(SettingsStackedFrag settingsFragment);
}
