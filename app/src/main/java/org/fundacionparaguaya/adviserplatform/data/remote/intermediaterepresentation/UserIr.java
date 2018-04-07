package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

/**
 * The intermediate representation of a user from the remote database.
 */

public class UserIr {
    @SerializedName("userId")
    int id;
    @SerializedName("username")
    String username;
    @SerializedName("pass")
    String password;
    @SerializedName("active")
    boolean active;
}
