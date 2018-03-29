package org.fundacionparaguaya.advisorapp.data.model;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.fundacionparaguaya.advisorapp.data.model.ModelUtils.member;
import static org.fundacionparaguaya.advisorapp.data.model.ModelUtils.personalResponses;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for FamilyMember.
 */

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class FamilyMemberTest {

    @Test
    public void builder_ShouldFillDetails_snapshot() {
        Snapshot snapshot = mock(Snapshot.class);
        when(snapshot.getPersonalResponses()).thenReturn(personalResponses());

        FamilyMember member = FamilyMember.builder()
                .snapshot(snapshot)
                .build();

        assertThat(member, is(member()));
    }
}