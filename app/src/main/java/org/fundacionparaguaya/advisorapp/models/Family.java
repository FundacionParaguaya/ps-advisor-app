package org.fundacionparaguaya.advisorapp.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Map;

/**
 * A family is the entity that is being helped by the advisor.
 */

@Entity(tableName = "families",
        indices={@Index(value="remote_id", unique=true)})
public class Family {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "remote_id")
    private Long remoteId;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "code")
    private String code;
    @ColumnInfo(name = "address")
    private String address;
    @ColumnInfo(name="is_active")
    private boolean isActive;
    @Embedded
    private Location location;
    @Embedded(prefix = "family_member_")
    private FamilyMember member;

    /**
     * Creates a new family. You should use the {@link Family#builder()} to construct a new family instead.
     */
    public Family(int id,
                  Long remoteId,
                  @NonNull String name,
                  @NonNull String code,
                  String address,
                  Location location,
                  FamilyMember member,
                  boolean isActive) {
        this.id = id;
        this.remoteId = remoteId;
        this.name = name;
        this.code = code;
        this.address = address;
        this.location = location;
        this.member = member;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(Long remoteId) {
        this.remoteId = remoteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Location location) {this.location = location; }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public FamilyMember getMember() {
        return member;
    }

    public String getAddress() {
        return address;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isActive() {
        return isActive;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Family family = (Family) o;

        if (id != family.id) return false;
        if (isActive != family.isActive) return false;
        if (remoteId != null ? !remoteId.equals(family.remoteId) : family.remoteId != null)
            return false;
        if (name != null ? !name.equals(family.name) : family.name != null) return false;
        if (code != null ? !code.equals(family.code) : family.code != null) return false;
        if (address != null ? !address.equals(family.address) : family.address != null)
            return false;
        if (location != null ? !location.equals(family.location) : family.location != null)
            return false;
        return member != null ? member.equals(family.member) : family.member == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (remoteId != null ? remoteId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (isActive ? 1 : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (member != null ? member.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private int id;
        private Long remoteId;
        private String name;
        private String code;
        private String address;
        private Location location;
        private FamilyMember member;
        private boolean isActive = true;

        /**
         * Sets the ID of the new family. Should only be used if overwriting an existing family!
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder remoteId(Long remoteId) {
            this.remoteId = remoteId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        public Builder member(FamilyMember member) {
            this.member = member;
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        /**
         * Fills in the information about a family that is available from a snapshot's personal
         * responses.
         */
        public Builder snapshot(Snapshot snapshot) {
            Map<BackgroundQuestion, String> personalResponses = snapshot.getPersonalResponses();
            FamilyMember.Builder memberBuilder = FamilyMember.builder();
            for (Map.Entry<BackgroundQuestion, String> entry : personalResponses.entrySet()) {
                switch(entry.getKey().getName()) {
                    case "firstName":
                        memberBuilder.firstName(entry.getValue());
                        break;
                    case "lastName":
                        memberBuilder.lastName(entry.getValue());
                        break;
                    case "birthdate":
                        memberBuilder.birthdate(entry.getValue());
                        break;
                    case "countryOfBirth":
                        memberBuilder.countryOfBirth(entry.getValue());
                        break;
                    case "phoneNumber":
                        memberBuilder.phoneNumber(entry.getValue());
                        break;
                    case "gender":
                        memberBuilder.gender(entry.getValue());
                        break;
                    default:
                        break;
                }
            }
            member(memberBuilder.build());
            name(member.getFirstName() + " " + member.getLastName());
            code(member.getCountryOfBirth()
                    + "."
                    + member.getFirstName().toUpperCase().charAt(0)
                    + member.getLastName().toUpperCase().charAt(0)
                    + "."
                    + member.getBirthdate().replace("-", ""));
            return this;
        }

        public Family build() {
            return new Family(id, remoteId, name, code, address, location, member, isActive);
        }
    }
}
