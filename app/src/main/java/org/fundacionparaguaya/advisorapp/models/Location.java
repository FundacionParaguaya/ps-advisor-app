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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (Double.compare(location.getLongitude(), getLongitude()) != 0) return false;
        if (Double.compare(location.getLatitude(), getLatitude()) != 0) return false;
        if (getCity() != null ? !getCity().equals(location.getCity()) : location.getCity() != null)
            return false;
        return getCountry() != null ? getCountry().equals(location.getCountry()) : location.getCountry() == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(getLongitude());
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLatitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (getCity() != null ? getCity().hashCode() : 0);
        result = 31 * result + (getCountry() != null ? getCountry().hashCode() : 0);
        return result;
    }
}
