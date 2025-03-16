package proj.entities;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Represents a location with longitude and latitude as returned by API call
 */
@Getter
@Setter
public final class Location {
    //BigDecimal is used as this is the RetVal of the API call
    private final BigDecimal longitude;
    private final BigDecimal latitude;


    //Previously switched order of longitude and latitude, switched back to correct order
    /**
     * Constructor
     * @param latitude latitude
     * @param longitude longitude
     *
     */

    //Were in wrong order for convention, switched back
    public Location(BigDecimal latitude,  BigDecimal longitude ) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Location{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
