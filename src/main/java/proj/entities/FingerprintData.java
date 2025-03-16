package proj.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * - Class to store fingerprint data of a particular user
 * - Is written to- and read from the database
 * - Contains other entity classes
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FingerprintData {
    //Foreign Key of User to associate the two tables
    //Not really used for anything, value only injected in dbUpdateUserByName method of dms
    private Integer blockId = null;
    //Careful, X-Ip not proxy Ip
    private String IP;
    private String accept;
    private String encoding;
    private Location location;
    //private float longitude;
    //private float latitude;
    private String screen;
    private String language;
    private String timezone;
    //private String userAgent;
    private UserAgent userAgent;
    private String canvas;
    private String webglVendor;
    private String webglRenderer;
    private String deviceMemory;

    private boolean cookiesAccepted;


    //For Debug prints
    @Override
    public String toString() {
        return "\nFingerprintData{" +"\n" +
                "blockId=" + blockId + "\n" +
                ", IP='" + IP + "\n" +
                ", accept='" + accept + "\n" +
                ", encoding='" + encoding + "\n" +
                ", location=" + location +
                ", screen='" + screen + "\n" +
                ", language='" + language + "\n" +
                ", timezone='" + timezone + "\n" +
                ", userAgent=" + userAgent +
                ", canvas='" + canvas + "\n" +
                ", webglVendor='" + webglVendor + "\n" +
                ", webglRenderer='" + webglRenderer + "\n" +
                ", DeviceMemory='" + deviceMemory + "\n" +
                ", cookiesAccepted=" + cookiesAccepted +"\n" +
                '}' + "\n" ;
    }
}

