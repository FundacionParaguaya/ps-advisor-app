package org.fundacionparaguaya.advisorapp.models;

import android.arch.persistence.room.ColumnInfo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;

/**
 * The member of a family being advised.
 */

public class FamilyMember {
    @ColumnInfo(name = "first_name")
    private String firstName;
    @ColumnInfo(name = "last_name")
    private String lastName;
    @ColumnInfo(name = "birthdate")
    private String birthdate;
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;
    @ColumnInfo(name = "identification_type")
    private String identificationType;
    @ColumnInfo(name = "identification_number")
    private String identificationNumber;
    @ColumnInfo(name = "gender")
    private String gender;
    @ColumnInfo(name = "country_of_birth")
    private String countryOfBirth;
    @ColumnInfo(name = "profile_url")
    private String profileUrl;

    /**
     * Creates a new family member.
     */
    public FamilyMember(String firstName,
                        String lastName,
                        String birthdate,
                        String phoneNumber,
                        String identificationType,
                        String identificationNumber,
                        String gender,
                        String countryOfBirth,
                        String profileUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.phoneNumber = phoneNumber;
        this.identificationType = identificationType;
        this.identificationNumber = identificationNumber;
        this.gender = gender;
        this.countryOfBirth = countryOfBirth;
        this.profileUrl = profileUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public String getGender() {
        return gender;
    }

    public String getCountryOfBirth() {
        return countryOfBirth;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FamilyMember that = (FamilyMember) o;

        return new EqualsBuilder()
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(birthdate, that.birthdate)
                .append(phoneNumber, that.phoneNumber)
                .append(identificationType, that.identificationType)
                .append(identificationNumber, that.identificationNumber)
                .append(gender, that.gender)
                .append(countryOfBirth, that.countryOfBirth)
                .append(profileUrl, that.profileUrl)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(birthdate)
                .append(phoneNumber)
                .append(identificationType)
                .append(identificationNumber)
                .append(gender)
                .append(countryOfBirth)
                .append(profileUrl)
                .toHashCode();
    }

    public static class Builder {
        private String firstName;
        private String lastName;
        private String birthdate;
        private String phoneNumber;
        private String identificationType;
        private String identificationNumber;
        private String gender;
        private String countryOfBirth;
        private String profileUrl;

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder birthdate(String birthdate) {
            this.birthdate = birthdate;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder identificationType(String identificationType) {
            this.identificationType = identificationType;
            return this;
        }

        public Builder identificationNumber(String identificationNumber) {
            this.identificationNumber = identificationNumber;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder countryOfBirth(String countryCode) {
            this.countryOfBirth = countryCode;
            return this;
        }

        public Builder profileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
            return this;
        }

        public Builder snapshot(Snapshot snapshot) {
            for (Map.Entry<BackgroundQuestion, String> entry
                    : snapshot.getPersonalResponses().entrySet()) {
                switch(entry.getKey().getName()) {
                    case "firstName":
                        firstName(entry.getValue());
                        break;
                    case "lastName":
                        lastName(entry.getValue());
                        break;
                    case "birthdate":
                        birthdate(entry.getValue());
                        break;
                    case "countryOfBirth":
                        countryOfBirth(entry.getValue());
                        break;
                    case "identificationType":
                        identificationType(entry.getValue());
                        break;
                    case "identificationNumber":
                        identificationNumber(entry.getValue());
                        break;
                    case "phoneNumber":
                        phoneNumber(entry.getValue());
                        break;
                    case "gender":
                        gender(entry.getValue());
                        break;
                    default:
                        break;
                }
            }
            return this;
        }

        public FamilyMember build() {
            return new FamilyMember(
                    firstName,
                    lastName,
                    birthdate,
                    phoneNumber,
                    identificationType,
                    identificationNumber,
                    gender,
                    countryOfBirth,
                    profileUrl);
        }
    }
}
