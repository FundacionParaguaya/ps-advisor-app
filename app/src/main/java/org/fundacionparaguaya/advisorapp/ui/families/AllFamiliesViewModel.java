package org.fundacionparaguaya.advisorapp.ui.families;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import org.fundacionparaguaya.advisorapp.data.model.Family;
import org.fundacionparaguaya.advisorapp.data.repositories.FamilyRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * The view model exposing data for the the all families page.
 */

public class AllFamiliesViewModel extends ViewModel {
    private FamilyRepository mFamilyRepository;
    private LiveData<List<Family>> mFamilyList;
    private MediatorLiveData<List<Family>> mFilteredFamilyList;
    private final MutableLiveData<String> mSearchQuery = new MutableLiveData<>();

    public AllFamiliesViewModel(FamilyRepository familyRepository) {
        this.mFamilyRepository = familyRepository;
        mFamilyList = mFamilyRepository.getFamilies();

        mFilteredFamilyList = new MediatorLiveData<>();

        mFilteredFamilyList.addSource(mFamilyList, list ->
                applyFilter(mSearchQuery.getValue(), list));

        mFilteredFamilyList.addSource(mSearchQuery, text ->
                applyFilter(mSearchQuery.getValue(), mFamilyList.getValue()));
    }

    /**
     * @param query
     * @param sourceList
     * @return a list with the families matching the search
     */
    private void applyFilter(String query, List<Family> sourceList) {
        if (sourceList == null) {
            //return if no families exist
            return;
        }

        if (query == null)
        {
            mFilteredFamilyList.setValue(sourceList);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Family> newFilteredList = new ArrayList<>();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(query)) {

                    newFilteredList.addAll(sourceList);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (Family family : sourceList) {
                        if (family.getName().toLowerCase().contains(query.toLowerCase())) {
                            // Adding Matched items
                            newFilteredList.add(family);
                        }
                    }
                }

                mFilteredFamilyList.postValue(newFilteredList);
            }
        }).start();
    }

    public LiveData<List<Family>> getFamilies() {
        return mFilteredFamilyList;
    }

    public LiveData<List<Family>> getAllFamilies() {
        return mFamilyList;
    }

    public void filter(final String searchText) {
        mSearchQuery.setValue(searchText);
    }
}



