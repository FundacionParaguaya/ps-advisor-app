package org.fundacionparaguaya.advisorapp.data;

import android.arch.lifecycle.LiveData;

import org.fundacionparaguaya.advisorapp.models.Family;

import java.util.List;

import javax.inject.Inject;

/**
 * The repository of information for the advisor.
 */
public class AdvisorRepository {
    private final FamilyDao familyDao;

    @Inject
    public AdvisorRepository(FamilyDao familyDao) {
        this.familyDao = familyDao;
    }

    public LiveData<List<Family>> getFamilies() {
        return familyDao.queryFamilies();
    }

    public LiveData<Family> getFamily(long id) {
        return familyDao.queryFamily(id);
    }

    public void saveFamily(Family family) {
        int rowCount = familyDao.updateFamily(family);
        if (rowCount == 0) {
            familyDao.insertFamily(family);
        }
    }

    public void deleteFamily(Family family) {
        familyDao.deleteFamily(family);
    }
}