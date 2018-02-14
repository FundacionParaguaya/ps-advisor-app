package org.fundacionparaguaya.advisorapp.data.remote;

import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.PriorityIr;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.SnapshotIr;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.SnapshotOverviewIr;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    @GET("snapshots/priority")
    Call<List<PriorityIr>> getPriorities(
            @Query("snapshotIndicatorId") long snapshotId);

    @POST("snapshots/priority")
    Call<PriorityIr> postPriority(
            @Body PriorityIr priorityIr);
}
