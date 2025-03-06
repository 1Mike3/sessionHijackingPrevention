package proj.core.fingerprinting.comparators;

import lombok.Setter;
import proj.entities.Location;

/**
 * Custom implementaion instead of DefaultStringComparator,
 * Max distance between old and new request is set in the Configuration Manager
 */
public class Cmp3_Geolocation implements FpComparator {
    private static final double EARTH_RADIUS = 6371.0;
    @Setter
    //Value in km is written from the Configuration Manager
    private static double MAX_ACCEPTABLE_DISTANCE_KM = 0;
    @Override
    public boolean compare(Object oldV, Object newV){
        //https://www.baeldung.com/java-find-distance-between-points
        //Taking the less accurate but faster method as speed is critical for a response to the user, and the
        //ip geolocation in itself is not very accurate anyway
        if (MAX_ACCEPTABLE_DISTANCE_KM == 0 ){
            throw new RuntimeException("Geolocation Comparator: Max Acceptable Distance not set");
        }
        //Equirectangular Distance Approximation (Earth is perfect sphere)
        Location oldLocation = (Location) oldV;
        Location newLocation = (Location) newV;
        double latOldRad = Math.toRadians(oldLocation.getLatitude().doubleValue());
        double latNewRad = Math.toRadians(newLocation.getLatitude().doubleValue());
        double lonOldRad = Math.toRadians(oldLocation.getLongitude().doubleValue());
        double lonNewRad = Math.toRadians(newLocation.getLongitude().doubleValue());
        double x = (lonNewRad - lonOldRad) * Math.cos((latOldRad + latNewRad) / 2);
        double y = (latNewRad - latOldRad);
        double distanceKm = (Math.sqrt(x * x + y * y) * EARTH_RADIUS);
        return distanceKm <= MAX_ACCEPTABLE_DISTANCE_KM;
    }

    //Written so Comparision can also be used outside for debugging and evaluation
    public double compareReturnValue(Object oldV, Object newV){
        if (MAX_ACCEPTABLE_DISTANCE_KM == 0 ){
            throw new RuntimeException("Geolocation Comparator: Max Acceptable Distance not set");
        }
        Location oldLocation = (Location) oldV;
        Location newLocation = (Location) newV;
        double latOldRad = Math.toRadians(oldLocation.getLatitude().doubleValue());
        double latNewRad = Math.toRadians(newLocation.getLatitude().doubleValue());
        double lonOldRad = Math.toRadians(oldLocation.getLongitude().doubleValue());
        double lonNewRad = Math.toRadians(newLocation.getLongitude().doubleValue());
        double x = (lonNewRad - lonOldRad) * Math.cos((latOldRad + latNewRad) / 2);
        double y = (latNewRad - latOldRad);
        return (Math.sqrt(x * x + y * y) * EARTH_RADIUS);
    }
}
