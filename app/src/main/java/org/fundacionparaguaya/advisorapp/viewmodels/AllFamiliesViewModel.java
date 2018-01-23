package org.fundacionparaguaya.advisorapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.repositories.FamilyRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * The view model exposing data for the the all families page.
 */

public class AllFamiliesViewModel extends ViewModel {
    private FamilyRepository mFamilyRepository;

    public AllFamiliesViewModel(FamilyRepository familyRepository) {
        this.mFamilyRepository = familyRepository;
    }

    public LiveData<List<Family>> getFamilies() {
        return mFamilyRepository.getFamilies();
    }

    public boolean syncFamilies() {
        try {
            return mFamilyRepository.sync().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sync() {
        mFamilyRepository.sync().execute();
    }
}


