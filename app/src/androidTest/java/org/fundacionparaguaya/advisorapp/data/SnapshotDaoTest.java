package org.fundacionparaguaya.advisorapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.support.test.runner.AndroidJUnit4;

import org.fundacionparaguaya.advisorapp.data.local.FamilyDao;
import org.fundacionparaguaya.advisorapp.data.local.LocalDatabase;
import org.fundacionparaguaya.advisorapp.data.local.SnapshotDao;
import org.fundacionparaguaya.advisorapp.data.local.SurveyDao;
import org.fundacionparaguaya.advisorapp.data.model.BackgroundQuestion;
import org.fundacionparaguaya.advisorapp.data.model.Family;
import org.fundacionparaguaya.advisorapp.data.model.Indicator;
import org.fundacionparaguaya.advisorapp.data.model.IndicatorOption;
import org.fundacionparaguaya.advisorapp.data.model.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.data.model.Snapshot;
import org.fundacionparaguaya.advisorapp.data.model.Survey;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static junit.framework.Assert.assertEquals;
import static org.fundacionparaguaya.advisorapp.data.LiveDataTestUtil.waitForValue;
import static org.fundacionparaguaya.advisorapp.data.model.BackgroundQuestion.QuestionType.ECONOMIC;
import static org.fundacionparaguaya.advisorapp.data.model.BackgroundQuestion.QuestionType.PERSONAL;
import static org.fundacionparaguaya.advisorapp.data.model.IndicatorOption.Level.Green;
import static org.fundacionparaguaya.advisorapp.data.model.IndicatorOption.Level.Red;
import static org.fundacionparaguaya.advisorapp.data.model.IndicatorOption.Level.Yellow;
import static org.fundacionparaguaya.advisorapp.data.model.ResponseType.STRING;

/**
 * The tests for the survey data access object.
 */

@RunWith(AndroidJUnit4.class)
public class SnapshotDaoTest {
    private LocalDatabase db;
    private SurveyDao surveyDao;
    private SnapshotDao snapshotDao;
    private FamilyDao familyDao;

    @Before
    public void init() {
        db = Room.inMemoryDatabaseBuilder(getTargetContext(), LocalDatabase.class).build();
        surveyDao = db.surveyDao();
        snapshotDao = db.snapshotDao();
        familyDao = db.familyDao();
    }

    @After
    public void close() {
        db.close();
    }

    @Test
    public void ShouldBeAbleToInsertASnapshot() {
        List<IndicatorQuestion> indicatorQuestions = new ArrayList<>();
        List<IndicatorOption> indicatorOptions = new ArrayList<>();
        indicatorOptions.add(new IndicatorOption("Has a stove.", "https://s3.us-east-2.amazonaws.com/fp-psp-images/21-3.jpg", Green));
        indicatorOptions.add(new IndicatorOption("Has no stove.", "https://s3.us-east-2.amazonaws.com/fp-psp-images/21-2.jpg", Yellow));
        indicatorOptions.add(new IndicatorOption("Has no kitchen.", "https://s3.us-east-2.amazonaws.com/fp-psp-images/21-1.jpg", Red));
        Indicator indicator = new Indicator("properKitchen", "Home", indicatorOptions);
        indicatorQuestions.add(new IndicatorQuestion(indicator));

        List<BackgroundQuestion> economicQuestions = new ArrayList<>();
        List<String> economicOptions = new ArrayList<>();
        economicOptions.add("Employed");
        economicOptions.add("Not Employed");
        economicQuestions.add(new BackgroundQuestion(
                "employmentStatus", "Employment status.", STRING, ECONOMIC, economicOptions));

        List<BackgroundQuestion> personalQuestions = new ArrayList<>();
        personalQuestions.add(new BackgroundQuestion("firstName", "First name.", STRING, PERSONAL));

        Survey survey = new Survey(1, 1L, personalQuestions, economicQuestions, indicatorQuestions);

        surveyDao.insertSurvey(survey);

        Family family = Family.builder().id(1).remoteId(1L).name("Smith").build();
        familyDao.insertFamily(family);

        Snapshot snapshot = new Snapshot(family, survey);
        snapshot.setId(1);
        snapshot.response(survey.getPersonalQuestions().get(0), "Joe");
        snapshot.response(survey.getEconomicQuestions().get(0), survey.getEconomicQuestions().get(0).getOptions().get(0));
        snapshot.response(survey.getIndicatorQuestions().get(0), survey.getIndicatorQuestions().get(0).getOptions().get(1));

        snapshotDao.insertSnapshot(snapshot);

        LiveData<List<Snapshot>> result = snapshotDao.querySnapshotsForFamily(family.getId());
        List<Snapshot> value = waitForValue(result);
        assertEquals(1, value.size());
        assertEquals(snapshot, value.get(0));
    }
}
