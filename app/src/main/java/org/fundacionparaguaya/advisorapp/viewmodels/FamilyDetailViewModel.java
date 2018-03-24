package org.fundacionparaguaya.advisorapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import com.instabug.library.Instabug;
import org.fundacionparaguaya.advisorapp.BuildConfig;
import org.fundacionparaguaya.advisorapp.models.*;
import org.fundacionparaguaya.advisorapp.repositories.FamilyRepository;
import org.fundacionparaguaya.advisorapp.repositories.SnapshotRepository;
import org.fundacionparaguaya.advisorapp.util.IndicatorUtilities;

import java.util.Collection;
import java.util.List;


public class FamilyDetailViewModel extends ViewModel {

    private FamilyRepository mFamilyRepository;
    private SnapshotRepository mSnapshotRespository;

    private LiveData<Family> currentFamily;

    final private MutableLiveData<Snapshot> mSelectedSnapshot = new MutableLiveData<>();

    private LiveData<List<Snapshot>> mSnapshots;
    private final MutableLiveData<LifeMapPriority> mSelectedPriority = new MutableLiveData<>();


    //Maps the selected snapshot to a list of indicators. This livedata object will notify it's observers when
    //the selected snapshot changes
    final private LiveData<Collection<IndicatorOption>> mIndicatorsForSelected = Transformations.map(mSelectedSnapshot, selected ->
    {
        if (selected == null) {
            return null;
        } else {
            return IndicatorUtilities.getResponsesAscending(selected.getIndicatorResponses().values());
        }
    });

    //Maps the selected snapshot to a list of priorities This livedata object will notify it's observers when
    //the selected snapshot changes
    final private LiveData<List<LifeMapPriority>> mPrioritiesForSelected = Transformations.map(mSelectedSnapshot, selected ->
    {
        if (selected == null) {
            return null;
        } else {
            return selected.getPriorities();
        }
    });

    public FamilyDetailViewModel(FamilyRepository familyRepository, SnapshotRepository snapshotRespository) {
        mFamilyRepository = familyRepository;
        mSnapshotRespository = snapshotRespository;
    }

    /**
     * Sets the current family for this view model and returns the LiveData representation
     *
     * @param id family id for this view model
     * @return current family selected
     */
    public LiveData<Family> setFamily(int id) {
        currentFamily = mFamilyRepository.getFamily(id);

        mSnapshots = Transformations.switchMap(currentFamily, currentFamily -> {
            if (currentFamily == null) {
                return null;
            } else return mSnapshotRespository.getSnapshots(currentFamily);
        });

        return currentFamily;
    }

    /**
     * Gets the current family that's been set by setFamily
     **/
    public LiveData<Family> getCurrentFamily() {
        if (currentFamily == null) {
            throw new IllegalStateException("setFamily must be called in ViewModel before getCurrentFamily");
        } else return currentFamily;
    }

    //TODO: Instead of returning the value currently selected snapshot, this should return the latest value from the family
    //Right now though, priorities are associated with snapshots instead of families
    public LiveData<IndicatorOption> getLatestIndicatorResponse(Indicator i) {
        if (mSelectedSnapshot.getValue() != null) {
            return Transformations.map(mSelectedSnapshot, selected ->
            {
                if (selected == null) {
                    return null;
                } else {
                    return IndicatorUtilities.getResponseForIndicator(i, mSelectedSnapshot.getValue().getIndicatorResponses());
                }
            });
        } else {
            Exception e = new IllegalStateException("getLatestIndicatorResponse called with no snapshot selected");

            if (BuildConfig.DEBUG) e.printStackTrace();
            else Instabug.reportException(e);

            return null;
        }
    }

    public LiveData<Snapshot> getSelectedSnapshot() {
        return mSelectedSnapshot;
    }

    public LiveData<List<Snapshot>> getSnapshots() {
        return mSnapshots;
    }

    public void setSelectedSnapshot(Snapshot s) {
        mSelectedSnapshot.setValue(s);
    }

    public LiveData<LifeMapPriority> getSelectedPriority() {
        return mSelectedPriority;
    }

    public void setSelectedPriority(LifeMapPriority priority) {
        mSelectedPriority.setValue(priority);
    }

    public void removeSelectedPriority(){
        mSelectedPriority.setValue(null);
    }
    /**
     * Returns the priorities for the selected snapshot. Will update when the selected snapshot is changed
     *
     * @return
     */
    public LiveData<List<LifeMapPriority>> getPriorities() {
        return mPrioritiesForSelected;
    }

    /**
     * Returns the indicators for the selected snapshot. Will update when the selected snapshot is changed
     *
     * @return
     */
    public LiveData<Collection<IndicatorOption>> getSnapshotIndicators() {
        return mIndicatorsForSelected;
    }

    public boolean hasImageUri() {
        Family family = getCurrentFamily().getValue();
        if (family == null || family.getImageUrl() == null || family.getImageUrl().isEmpty()) {
            return false;
        }
        return true;
    }

    public Uri getImageUri() {
        Uri uri;
        Family family = getCurrentFamily().getValue();
        if (family != null && family.getImageUrl() != null && !family.getImageUrl().isEmpty()) {
            uri = Uri.parse(family.getImageUrl());
        } else {
            uri = Uri.parse("https://s3.us-east-2.amazonaws.com/fp-psp-images/44-3.jpg");
        }
        return uri;
    }
}
