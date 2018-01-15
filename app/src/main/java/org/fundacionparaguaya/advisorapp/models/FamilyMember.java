package org.fundacionparaguaya.advisorapp.models;

import android.arch.persistence.room.ColumnInfo;

/**
 * The member of a family being advised.
 */

public class FamilyMember {
    @ColumnInfo(name = "first_name")
    private String firstName;
    @ColumnInfo(name = "last_name")
    private String lastName;
    @ColumnInfo(name = "profile_url")
    private String profileUrl;

    public FamilyMember(String firstName, String lastName, String profileUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileUrl = profileUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
