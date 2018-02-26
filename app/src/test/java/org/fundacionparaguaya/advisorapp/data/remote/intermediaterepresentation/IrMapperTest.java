package org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation;

import android.test.suitebuilder.annotation.SmallTest;

import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.models.FamilyMember;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.models.Survey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrUtils.familyIr;
import static org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrUtils.priorityIrs;
import static org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrUtils.snapshotIr;
import static org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrUtils.surveyIr;
import static org.fundacionparaguaya.advisorapp.models.ModelUtils.family;
import static org.fundacionparaguaya.advisorapp.models.ModelUtils.member;
import static org.fundacionparaguaya.advisorapp.models.ModelUtils.snapshot;
import static org.fundacionparaguaya.advisorapp.models.ModelUtils.survey;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * A test for the functionality of the IrMapper.
 */

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class IrMapperTest {

    //region Family
    @Test
    public void family_ShouldMapFromIr() {
        FamilyIr ir = familyIr();

        Family family = IrMapper.mapFamily(ir);
        family.setId(1);

        assertThat(family, is(family(member())));
    }

    @Test
    public void family_ShouldMapFromIr_nullMembers() {
        FamilyIr ir = mock(FamilyIr.class);
        Family family = IrMapper.mapFamily(ir);

        assertThat(family, is(notNullValue()));
    }

    @Test
    public void member_ShouldMapFromIr_nullMembers() {
        FamilyMemberIr ir = mock(FamilyMemberIr.class);
        FamilyIr familyIr = familyIr(ir);
        FamilyMember member = IrMapper.mapFamily(familyIr).getMember();

        assertThat(member, is(notNullValue()));
    }
    //endregion Family

    //region Survey
    @Test
    public void survey_ShouldMapFromIr() {
        SurveyIr ir = surveyIr();

        Survey survey = IrMapper.mapSurvey(ir);
        survey.setId(1);

        assertThat(survey, is(survey()));
    }

    @Test
    public void survey_ShouldMapFromIr_nullMembers() {
        SurveyIr ir = mock(SurveyIr.class);

        Survey survey = IrMapper.mapSurvey(ir);

        assertThat(survey, is(notNullValue()));
    }
    //endregion Survey

    //region Snapshot
    @Test
    public void snapshot_ShouldMapFromIr() {
        SnapshotIr ir = snapshotIr();
        List<PriorityIr> priorityIrs = priorityIrs();

        priorityIrs.get(0).estimatedDate = "2018-02-13";
        Snapshot snapshot = IrMapper.mapSnapshot(ir, priorityIrs, family(member()), survey());
        snapshot.setId(1);

        assertThat(snapshot, is(snapshot()));
    }

    @Test
    public void snapshot_ShouldMapFromIr_nullMembers() {
        SnapshotIr ir = mock(SnapshotIr.class);
        List<PriorityIr> priorityIrs = new ArrayList<>();
        priorityIrs.add(mock(PriorityIr.class));
        Family family = mock(Family.class);
        Survey survey = mock(Survey.class);

        Snapshot snapshot = IrMapper.mapSnapshot(ir, priorityIrs, family, survey);

        assertThat(snapshot, is(notNullValue()));
    }

    @Test
    public void snapshot_ShouldMapToIr() {
        Snapshot snapshot = snapshot();
        Survey survey = survey();

        SnapshotIr ir = IrMapper.mapSnapshot(snapshot, survey);
        List<PriorityIr> priorityIrs = IrMapper.mapPriorities(snapshot);
        priorityIrs.get(0).id = 1; // remote id not stored locally

        assertThat(ir, is(snapshotIr()));
        assertThat(priorityIrs, is(priorityIrs()));
    }
    //endregion Snapshot
}
