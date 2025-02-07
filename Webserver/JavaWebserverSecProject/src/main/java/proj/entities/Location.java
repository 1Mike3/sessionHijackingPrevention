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
    private final BigDecimal longitude;
    private final BigDecimal latitude;

    /**
     * Constructor
     * @param longitude longitude
     * @param latitude latitude
     */
    public Location(BigDecimal longitude, BigDecimal latitude) {
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
