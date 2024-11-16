package proj.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import proj.core.Main;
import proj.util.JSON_Deserialize;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Replaces previous ENUM "Parameters"
 * For a better manageability of the server the configuration data is now loaded into the program on startup from
 * config .json
 * Partly written by a generative AI to make the switch easier
 */
@Getter
@Setter
public class ConfigManager {

    private boolean HTTPS;
    private String ADDRESS;
    private String ADDRESS_SECURE;
    private int PORT;
    private int PORT_SECURE;
    private String PATH_WS_STATIC;
    private String PATH_RELATIVE_USER_DB;
    private String PATH_RELATIVE_USER_DB_ON_DEVICE;
    private String PATH_RELATIVE_USERSPACE_HTML;
    private static ConfigManager instance;

    //Fasterxml constructor
    public ConfigManager(@JsonProperty("HTTPS") boolean HTTPS,
                         @JsonProperty("ADDRESS") String ADDRESS,
                         @JsonProperty("ADDRESS_SECURE") String ADDRESS_SECURE,
                         @JsonProperty("PORT") int PORT,
                         @JsonProperty("PORT_SECURE") int PORT_SECURE,
                         @JsonProperty("PATH_WS_STATIC") String PATH_WS_STATIC,
                         @JsonProperty("PATH_RELATIVE_USER_DB") String PATH_RELATIVE_USER_DB,
                         @JsonProperty("PATH_RELATIVE_USER_DB_ON_DEVICE") String PATH_RELATIVE_USER_DB_ON_DEVICE,
                         @JsonProperty("PATH_RELATIVE_USERSPACE_HTML") String PATH_RELATIVE_USERSPACE_HTML) {
        this.HTTPS = HTTPS;
        this.ADDRESS = ADDRESS;
        this.ADDRESS_SECURE = ADDRESS_SECURE;
        this.PORT = PORT;
        this.PORT_SECURE = PORT_SECURE;
        this.PATH_WS_STATIC = PATH_WS_STATIC;
        this.PATH_RELATIVE_USER_DB = PATH_RELATIVE_USER_DB;
        this.PATH_RELATIVE_USER_DB_ON_DEVICE = PATH_RELATIVE_USER_DB_ON_DEVICE;
        this.PATH_RELATIVE_USERSPACE_HTML = PATH_RELATIVE_USERSPACE_HTML;
    }


    public static synchronized ConfigManager getInstance() {

        if (instance == null) {
            try {
                //... Path could be passed as parameter to jar ... lets just leave it like that for now
                String json = new String(Files.readAllBytes(Paths.get("./src/main/resources/config/config.json")));
                instance = JSON_Deserialize.deserialize(json, new TypeReference<ConfigManager>() {
                });
                return instance;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return instance;
        }
    }


    public static void main(String[] args) {
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        Logger logger = loggerFactory.getLogger(Main.class.getName());
       try {


           // Read the file content into a string
           String json = new String(Files.readAllBytes(Paths.get("./src/main/resources/config/config.json")));

           // Deserialize the JSON string into a Config object
           ConfigManager config = JSON_Deserialize.deserialize(json, new TypeReference<ConfigManager>() {});
           // Access the parsed fields
           System.out.println("HTTPS: "+config.isHTTPS());
           System.out.println("ADDRESS: " + config.getADDRESS());
           System.out.println("PORT: " + config.getPORT());
           // Add other field accesses as needed

       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}
