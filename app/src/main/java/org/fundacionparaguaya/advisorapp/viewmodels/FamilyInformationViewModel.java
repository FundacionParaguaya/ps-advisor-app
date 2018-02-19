package org.fundacionparaguaya.advisorapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.repositories.FamilyRepository;
import org.fundacionparaguaya.advisorapp.repositories.SnapshotRepository;
import org.fundacionparaguaya.advisorapp.util.IndicatorUtilities;

import java.util.List;


public class FamilyInformationViewModel extends ViewModel {

    private FamilyRepository mFamilyRepository;
    private SnapshotRepository mSnapshotRespository;

    private LiveData<Family> currentFamily;

    final private MutableLiveData<Snapshot> mSelectedSnapshot = new MutableLiveData<>();

    private LiveData<List<Snapshot>> mSnapshots;


    //Maps the selected snapshot to a list of indicators. This livedata object will notify it's observers when
    //the selected snapshot changes
    final private LiveData<List<IndicatorOption>> mIndicatorsForSelected = Transformations.map(mSelectedSnapshot, selected ->
    {
        if(selected==null)
        {
            return null;
        }
        else
        {
            return IndicatorUtilities.getResponsesAscending(selected.getIndicatorResponses().values());
        }
    });

    //Maps the selected snapshot to a list of priorities This livedata object will notify it's observers when
    //the selected snapshot changes
    final private LiveData<List<LifeMapPriority>> mPrioritiesForSelected = Transformations.map(mSelectedSnapshot, selected ->
    {
        if(selected==null)
        {
            return null;
        }
        else
        {
            return selected.getPriorities();
        }
    });


    public  FamilyInformationViewModel(FamilyRepository familyRepository, SnapshotRepository snapshotRespository){
        mFamilyRepository = familyRepository;
        mSnapshotRespository = snapshotRespository;
    }

    /**
     * Sets the current family for this view model and returns the LiveData representation
     * @param id family id for this view model
     * @return current family selected
     */
    public LiveData<Family> setFamily(int id){
        currentFamily = mFamilyRepository.getFamily(id);

        mSnapshots = Transformations.switchMap(currentFamily, currentFamily -> {
            if(currentFamily==null)
            {
                return null;
            }
            else return mSnapshotRespository.getSnapshots(currentFamily);
        });

        return currentFamily;
    }

    /**
     * Returns the indicators for the selected snapshot. Will update when the selected snapshot is changed
     * @return
     */
    public LiveData<List<IndicatorOption>> getSnapshotIndicators()
    {
       return mIndicatorsForSelected;
    }

    /**
     * Returns the indicators for the selected snapshot. Will update when the selected snapshot is changed
     * @return
     */
    public LiveData<List<LifeMapPriority>> getSnapshotPriorities()
    {
        return mPrioritiesForSelected;
    }



    /**Gets the current family that's been set by setFamily**/
    public LiveData<Family> getCurrentFamily()
    {
        if(currentFamily == null)
        {
            throw new IllegalStateException("setFamily must be called in ViewModel before getCurrentFamily");
        }
        else return currentFamily;
    }

    public LiveData<Snapshot> getSelectedSnapshot()
    {
        return mSelectedSnapshot;
    }

    public LiveData<List<Snapshot>> getSnapshots()
    {
        return mSnapshots;
    }

    public void setSelectedSnapshot(Snapshot s)
    {
        mSelectedSnapshot.setValue(s);
    }
}
