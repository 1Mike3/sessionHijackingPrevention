package proj.core;

import org.junit.jupiter.api.Test;
import proj.core.fingerprinting.comparators.Cmp3_Geolocation;
import proj.entities.Location;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

public class GeolocationComparatorTest {
    double testVal = 40.95;
    @Test
    public void testKnownCoordinates(){
        //Test Locations
        //Vienna
        // 48°12'29.5"N 16°22'25.7"E
        //Gänserndorf (40km weg)
        //  48°20'32.9"N 16°43'13.7"E
        //  48.342467 , 16.720471
        //Berlin
        //   52°31'12.0"N 13°24'18.0"E
        //Paris
        //  48°51'23.8"N 2°21'07.9"E
        //London
        //  51°30'26.6"N 0°07'40.1"E
        //Rome
        //    41°54'10.1"N 12°29'47.0"E
        //New York
        //   40°42'46.1"N 74°00'21.6"W
        //Los Angeles
        //   34°03'07.9"N 118°14'37.3"W
        //Vienna  //lat long
        Location vienna = new Location(new BigDecimal(48.2082), new BigDecimal(16.3738));
        //Gänserndorf
        Location gaenserndorf = new Location(new BigDecimal(48.342467), new BigDecimal(16.720471));
        //Berlin
        Location berlin = new Location(new BigDecimal(52.5200), new BigDecimal(13.4050));
        //Paris
        Location paris = new Location(new BigDecimal(48.8566), new BigDecimal(2.3522));
        //London
        Location london = new Location(new BigDecimal(51.5074), new BigDecimal(0.1278));
        //Rome
        Location rome = new Location(new BigDecimal(41.9028), new BigDecimal(12.4964));
        //New York
        Location newYork = new Location(new BigDecimal(40.7128), new BigDecimal(-74.0060));
        //Los Angeles
        Location losAngeles = new Location(new BigDecimal(34.0522), new BigDecimal(-118.2437));


        //Calculations provided by https://www.calculator.net/distance-calculator.html
        //Distance Vienna - Gänserndorf
        //40.95 km
        testVal = 41.95;
        Cmp3_Geolocation comparator = new Cmp3_Geolocation();
        comparator.setMAX_ACCEPTABLE_DISTANCE_KM(100.0);
        System.out.println("Distance Vienna - Gänserndorf " + comparator.compareReturnValue(vienna, gaenserndorf) + " km");
        assertTrue(comparator.compareReturnValue(vienna, gaenserndorf) <= (testVal * 1.3) &&
                comparator.compareReturnValue(vienna, gaenserndorf) >= testVal * 0.7);

        //Distance Vienna - Berlin
        //524.0 km
        testVal = 524.0;
        assertTrue(comparator.compareReturnValue(vienna, berlin) <= (testVal * 1.3) &&
                comparator.compareReturnValue(vienna, berlin) >= testVal * 0.7);
        System.out.println("Distance Vienna - Berlin " + comparator.compareReturnValue(vienna, berlin) + " km");
        //Distance Vienna - Paris
        //1036.6 km
        testVal = 1036.6;
        assertTrue(comparator.compareReturnValue(vienna, paris) <= (testVal * 1.3) &&
                comparator.compareReturnValue(vienna, paris) >= testVal * 0.7);
        System.out.println("Distance Vienna - Paris " + comparator.compareReturnValue(vienna, paris) + " km");
        //Distance Vienna - Rome
        //764.1 km
        testVal = 764.1;
        assertTrue(comparator.compareReturnValue(vienna, rome) <= (testVal * 1.3) &&
                comparator.compareReturnValue(vienna, rome) >= testVal * 0.7);
        System.out.println("Distance Vienna - Rome " + comparator.compareReturnValue(vienna, rome) + " km");
        //Distance Vienna - New York
        //6,814.6 km
        testVal = 6814.6;
        assertTrue(comparator.compareReturnValue(vienna, newYork) <= (testVal * 1.3) &&
                comparator.compareReturnValue(vienna, newYork) >= testVal * 0.7);
        System.out.println("Distance Vienna - New York" + comparator.compareReturnValue(vienna, newYork) + " km");
        //Distance Vienna - Los Angeles
        //9,841.5 km
        testVal = 9841.5;
        assertTrue(comparator.compareReturnValue(vienna, losAngeles) <= (testVal * 1.3) &&
                comparator.compareReturnValue(vienna, losAngeles) >= testVal * 0.7);
        System.out.println("Distance Vienna - Los Angeles" + comparator.compareReturnValue(vienna, losAngeles) + " km");
        //Distance London - Rome
        //1,422.5 km
        testVal = 1422.5;
        assertTrue(comparator.compareReturnValue(london, rome) <= (testVal * 1.3) &&
                comparator.compareReturnValue(london, rome) >= testVal * 0.7);
        System.out.println("Distance London - Rome" + comparator.compareReturnValue(london, rome) + " km");
    }

    @Test
    public void testValidDistance(){
        //Vienna  //lat long
        Location vienna = new Location(new BigDecimal(48.2082), new BigDecimal(16.3738));
        //Gänserndorf
        Location gaenserndorf = new Location(new BigDecimal(48.342467), new BigDecimal(16.720471));

        Cmp3_Geolocation comparator = new Cmp3_Geolocation();
        comparator.setMAX_ACCEPTABLE_DISTANCE_KM(100.0);

        assertTrue(comparator.compare(vienna, gaenserndorf));
    }

    @Test
    public void testInvalidDistance(){
        //Vienna  //lat long
        Location vienna = new Location(new BigDecimal(48.2082), new BigDecimal(16.3738));
        Location rome = new Location(new BigDecimal(41.9028), new BigDecimal(12.4964));

        Cmp3_Geolocation comparator = new Cmp3_Geolocation();
        comparator.setMAX_ACCEPTABLE_DISTANCE_KM(100.0);

        assertFalse(comparator.compare(vienna, rome));
    }


}
