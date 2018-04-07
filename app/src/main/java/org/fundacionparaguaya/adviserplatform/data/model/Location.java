package org.fundacionparaguaya.adviserplatform.data.model;

import android.arch.persistence.room.Embedded;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A geographical location.
 */

public class Location {
    public static final Location UNKNOWN = new Location(0, 0, null, null);

    private double longitude;
    private double latitude;
    @Embedded(prefix = "city_")
    private City city;
    @Embedded(prefix = "country_")
    private Country country;

    public Location(double longitude, double latitude, Country country, City city) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
        this.city = city;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public Country getCountry() {
        return country;
    }

    public City getCity() {
        return city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location that = (Location) o;

        return new EqualsBuilder()
                .append(longitude, that.longitude)
                .append(latitude, that.latitude)
                .append(country, that.country)
                .append(city, that.city)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(47, 3)
                .append(longitude)
                .append(latitude)
                .append(country)
                .append(city)
                .toHashCode();
    }
}
