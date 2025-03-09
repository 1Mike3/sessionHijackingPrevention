package proj.core.fingerprinting;

import io.ipgeolocation.api.Geolocation;
import io.ipgeolocation.api.GeolocationParams;
import io.ipgeolocation.api.IPGeolocationAPI;
import org.slf4j.LoggerFactory;
import proj.entities.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.FileSystemException;


public class GeolocationProcessing {

    private final String ApiKeyPath;
    private String ApiKey;
    private final org.slf4j.Logger logger;

    /**
     * Constructor
     * Only takes path that is needed to obtain the Key from the Filesystem
     * @param ApiKeyPath
     */
    public GeolocationProcessing(String ApiKeyPath){
        this.ApiKeyPath = ApiKeyPath;
        this.ApiKey = null;
        this.logger = LoggerFactory.getLogger(GeolocationProcessing.class);
    }

    /**
     * Reads the API Key from the file, throws exceptions if error occurs
     * @throws FileNotFoundException
     * @throws FileSystemException
     */
    public void obtainKey() throws FileNotFoundException, FileSystemException {
        File f = new File(ApiKeyPath);
        if(f.exists() && !f.isDirectory()) {
    //Read the API Key from the file throws exceptions if error occurs
           try ( FileReader fr = new FileReader(f)){
               //Actual Reading of the Key
               char[] buf = new char[100];
                fr.read(buf);
                ApiKey = new String(buf);
                ApiKey = ApiKey.trim();
                //Error cases
                if(ApiKey.isEmpty()){
                    throw new FileSystemException("Error reading file");
                }
               //logger.atDebug().log("API Key obtained: " + ApiKey);
              } catch (Exception e){
                throw new FileSystemException("Error reading file");
           }
        } else {
            throw new FileNotFoundException("Error File not found");
        }
    };


/**
     * Gets the coordinates from an IP address
     * Uses the API Key obtained from the file
     * @param ip
 */
    public Location getCoordinates(String ip){
        //init api
        IPGeolocationAPI api = new IPGeolocationAPI(ApiKey);
        //prep params
        GeolocationParams.GeolocationParamsBuilder builder = GeolocationParams.builder();
        builder.withIPAddress(ip);
        builder.withLang("en");
        GeolocationParams params = builder.build();
        //Make Request
        Geolocation geolocation = api.getGeolocation(params);
        //Process Response
        if (geolocation != null) {
            //dbg info
            logger.trace("~~Geolocation RESOLUTION:");
            logger.trace("~LON: "+geolocation.getLatitude().toString());
            logger.trace("~LAT" + geolocation.getLongitude().toString());
            logger.trace("~Country: "+geolocation.getCountryName());
            logger.trace("~City: "+geolocation.getCity());
            //return Location val
            return new Location(geolocation.getLatitude(),geolocation.getLongitude());
        } else {
            logger.error("Error obtaining Geolocation");
            return null;
        }
    };


}
