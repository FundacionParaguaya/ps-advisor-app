package org.fundacionparaguaya.adviserplatform.data.remote.intermediaterepresentation;

import com.google.gson.annotations.SerializedName;

/**
 * The intermediate representation of the family from the remote database.
 */

public class FamilyIr {
    @SerializedName("familyId")
    long id;
    @SerializedName("code")
    String code;
    @SerializedName("name")
    String name;
    @SerializedName("person")
    FamilyMemberIr member;
    @SerializedName("active")
    boolean active;
    @SerializedName("imageURL")
    String imageUrl;
}
