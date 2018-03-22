package org.fundacionparaguaya.advisorapp.data.remote;

import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.SurveyIr;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * The service used to retrieve surveys from the remote database.
 */

public interface SurveyService {

    @GET("surveys")
    Call<List<SurveyIr>> getSurveys();

    @GET("surveys")
    Call<List<SurveyIr>> getSurveysModifiedSince(@Query("last_modified_gt") String lastModified);
}
