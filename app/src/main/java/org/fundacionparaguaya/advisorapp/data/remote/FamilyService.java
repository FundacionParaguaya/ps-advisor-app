package org.fundacionparaguaya.advisorapp.data.remote;

import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.FamilyIr;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.FamilyMemberIr;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * The service used to retrieve families from the remote database.
 */

public interface FamilyService {

    @GET("families")
    Call<List<FamilyIr>> getFamilies();

    @POST("families")
    Call<FamilyIr> postFamily(@Body FamilyIr family);

    @POST("people")
    Call<FamilyMemberIr> postFamilyMember(@Body FamilyMemberIr member);
}
