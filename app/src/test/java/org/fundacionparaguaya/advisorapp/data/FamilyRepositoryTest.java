package org.fundacionparaguaya.advisorapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import org.fundacionparaguaya.advisorapp.data.local.FamilyDao;
import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.models.FamilyMember;
import org.fundacionparaguaya.advisorapp.rapositories.FamilyRepository;
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
import static org.mockito.Mockito.never;
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
        member = new FamilyMember("Joe", "Smith", "");
        family = new Family(1, member);

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

        when(familyDao.updateFamily(family)).thenReturn(0);
        repo.saveFamily(family);

        verify(familyDao, times(1)).insertFamily(family);
    }

    @Test
    public void ShouldBeAbleToChangeFamily() {

        when(familyDao.updateFamily(family)).thenReturn(1);
        repo.saveFamily(family);

        verify(familyDao, atLeastOnce()).updateFamily(family);
        verify(familyDao, never()).insertFamily(any(Family.class));
    }

    @Test
    public void ShouldBeAbleToDeleteFamily() {

        repo.deleteFamily(family);

        verify(familyDao, atLeastOnce()).deleteFamily(family);
    }
    //endregion

//    //region Family Members
//    @Test
//    public void ShouldBeAbleToGetFamilyMembers() {
//        LiveData<List<FamilyMember>> members = new MutableLiveData<>();
//        when(memberDao.queryFamilyMembers(1)).thenReturn(members);
//
//        assertEquals(members, repo.getMembersOfFamily(family));
//
//        verify(memberDao, atLeastOnce()).queryFamilyMembers(1);
//    }
//
//    @Test
//    public void ShouldBeAbleToGetFamilyMember() {
//        LiveData<FamilyMember> member = new MutableLiveData<>();
//        when(memberDao.queryFamilyMember(1)).thenReturn(member);
//
//        assertEquals(member, repo.getFamilyMember(1));
//
//        verify(memberDao, atLeastOnce()).queryFamilyMember(1);
//    }
//
//    @Test
//    public void ShouldBeAbleToCreateFamilyMember() {
//
//        when(memberDao.updateFamilyMember(member)).thenReturn(0);
//        repo.saveFamilyMember(member);
//
//        verify(memberDao, times(1)).insertFamilyMember(member);
//    }
//
//
//
//    @Test
//    public void ShouldBeAbleToUpdateFamilyMember() {
//
//        when(memberDao.updateFamilyMember(member)).thenReturn(1);
//        repo.saveFamilyMember(member);
//
//        verify(memberDao, atLeastOnce()).updateFamilyMember(member);
//        verify(memberDao, never()).insertFamilyMember(any(FamilyMember.class));
//    }
//
//    @Test
//    public void ShouldBeAbleToDeleteFamilyMember() {
//
//        repo.deleteFamilyMember(member);
//
//        verify(memberDao, atLeastOnce()).deleteFamilyMember(member);
//    }
//    //endregion
}
