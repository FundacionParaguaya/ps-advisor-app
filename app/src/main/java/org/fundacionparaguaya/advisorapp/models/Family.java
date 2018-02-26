package org.fundacionparaguaya.advisorapp.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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

        if (getId() != family.getId()) return false;
        if (isActive() != family.isActive()) return false;
        if (getRemoteId() != null ? !getRemoteId().equals(family.getRemoteId()) : family.getRemoteId() != null)
            return false;
        if (getName() != null ? !getName().equals(family.getName()) : family.getName() != null)
            return false;
        if (getCode() != null ? !getCode().equals(family.getCode()) : family.getCode() != null)
            return false;
        if (getAddress() != null ? !getAddress().equals(family.getAddress()) : family.getAddress() != null)
            return false;
        if (getLocation() != null ? !getLocation().equals(family.getLocation()) : family.getLocation() != null)
            return false;
        return getMember() != null ? getMember().equals(family.getMember()) : family.getMember() == null;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getRemoteId() != null ? getRemoteId().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getCode() != null ? getCode().hashCode() : 0);
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        result = 31 * result + (isActive() ? 1 : 0);
        result = 31 * result + (getLocation() != null ? getLocation().hashCode() : 0);
        result = 31 * result + (getMember() != null ? getMember().hashCode() : 0);
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
            FamilyMember member = FamilyMember.builder().snapshot(snapshot).build();
            member(member);
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
