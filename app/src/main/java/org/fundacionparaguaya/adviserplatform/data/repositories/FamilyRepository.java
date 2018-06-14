package org.fundacionparaguaya.adviserplatform.data.repositories;

import android.arch.lifecycle.LiveData;
import android.support.annotation.Nullable;
import org.fundacionparaguaya.adviserplatform.data.local.FamilyDao;
import org.fundacionparaguaya.adviserplatform.data.model.Family;
import org.fundacionparaguaya.adviserplatform.data.remote.FamilyService;
import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.FamilyIr;
import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.IrMapper;
import retrofit2.Response;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.String.format;

/**
 * The utility for the storage of families and their members.
 */
public class FamilyRepository extends BaseRepository {
    private static final String TAG = "FamilyRepository";

    private final FamilyDao familyDao;
    private final FamilyService familyService;

    @Inject
    public FamilyRepository(FamilyDao familyDao,
                            FamilyService familyService) {
        this.familyDao = familyDao;
        this.familyService = familyService;
    }

    //region Family
    public LiveData<List<Family>> getFamilies() {
        return familyDao.queryFamilies();
    }

    /**
     * Gets the families synchronously.
     */
    public List<Family> getFamiliesNow() {
        return familyDao.queryFamiliesNow();
    }

    /**
     * Gets the families that were modified since a given date now.
     */
    public List<Family> getFamiliesModifiedSinceDateNow(Date date) {
        return familyDao.queryFamiliesModifiedSinceDateNow(date.getTime());
    }

    public LiveData<Family> getFamily(int id) {
        return familyDao.queryFamily(id);
    }

    /**
     * Gets a family synchronously.
     */
    public Family getFamilyNow(int id) {
        return familyDao.queryFamilyNow(id);
    }

    public void saveFamily(Family family) {
        long rows = familyDao.updateFamily(family);
        if (rows == 0) { // no row was updated
            int id = (int) familyDao.insertFamily(family);
            family.setId(id);
        }
    }

    /**
     * Synchronizes the local families with the remote database.
     * @return Whether the sync was successful.
     */
    public boolean sync(@Nullable Date lastSync) {
        boolean result = pullFamilies(lastSync);
        clearSyncStatus();

        return result;
    }

    void clean() {
        familyDao.deleteAll();
    }

    private boolean pullFamilies(@Nullable Date lastSync) {
        try {
            //TODO Sodep: why ask here and again inside family loop?
            if(shouldAbortSync()) return false;

            Response<List<FamilyIr>> response;
            if (lastSync != null) {
                String lastSyncString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
                        .format(lastSync);
                response = familyService.getFamiliesModifiedSince(lastSyncString).execute();
            } else {
                response = familyService.getFamilies().execute();
            }

            if (!response.isSuccessful() || response.body() == null) {
                Timber.tag(TAG);
                Timber.e(format("pullFamilies: Could not pull families! %s", response.errorBody().string()));
                return false;
            }

            List<Family> families = IrMapper.mapFamilies(response.body());
            for (Family family : families) {

                //TODO Sodep: second check for sync abort
                if(shouldAbortSync()) return false;

                Family old = familyDao.queryRemoteFamilyNow(family.getRemoteId());
                if (old != null) {
                    family.setId(old.getId());
                    family.setLastModified(new Date()); // TODO: Replace with last modified from server
                }
                saveFamily(family);
            }
        } catch (IOException e) {
            Timber.tag(TAG);
            Timber.e("pullFamilies: Could not pull families!", e);
            return false;
        }
        return true;
    }
    //endregion
}
