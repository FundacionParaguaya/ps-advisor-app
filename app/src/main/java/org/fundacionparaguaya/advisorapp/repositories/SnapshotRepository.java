package org.fundacionparaguaya.advisorapp.repositories;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.fundacionparaguaya.advisorapp.data.local.SnapshotDao;
import org.fundacionparaguaya.advisorapp.data.remote.SnapshotService;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrMapper;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.PriorityIr;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.SnapshotDetailsIr;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.SnapshotIr;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.SnapshotOverviewIr;
import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;
import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.models.Survey;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import static java.lang.String.format;

/**
 * The utility for the storage of snapshots.
 */

public class SnapshotRepository {
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

    public @Nullable Snapshot getPendingSnapshotNow(@Nullable Integer familyId) {
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
        }
    }

    private boolean pushSnapshots() {
        List<Snapshot> pending = snapshotDao.queryPendingFinishedSnapshots();
        boolean success = true;

        // attempt to push each of the pending snapshots
        for (Snapshot snapshot : pending) {
            try {
                Family family = familyRepository.getFamilyNow(snapshot.getFamilyId());
                Survey survey = surveyRepository.getSurveyNow(snapshot.getSurveyId());

                //region Temporary upload image for demo
                String familyPicturePath = null;
                for (BackgroundQuestion question : survey.getPersonalQuestions()) {
                    if (question.getName().equals("familyPicture")) {
                        familyPicturePath = snapshot.getBackgroundResponse(question);
                        snapshot.response(question, null);
                    }
                }
                //endregion Temporary upload image for demo

                // push the snapshot
                Response<SnapshotIr> snapshotResponse = snapshotService
                        .postSnapshot(IrMapper.mapSnapshot(snapshot, survey))
                        .execute();
                if (!snapshotResponse.isSuccessful() || snapshotResponse.body() == null) {
                    Log.w(TAG, format("pushSnapshots: Could not push snapshot with id %d! %s",
                            snapshot.getId(), snapshotResponse.errorBody().string()));
                    success = false;
                    continue;
                }
                snapshot.setRemoteId(snapshotResponse.body().getId());

                // push the priorities; don't need to save these responses
                for (PriorityIr priorityIr : IrMapper.mapPriorities(snapshot)) {
                    Response<PriorityIr> priorityResponse = snapshotService
                            .postPriority(priorityIr)
                            .execute();

                    if (!priorityResponse.isSuccessful() || priorityResponse.body() == null) {
                        Log.w(TAG, format("pushSnapshots: Could not push priority! %s",
                                priorityResponse.errorBody().string()));
                        success = false;
                    }
                }

                Response<List<PriorityIr>> prioritiesResponse = snapshotService
                        .getPriorities(snapshot.getRemoteId())
                        .execute();
                if (!prioritiesResponse.isSuccessful() || prioritiesResponse.body() == null) {
                    Log.w(TAG, format("pullSnapshots: Could not pull priorities for family %d! %s",
                            family.getRemoteId(), prioritiesResponse.errorBody().string()));
                    success = false;
                    continue;
                }

                // overwrite the pending snapshot with the snapshot from remote db
                Snapshot remoteSnapshot = IrMapper.mapSnapshot(
                        snapshotResponse.body(), prioritiesResponse.body(), family, survey);
                remoteSnapshot.setId(snapshot.getId());
                saveSnapshot(remoteSnapshot);

                // overwrite the pending family with the family that the remote db created
                Response<SnapshotDetailsIr> detailsResponse = snapshotService
                        .getSnapshotDetails(snapshot.getRemoteId())
                        .execute();
                if (!detailsResponse.isSuccessful() || detailsResponse.body() == null) {
                    Log.w(TAG, format("pullSnapshots: Could not pull snapshot details for snapshot %d! %s",
                            remoteSnapshot.getRemoteId(), detailsResponse.errorBody().string()));
                    success = false;
                    continue;
                }
                Family remoteFamily = IrMapper.mapFamily(detailsResponse.body().getFamily());
                remoteFamily.setId(family.getId());
                familyRepository.saveFamily(remoteFamily);

                //region Temporary upload image for demo
                if (familyPicturePath != null)
                    uploadFamilyPicture(remoteFamily, familyPicturePath);
                //endregion Temporary upload image for demo


            } catch (IOException e) {
                Log.e(TAG, format("pushSnapshots: Could not push snapshot with id %d!",
                        snapshot.getId()), e);
                success = false;
            }
        }
        return success;
    }

    //region Temporary upload image for demo
    private void uploadFamilyPicture(Family family, String imagePath) {
        File file = new File(imagePath);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        try {
            Response<String> response = snapshotService.putFamilyPicture(body).execute();

            if (!response.isSuccessful() || response.body() == null) {
                Log.w(TAG, format("uploadFamilyPicture: Could not upload! %s", response.errorBody().string()));
                return;
            }

            family.setImageUrl(response.body());
            familyRepository.saveFamily(family);
        } catch (IOException e) {
            Log.w(TAG, "uploadFamilyPicture: Could not upload!", e);
        }
    }
    //endregion Temporary upload image for demo

    private boolean pullSnapshots(@Nullable Date lastSync) {
        boolean success = true;
        List<Family> families; // the families to sync snapshots for
        if (lastSync != null) {
            families = familyRepository.getFamiliesModifiedSinceDateNow(lastSync);
        } else {
            families = familyRepository.getFamiliesNow();
        }

        for (Family family : families) {
            for (Survey survey : surveyRepository.getSurveysNow()) {
                success &= pullSnapshots(family, survey);
            }
        }
        return success;
    }

    private boolean pullSnapshots(Family family, Survey survey) {
        try {
            // get the snapshots
            Response<List<SnapshotIr>> snapshotsResponse = snapshotService
                    .getSnapshots(survey.getRemoteId(), family.getRemoteId())
                    .execute();
            if (!snapshotsResponse.isSuccessful() || snapshotsResponse.body() == null) {
                Log.w(TAG, format("pullSnapshots: Could not pull snapshots for family %d! %s",
                        family.getRemoteId(), snapshotsResponse.errorBody().string()));
                return false;
            }

            // get the snapshot overviews (which have the priorities)
            Response<List<SnapshotOverviewIr>> overviewsResponse = snapshotService
                    .getSnapshotOverviews(family.getRemoteId())
                    .execute();
            if (!overviewsResponse.isSuccessful() || overviewsResponse.body() == null) {
                Log.w(TAG, format("pullSnapshots: Could not pull overviews for family %d! %s",
                        family.getRemoteId(), overviewsResponse.errorBody().string()));
                return false;
            }

            List<Snapshot> snapshots = IrMapper.
                    mapSnapshots(snapshotsResponse.body(), overviewsResponse.body(), family, survey);
            for (Snapshot snapshot : snapshots) {
                Snapshot old = snapshotDao.queryRemoteSnapshotNow(snapshot.getRemoteId());
                if (old != null) {
                    snapshot.setId(old.getId());
                }
                saveSnapshot(snapshot);
            }
        } catch (IOException e) {
            Log.e(TAG, format("pullSnapshots: Could not pull snapshots for family %d!", family.getRemoteId()), e);
            return false;
        }
        return true;
    }

    /**
     * Synchronizes the local snapshots with the remote database.
     * @return Whether the sync was successful.
     */
    boolean sync(@Nullable Date lastSync) {
        boolean successful;
        successful = pushSnapshots();
        if (successful) {
            successful = pullSnapshots(lastSync);
        }

        return successful;
    }

    void clean() {
        snapshotDao.deleteAll();
    }
}
