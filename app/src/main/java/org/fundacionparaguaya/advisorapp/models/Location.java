package org.fundacionparaguaya.advisorapp.models;

import android.arch.persistence.room.Embedded;

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
}
