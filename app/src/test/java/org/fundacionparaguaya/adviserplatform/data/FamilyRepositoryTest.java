package org.fundacionparaguaya.adviserplatform.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import org.fundacionparaguaya.adviserplatform.data.local.FamilyDao;
import org.fundacionparaguaya.adviserplatform.data.model.Family;
import org.fundacionparaguaya.adviserplatform.data.model.FamilyMember;
import org.fundacionparaguaya.adviserplatform.data.repositories.FamilyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Tests the functionality of the advisor repository.
 */

@RunWith(MockitoJUnitRunner.class)
public class FamilyRepositoryTest {
    @Mock
    FamilyDao familyDao;

    Family family;
    FamilyMember member;

    @InjectMocks
    FamilyRepository repo;

    @Before
    public void setUp() {
        member = FamilyMember.builder().firstName("Joe").lastName("Smith").build();
        family = Family.builder().remoteId(1L).name("Smith").member(member).build();
    }

    //region Family
    @Test
    public void ShouldBeAbleToGetFamily() {
        LiveData<Family> data = new MutableLiveData<>();
        when(familyDao.queryFamily(1)).thenReturn(data);

        assertEquals(data, repo.getFamily(1));

        verify(familyDao, atLeastOnce()).queryFamily(1);
    }

    @Test
    public void ShouldBeAbleToGetFamilies() {
        LiveData<List<Family>> data = new MutableLiveData<>();
        when(familyDao.queryFamilies()).thenReturn(data);

        assertEquals(data, repo.getFamilies());

        verify(familyDao, atLeastOnce()).queryFamilies();
    }
    @Test
    public void ShouldBeAbleToCreateFamily() {

        repo.saveFamily(family);

        verify(familyDao, times(1)).insertFamily(family);
    }

    @Test
    public void ShouldBeAbleToChangeFamily() {

        repo.saveFamily(family);

        verify(familyDao, times(1)).insertFamily(any(Family.class));
    }
    //endregion
}
