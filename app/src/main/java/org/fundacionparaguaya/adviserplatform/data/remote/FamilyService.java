package org.fundacionparaguaya.adviserplatform.data.remote;

import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.FamilyIr;
import org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation.FamilyMemberIr;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * The service used to retrieve families from the remote database.
 */

public interface FamilyService {

    @GET("families")
    Call<List<FamilyIr>> getFamilies();

    @GET("families")
    Call<List<FamilyIr>> getFamiliesModifiedSince(@Query("last_modified_gt") String lastModified);

    @POST("families")
    Call<FamilyIr> postFamily(@Body FamilyIr family);

    @POST("people")
    Call<FamilyMemberIr> postFamilyMember(@Body FamilyMemberIr member);
}
