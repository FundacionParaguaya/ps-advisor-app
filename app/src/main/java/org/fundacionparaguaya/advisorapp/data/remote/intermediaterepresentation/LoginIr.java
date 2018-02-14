package org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

/**
 * The intermediate representation of a login from the remote database.
 */

public class LoginIr {
    @SerializedName("access_token")
    String accessToken;
    @SerializedName("token_type")
    String tokenType;
    @SerializedName("expires_in")
    int expiresIn;
    @SerializedName("refresh_token")
    String refreshToken;
}
