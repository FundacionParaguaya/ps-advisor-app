package org.fundacionparaguaya.advisorapp.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * A family is the entity that is being helped by the advisor.
 */

@Entity(tableName = "families")
public class Family {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String address;
    @Embedded
    private Location location;
    @Embedded(prefix = "family_member_")
    private FamilyMember member;

    @Ignore
    public Family(int id, String name, FamilyMember member) {
        this(id, name, member, "", Location.UNKNOWN);
    }

    public Family(int id, String name, FamilyMember member, String address, Location location) {
        this.id = id;
        this.name = name;
        this.member = member;
        this.address = address;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Family family = (Family) o;

        if (getId() != family.getId()) {
            return false;
        }
        if (getName() != null ? !getName().equals(family.getName()) : family.getName() != null) {
            return false;
        }
        if (getAddress() != null ? !getAddress().equals(family.getAddress()) : family.getAddress() != null) {
            return false;
        }
        if (getLocation() != null ? !getLocation().equals(family.getLocation()) : family.getLocation() != null) {
            return false;
        }
        return getMember() != null ? getMember().equals(family.getMember()) : family.getMember() == null;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        result = 31 * result + (getLocation() != null ? getLocation().hashCode() : 0);
        result = 31 * result + (getMember() != null ? getMember().hashCode() : 0);
        return result;
    }
}
