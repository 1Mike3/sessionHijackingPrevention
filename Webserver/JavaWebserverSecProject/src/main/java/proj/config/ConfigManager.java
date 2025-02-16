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
 * Class to manage the configuration of the application, values that should be changeable without recompiling the code
 * Replaces previous ENUM "Parameters"
 * For a better manageability on the server the configuration data is now loaded into the program on startup from
 * config .json
 */
@Getter
@Setter
public class ConfigManager {

    private boolean ON_DEVICE;
    private boolean HTTPS;
    private String ADDRESS;
    private String ADDRESS_SECURE;
    private int PORT;
    private int PORT_SECURE;
    private String PATH_WS_STATIC;
    private boolean DB_WEBSERVER_ENABLED;
    private String FINGERPRINT_SENSITIVITY;
    private String PATH_RELATIVE_USER_DB;
    private String PATH_RELATIVE_USER_DB_ON_DEVICE;
    private String PATH_RELATIVE_CERTIFICATE;
    private String PATH_RELATIVE_CERTIFICATE_ON_DEVICE;
    private String PATH_RELATIVE_PRIVATE_KEY;
    private String PATH_RELATIVE_PRIVATE_KEY_ON_DEVICE;
    private String PATH_RELATIVE_USERSPACE_HTML;
    private String PATH_RELATIVE_USERSPACE_HTML_ON_DEVICE;
    private String PATH_DB_API_KEY;
    private String PATH_DB_API_KEY_ON_DEVICE;

    private static ConfigManager instance;

    //Fasterxml constructor
    public ConfigManager(
                        @JsonProperty("ON_DEVICE") boolean ON_DEVICE,
                        @JsonProperty("HTTPS") boolean HTTPS,
                         @JsonProperty("ADDRESS") String ADDRESS,
                         @JsonProperty("ADDRESS_SECURE") String ADDRESS_SECURE,
                         @JsonProperty("PORT") int PORT,
                         @JsonProperty("PORT_SECURE") int PORT_SECURE,
                         @JsonProperty("PATH_WS_STATIC") String PATH_WS_STATIC,
                         @JsonProperty("DB_WEBSERVER_ENABLED") boolean DB_WEBSERVER_ENABLED,
                         @JsonProperty("FINGERPRINT_SENSITIVITY") String FINGERPRINT_SENSITIVITY,
                         @JsonProperty("PATH_RELATIVE_USER_DB") String PATH_RELATIVE_USER_DB,
                         @JsonProperty("PATH_RELATIVE_USER_DB_ON_DEVICE") String PATH_RELATIVE_USER_DB_ON_DEVICE,
                         @JsonProperty("PATH_RELATIVE_CERTIFICATE") String PATH_RELATIVE_CERTIFICATE,
                         @JsonProperty("PATH_RELATIVE_CERTIFICATE_ON_DEVICE") String PATH_RELATIVE_CERTIFICATE_ON_DEVICE,
                         @JsonProperty("PATH_RELATIVE_PRIVATE_KEY") String PATH_RELATIVE_PRIVATE_KEY,
                         @JsonProperty("PATH_RELATIVE_PRIVATE_KEY_ON_DEVICE") String PATH_RELATIVE_PRIVATE_KEY_ON_DEVICE,
                         @JsonProperty("PATH_RELATIVE_USERSPACE_HTML") String PATH_RELATIVE_USERSPACE_HTML,
                         @JsonProperty("PATH_RELATIVE_USERSPACE_HTML_ON_DEVICE") String PATH_RELATIVE_USERSPACE_HTML_ON_DEVICE,
                        @JsonProperty("PATH_DB_API_KEY") String PATH_DB_API_KEY,
                        @JsonProperty("PATH_DB_API_KEY_ON_DEVICE") String PATH_DB_API_KEY_ON_DEVICE)
    {
        this.ON_DEVICE = ON_DEVICE;
        this.HTTPS = HTTPS;
        this.ADDRESS = ADDRESS;
        this.ADDRESS_SECURE = ADDRESS_SECURE;
        this.PORT = PORT;
        this.PORT_SECURE = PORT_SECURE;
        this.PATH_WS_STATIC = PATH_WS_STATIC;
        this.DB_WEBSERVER_ENABLED = DB_WEBSERVER_ENABLED;
        this.FINGERPRINT_SENSITIVITY = FINGERPRINT_SENSITIVITY;
        this.PATH_RELATIVE_USER_DB = PATH_RELATIVE_USER_DB;
        this.PATH_RELATIVE_USER_DB_ON_DEVICE = PATH_RELATIVE_USER_DB_ON_DEVICE;
        this.PATH_RELATIVE_CERTIFICATE = PATH_RELATIVE_CERTIFICATE;
        this.PATH_RELATIVE_CERTIFICATE_ON_DEVICE = PATH_RELATIVE_CERTIFICATE_ON_DEVICE;
        this.PATH_RELATIVE_PRIVATE_KEY = PATH_RELATIVE_PRIVATE_KEY;
        this.PATH_RELATIVE_PRIVATE_KEY_ON_DEVICE = PATH_RELATIVE_PRIVATE_KEY_ON_DEVICE;
        this.PATH_RELATIVE_USERSPACE_HTML = PATH_RELATIVE_USERSPACE_HTML;
        this.PATH_RELATIVE_USERSPACE_HTML_ON_DEVICE = PATH_RELATIVE_USERSPACE_HTML_ON_DEVICE;
        this.PATH_DB_API_KEY = PATH_DB_API_KEY;
        this.PATH_DB_API_KEY_ON_DEVICE = PATH_DB_API_KEY_ON_DEVICE;
    }


    public static synchronized ConfigManager getInstance() {

        if (instance == null) {
            try {
                //... Path could be passed as parameter to jar ... lets just leave it like that for now
                //CONFIGURABLE
                    //Used during Testing
                final String pathConfigFileTestPARAM = "./src/main/resources/config/config.json";
                    //Used on Device on which the application will be run
                final String pathConfigFileDevicePARAM = "./target/classes/config/config.json";

                String json;
                if(Files.exists(Paths.get(pathConfigFileTestPARAM))){
                    json = new String(Files.readAllBytes(Paths.get(pathConfigFileTestPARAM)));
                    System.out.println("Test Path exists and is used");
                    //Check if prod path exists
                } else {
                    json = new String(Files.readAllBytes(Paths.get(pathConfigFileDevicePARAM)));
                }

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

   //Only used for debugging
   public void printActiveConfiguration(){
         System.out.println("###Active Configuration###");
         System.out.println("ON_DEVICE: "+this.isON_DEVICE());
         System.out.println("HTTPS: "+this.isHTTPS());
         System.out.println("ADDRESS: " + this.getADDRESS());
         System.out.println("ADDRESS_SEC: " + this.getADDRESS_SECURE());
         System.out.println("PORT: " + this.getPORT());
         System.out.println("PORT_SEC: " + this.getPORT_SECURE());
         System.out.println("PATH_WS_STATIC: " + this.getPATH_WS_STATIC());
         System.out.println("DB_WEBSERVER_ENABLED: " + this.isDB_WEBSERVER_ENABLED());
         System.out.println("FINGERPRINT_SENSITIVITY: " + this.getFINGERPRINT_SENSITIVITY());
         System.out.println("PATH_RELATIVE_USER_DB: " + this.getPATH_RELATIVE_USER_DB());
         System.out.println("PATH_RELATIVE_USER_DB_ON_DEVICE: " + this.getPATH_RELATIVE_USER_DB_ON_DEVICE());
         System.out.println("PATH_RELATIVE_CERTIFICATE: " + this.getPATH_RELATIVE_CERTIFICATE());
         System.out.println("PATH_RELATIVE_CERTIFICATE_ON_DEVICE: " + this.getPATH_RELATIVE_CERTIFICATE_ON_DEVICE());
         System.out.println("PATH_RELATIVE_PRIVATE_KEY: " + this.getPATH_RELATIVE_PRIVATE_KEY());
         System.out.println("PATH_RELATIVE_PRIVATE_KEY_ON_DEVICE: " + this.getPATH_RELATIVE_PRIVATE_KEY_ON_DEVICE());
         System.out.println("PATH_RELATIVE_USERSPACE_HTML: " + this.getPATH_RELATIVE_USERSPACE_HTML());
         System.out.println("PATH_RELATIVE_USERSPACE_HTML_ON_DEVICE: " + this.getPATH_RELATIVE_USERSPACE_HTML_ON_DEVICE());
         System.out.println("\n");
    }

}
