package org.fundacionparaguaya.advisorapp.rapositories;

import android.arch.lifecycle.LiveData;

import org.fundacionparaguaya.advisorapp.data.local.FamilyDao;
import org.fundacionparaguaya.advisorapp.models.Family;

import java.util.List;

import javax.inject.Inject;

/**
 * The utility for the storage of families and their members.
 */
public class FamilyRepository {
    private final FamilyDao familyDao;

    @Inject
    public FamilyRepository(FamilyDao familyDao) {
        this.familyDao = familyDao;
    }

    //region Family
    public LiveData<List<Family>> getFamilies() {
        return familyDao.queryFamilies();
    }

    public LiveData<Family> getFamily(int id) {
        return familyDao.queryFamily(id);
    }

    public void saveFamily(Family family) {
        int rowCount = familyDao.updateFamily(family);
        if (rowCount == 0) { // didn't already exist
            familyDao.insertFamily(family);
        }
    }

    public void deleteFamily(Family family) {
        familyDao.deleteFamily(family);
    }
    //endregion

    //region Family Member
//    public LiveData<List<FamilyMember>> getMembersOfFamily(Family family) {
//        return memberDao.queryFamilyMembers(family.getId());
//    }
//
//    public LiveData<FamilyMember> getFamilyMember(int id) {
//        return memberDao.queryFamilyMember(id);
//    }
//
//    public void saveFamilyMember(FamilyMember member) {
//        int rowCount = memberDao.updateFamilyMember(member);
//        if (rowCount == 0) { // didn't already exist
//            memberDao.insertFamilyMember(member);
//        }
//    }
//
//    public void deleteFamilyMember(FamilyMember member) {
//        memberDao.deleteFamilyMember(member);
//    }
    //endregion
}
