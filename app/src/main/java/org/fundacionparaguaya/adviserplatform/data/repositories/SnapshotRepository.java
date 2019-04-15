package org.fundacionparaguaya.adviserplatform.data.repositories;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.data.local.SnapshotDao;
import org.fundacionparaguaya.adviserplatform.data.model.BackgroundQuestion;
import org.fundacionparaguaya.adviserplatform.data.model.Family;
import org.fundacionparaguaya.adviserplatform.data.model.Snapshot;
import org.fundacionparaguaya.adviserplatform.data.model.Survey;
import org.fundacionparaguaya.adviserplatform.data.remote.SnapshotService;
import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.*;
import org.fundacionparaguaya.adviserplatform.exceptions.DataRequiredException;
import org.fundacionparaguaya.adviserplatform.util.AppConstants;
import org.perf4j.StopWatch;

import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

import javax.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;

/**
 * The utility for the storage of snapshots.
 */

public class SnapshotRepository extends BaseRepository {
    private static final String TAG = "SnapshotRepository";

    private final SnapshotDao snapshotDao;
    private final SnapshotService snapshotService;

    private final FamilyRepository familyRepository;
    private final SurveyRepository surveyRepository;

    @Inject
    public SnapshotRepository(SnapshotDao snapshotDao,
                              SnapshotService snapshotService,
                              FamilyRepository familyRepository,
                              SurveyRepository surveyRepository) {
        this.snapshotDao = snapshotDao;
        this.snapshotService = snapshotService;
        this.familyRepository = familyRepository;
        this.surveyRepository = surveyRepository;
        setPreferenceKey(String.format("%s-%s", AppConstants.KEY_LAST_SYNC_TIME, TAG));
    }

    public LiveData<List<Snapshot>> getSnapshots(@NonNull Family family) {
        return snapshotDao.queryFinishedSnapshotsForFamily(family.getId());
    }

    /**
     * Gets the pending snapshots for the given family, or <code>null</code></code?>null for pending snapshots without a
     * family.
     */
    public LiveData<Snapshot> getPendingSnapshot(@Nullable Integer familyId) {
        if (familyId != null) {
            return snapshotDao.queryInProgressSnapshotForFamily(familyId);
        } else {
            return snapshotDao.queryInProgressSnapshotForNewFamily();
        }
    }

    public @Nullable
    Snapshot getPendingSnapshotNow(@Nullable Integer familyId) {
        if (familyId != null) {
            return snapshotDao.queryInProgressSnapshotForFamilyNow(familyId);
        } else {
            return snapshotDao.queryInProgressSnapshotForNewFamilyNow();
        }
    }

    private void discardPendingSnapshot(@NonNull Snapshot snapshot) {
        snapshotDao.deleteInProgressSnapshot(snapshot.getId());
    }

    /**
     * Saves a snapshot, relating a new family to it if one hasn't been created yet.
     * Note: This will automatically discard previous pending snapshots.
     */
    public void saveSnapshot(@NonNull Snapshot snapshot) {
        if (!snapshot.isInProgress() && snapshot.getFamilyId() == null) {
            // need to create a family for the snapshot before saving
            Family family = Family.builder().snapshot(snapshot).build();
            familyRepository.saveFamily(family);
            snapshot.setFamilyId(family.getId());
        }
        if (snapshot.isInProgress()) {
            // need to discard any previous pending snapshots
            Snapshot previousInProgress = getPendingSnapshotNow(snapshot.getFamilyId());
            if (previousInProgress != null && previousInProgress.getId() != snapshot.getId()) {
                discardPendingSnapshot(previousInProgress);
            }
        }
        long rows = snapshotDao.updateSnapshot(snapshot);
        if (rows == 0) { // no row was updated
            int id = (int) snapshotDao.insertSnapshot(snapshot);
            snapshot.setId(id);
            Log.d(TAG, String.format("Snapshot INSERTED: %s", snapshot.toDebugString()));
        } else {
            Log.d(TAG, String.format("Snapshot UPDATED %s", snapshot.toDebugString()));
        }
    }

    private boolean pushSnapshots() {
        List<Snapshot> pending = snapshotDao.queryPendingFinishedSnapshots();
        boolean success = true;
        //We set the organization id, so families query can be filtered by organization
        long organizationId = getSharedPreferences().getLong(AppConstants.ORGANIZATION_ID,
                -1);

        //TODO Sodep: error handling on REST API calls should be refactored
        // attempt to push each of the pending snapshots
        for (Snapshot snapshot : pending) {
            try {
                Family family = familyRepository.getFamilyNow(snapshot.getFamilyId());
                Survey survey = surveyRepository.getSurveyNow(snapshot.getSurveyId());
                Response<SnapshotIr> snapshotResponse = null;
                if (organizationId > 0) {
                    snapshot.setOrganizationId(organizationId);
                } else {
                    throw new DataRequiredException("Organization ID is required for snapshot");
                }

                //region Temporary upload image for demo
                String familyPicturePath = null;
                for (BackgroundQuestion question : survey.getPersonalQuestions()) {
                    if (question.getName().equals("familyPicture")) {
                        familyPicturePath = snapshot.getBackgroundResponse(question);
                        snapshot.response(question, null);
                    }
                }
                //endregion Temporary upload image for demo
                Log.d(TAG, String.format("Snapshot SQLite: %s", snapshot.toDebugString()));
                if (snapshot.getRemoteId() == null) {
                    //TODO Sodep: does this POST bring another information besides "id" from server,
                    //TODO Sodep: other than snapshots already has on device?
                    // push the snapshot
                    snapshotResponse = snapshotService
                            .postSnapshot(IrMapper.mapSnapshot(snapshot, survey))
                            .execute();
                    checkFor4xxCode(snapshotResponse);
                    if (!snapshotResponse.isSuccessful() || snapshotResponse.body() == null) {
                        Timber.tag(TAG);
                        Timber.e(format("pushSnapshots: Could not push snapshot with id %d! %s",
                                snapshot.getId(), snapshotResponse.errorBody().string()));
                        success = false;
                    } else {
                        //TODO Sodep: set remoteId from REST API response
                        snapshot.setRemoteId(snapshotResponse.body().getId());
                        snapshot.setSnapshotIndicatorId(snapshotResponse.body().getSnapshotIndicatorId());
                        saveSnapshot(snapshot);

                    }
                } else {
                    //Snapshot was already pushed
                    snapshotResponse = snapshotService.getSnapshotById(snapshot.getRemoteId()).execute();
                }
                // push the priorities; don't need to save these responses
                for (PriorityIr priorityIr : IrMapper.mapPriorities(snapshot)) {
                    Response<PriorityIr> priorityResponse = snapshotService
                            .postPriority(priorityIr)
                            .execute();
                    checkFor4xxCode(priorityResponse);
                    if (!priorityResponse.isSuccessful() || priorityResponse.body() == null) {
                        //TODO Sodep: How this information is  recovered when there's an error?
                        Timber.tag(TAG);
                        Timber.e(format("pushSnapshots: Could not push priority! %s",
                                priorityResponse.errorBody().string()));
                        success = false;
                    }
                }
                if (success) {
                    Response<List<PriorityIr>> prioritiesResponse = snapshotService
                            .getPriorities(snapshot.getRemoteId())
                            .execute();
                    checkFor4xxCode(prioritiesResponse);
                    if (!prioritiesResponse.isSuccessful() || prioritiesResponse.body() == null) {
                        Timber.tag(TAG);
                        Timber.e(format("pullSnapshots: Could not pull priorities for family %d! %s",
                                family.getRemoteId(), prioritiesResponse.errorBody().string()));
                        success = false;
                    } else {
                        snapshot.setPrioritiesSynced(true);
                        // overwrite the pending snapshot with the snapshot from remote db
                        Snapshot remoteSnapshot = IrMapper.mapSnapshot(
                                snapshotResponse.body(), prioritiesResponse.body(), family, survey);
                        //TODO Sodep: why the remote snapshot needs the local database "id" ?
                        remoteSnapshot.setId(snapshot.getId());
                        saveSnapshot(remoteSnapshot);

                        // overwrite the pending family with the family that the remote db created
                        Response<SnapshotDetailsIr> detailsResponse = snapshotService
                                .getSnapshotDetails(snapshot.getRemoteId())
                                .execute();
                        checkFor4xxCode(detailsResponse);
                        if (!detailsResponse.isSuccessful() || detailsResponse.body() == null) {
                            Timber.tag(TAG);
                            Timber.e(format("pullSnapshots: Could not pull snapshot details for snapshot %d! %s",
                                    remoteSnapshot.getRemoteId(), detailsResponse.errorBody().string()));
                            success = false;
                        }
                        if (success) {
                            Family remoteFamily = IrMapper.mapFamily(detailsResponse.body().getFamily());
                            if (remoteFamily != null) {
                                //TODO Sodep: This should not be null, but sometimes server
                                // returns no data
                                remoteFamily.setId(family.getId());
                                familyRepository.saveFamily(remoteFamily);

                                //region Temporary upload image for demo
                                if (familyPicturePath != null)
                                    uploadFamilyPicture(remoteFamily, familyPicturePath);
                                //endregion Temporary upload image for demo
                            }
                        }
                    }
                }
            } catch (IOException e) {
                Timber.tag(TAG);
                Timber.e(format("pushSnapshots: Could not push snapshot with id %d!",
                        snapshot.getId()), e);
                success = false;
            }
            if (success) {
                Log.d(TAG, String.format("Deleted from SQLite: %s", snapshot.toDebugString()));
                snapshotDao.deleteSyncedSnapshot(snapshot.getId());
                //snapshotDao.deleteInProgressSnapshot(snapshot.getId());

            }
        }
        //TODO Sodep: this is a loop, with several modifications to _success_
        //TODO Sodep: returning only last state is not accurate
        return success;
    }

    private void checkFor4xxCode(Response<?> snapshotResponse) {
        if (AppConstants.HTTP_SC_UNAUTHORIZED == snapshotResponse.code() ||
                AppConstants.HTTP_SC_BAD_REQUEST == snapshotResponse.code()) {
            throw new HttpException(snapshotResponse);
        }
    }

    //region Temporary upload image for demo
    private void uploadFamilyPicture(Family family, String imagePath) {
        File file = new File(imagePath);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        try {
            Response<String> response = snapshotService.putFamilyPicture(body).execute();
            checkFor4xxCode(response);

            if (!response.isSuccessful() || response.body() == null) {
                Timber.w(format("uploadFamilyPicture: Could not upload! %s", response.errorBody().string()));
                return;
            }

            family.setImageUrl(response.body());
            familyRepository.saveFamily(family);
        } catch (IOException e) {
            Timber.w("uploadFamilyPicture: Could not upload!", e);
        }
    }
    //endregion Temporary upload image for demo

    private boolean pullSnapshots() {
        Date lastSync = getLastSyncDate() > 0 ? new Date(getLastSyncDate()) : null;
        boolean success = true;
        long loopCount = 0;
        final List<Survey> surveysNow = surveyRepository.getSurveysNow();

        StopWatch stopWatch = new StopWatch("pullSnapshots");
        List<Family> families; // the families to sync snapshots for
        if (lastSync != null) {
            families = familyRepository.getFamiliesModifiedSinceDateNow(lastSync);
        } else {
            families = familyRepository.getFamiliesNow();
        }

        setRecordsCount(families.size());

        for (Family family : families) {
            if (shouldAbortSync()) return false;
            Log.d(TAG, stopWatch.lap(String.format("Family: %s", family.getName())));
            success &= pullSnapshots(family, surveysNow);
            if (success && getDashActivity() != null) {
                getDashActivity().setSyncLabel(R.string.syncing_family_snapshots, ++loopCount,
                        getRecordsCount());
            }
        }
        Log.d(TAG, stopWatch.stop("Finished pulling snapshots"));
        return success;
    }

    private boolean pullSnapshots(Family family, List<Survey> surveyList)
            throws HttpException {
        try {
            if (shouldAbortSync()) return false;
            // get the snapshots
            Response<List<SnapshotIr>> snapshotsResponse = snapshotService
                    .getAllSnapshotsByFamily(family.getRemoteId())
                    .execute();
            checkFor4xxCode(snapshotsResponse);
            if (!snapshotsResponse.isSuccessful() || snapshotsResponse.body() == null) {
                Timber.tag(TAG);
                Timber.e(format("pullSnapshots: Could not pull snapshots for family %d! %s",
                        family.getRemoteId(), snapshotsResponse.errorBody().string()));
                return false;
            }

            if (shouldAbortSync()) return false;

            // get the snapshot overviews (which have the priorities)
            Response<List<SnapshotOverviewIr>> overviewsResponse = snapshotService
                    .getSnapshotOverviews(family.getRemoteId())
                    .execute();
            checkFor4xxCode(overviewsResponse);
            if (!overviewsResponse.isSuccessful() || overviewsResponse.body() == null) {
                Timber.tag(TAG);
                Timber.e(format("pullSnapshots: Could not pull overviews for family %d! %s",
                        family.getRemoteId(), overviewsResponse.errorBody().string()));
                return false;
            }

            if (shouldAbortSync()) return false;

            List<Snapshot> snapshots = IrMapper.
                    mapSnapshots(snapshotsResponse.body(), overviewsResponse.body(), family,
                            surveyList);
            for (Snapshot snapshot : snapshots) {
                if (shouldAbortSync()) return false;
                if (snapshot.getPriorities() != null) {
                    if (snapshot.getPriorities().isEmpty()) {
                        Response<List<PriorityIr>> prioritiesResponse = snapshotService
                                .getPriorities(snapshot.getSnapshotIndicatorId())
                                .execute();
                        checkFor4xxCode(prioritiesResponse);
                        if (!prioritiesResponse.isSuccessful() || prioritiesResponse.body() == null) {
                            Timber.e(format(
                                    "pullSnapshots: Could not pull priorities for snaptshot %d! %s",
                                    snapshot.getRemoteId(), prioritiesResponse.errorBody().string()));
                            throw new HttpException(prioritiesResponse);
                        }

                        Survey currentSurvey = getCurrentSnapshotSurvey(surveyList, snapshot.getSurveyId());
                        if (currentSurvey != null) {
                            snapshot.setPriorities(
                                    IrMapper.mapPriorities(prioritiesResponse.body(), currentSurvey));
                        }
                    }
                } else {
                    snapshot.setPriorities(Collections.emptyList());
                }
                Snapshot old = snapshotDao.queryRemoteSnapshotNow(snapshot.getRemoteId());
                if (old != null) {
                    snapshot.setId(old.getId());
                }
                saveSnapshot(snapshot);
            }
        } catch (IOException e) {
            Timber.tag(TAG);
            Timber.e(format("pullSnapshots: Could not pull snapshots for family %d!",
                    family.getRemoteId()), e);
            return false;
        }
        return true;
    }

    private Survey getCurrentSnapshotSurvey(List<Survey> surveyList, long survey_id) {
        for (Survey survey : surveyList) {
            if (survey.getId() == survey_id) {
                return survey;
            }
        }
        return null;
    }

    public List<Snapshot> getQueueSnapshots() {
        return snapshotDao.queryPendingFinishedSnapshots();
    }

    /**
     * Synchronizes the local snapshots with the remote database.
     *
     * @return Whether the sync was successful.
     */
    @Override
    boolean sync() {
        boolean successful;
        successful = pushSnapshots();
        if (successful) {
            successful = pullSnapshots();
        }

        return successful;
    }

    public void clean() {
        List<Snapshot> pending = snapshotDao.queryPendingFinishedSnapshots();
        setmIsAlive(new AtomicBoolean(false));
        if (pending.isEmpty()) {
            snapshotDao.deleteAll();
        } else {
            Log.d(TAG, "There are still pending snapshots to sync");
        }
    }

    @Override
    public boolean needsSync() {
        List<Snapshot> pending = snapshotDao.queryPendingFinishedSnapshots();
        return super.needsSync() || (pending != null && !pending.isEmpty());
    }
}
