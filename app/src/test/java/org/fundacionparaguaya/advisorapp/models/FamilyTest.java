package org.fundacionparaguaya.advisorapp.models;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.fundacionparaguaya.advisorapp.models.ModelUtils.family;
import static org.fundacionparaguaya.advisorapp.models.ModelUtils.member;
import static org.fundacionparaguaya.advisorapp.models.ModelUtils.personalResponses;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for Family.
 */

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class FamilyTest {

    @Test
    public void builder_ShouldFillFamily_snapshot() {
        Snapshot snapshot = mock(Snapshot.class);
        when(snapshot.getPersonalResponses()).thenReturn(personalResponses());

        Family family = Family.builder()
                .snapshot(snapshot)
                .build();

        assertThat(family.getId(), is(0));
        assertThat(family.getRemoteId(), is(nullValue()));
        family.setId(1);
        family.setRemoteId(1L);
        assertThat(family, is(family(member())));
    }
}