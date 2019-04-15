package org.fundacionparaguaya.adviserplatform.data.remote;

import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.PriorityIr;
import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.SnapshotDetailsIr;
import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.SnapshotIr;
import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.SnapshotOverviewIr;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * The service used to retrieve snapshot from the remote database.
 */

public interface SnapshotService {

    @GET("snapshots")
    Call<List<SnapshotIr>> getSnapshots(
            @Query("survey_id") long surveyId,
            @Query("family_id") long familyId);

    @POST("snapshots")
    Call<SnapshotIr> postSnapshot(
            @Body SnapshotIr snapshotIr);

    @GET("snapshots/family")
    Call<List<SnapshotOverviewIr>> getSnapshotOverviews(
            @Query("family_id") long familyId);

    @GET("snapshots/indicators")
    Call<SnapshotDetailsIr> getSnapshotDetails(
            @Query("snapshot_id") long snapshotId);

    @GET("snapshots/priority")
    Call<List<PriorityIr>> getPriorities(
            @Query("snapshotIndicatorId") long snapshotIndicatorId);

    @POST("snapshots/priority")
    Call<PriorityIr> postPriority(
            @Body PriorityIr priorityIr);

    //region Temporary upload image for demo
    @PUT("families/{id}/image")
    Call<String> putFamilyPicture(@Part MultipartBody.Part file);

    @GET("snapshots/all/family")
    Call <List<SnapshotIr>> getAllSnapshotsByFamily(@Query("family_id") long familyId);

    @GET("snapshots/{snapShotId}")
    Call<SnapshotIr> getSnapshotById(@Path("snapShotId") Long snapShotId);

}
