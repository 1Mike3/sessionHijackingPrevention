package proj.core;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proj.config.ConfigManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import io.javalin.community.ssl.SslPlugin;
import proj.core.web.GeolocationProcessing;
import proj.core.web.RequestHandler;

/**
 * Main class to start the application
 **/
public class Main {
    public static void main(String[] args) {
        //Setup Logger
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        Logger logger = loggerFactory.getLogger(Main.class.getName());
        logger.info("\n\n\n\n###Startup### \n -- Logger initialized#");

        //Setup Access to config
        //Config fetched from
        ConfigManager cfg = ConfigManager.getInstance();
        if(cfg == null){
            logger.error("Failed to fetch Config Data, SHUTTING DOWN");
            printDebugOnDevice(logger, null);
            System.exit(1);
        } else {
          //cfg.printActiveConfiguration();
            logger.trace("#Startup Config Data fetched Successfully");
        }



            //These two are not strictly necessary but i want to control where the instance is created
        //Setup UserManagementSystem
        UserManagementSystem ums = UserManagementSystem.getInstance();
        logger.info("#Startup UserManagementSystem initialized");
        //Setup SessionManagementSystem
        SessionManagementSystem sms = SessionManagementSystem.getInstance();
        logger.info("#Startup SessionManagementSystem initialized");

        //Get server address and port from Parameters, conditional http/https
        String address;
        int port;
        //NOTE
        //HTTPS was added previously for testing but is for now handled by nginx => "secure" stuff is obsolete
        if(!cfg.isHTTPS()){
        address = cfg.getADDRESS();
        port = cfg.getPORT();
        }else{
        address = cfg.getADDRESS_SECURE();
        port = cfg.getPORT_SECURE();
        }

        //Start Javalin app
        //Orientation https://github.com/javalin/javalin/blob/master/javalin/src/test/java/io/javalin/examples/HelloWorldSecure.java
        Javalin app;
        if(!cfg.isHTTPS()){
            //HTTP only setup
            logger.info("Application running using HTTP");
             app = Javalin.create(config -> {
                config.staticFiles.add(cfg.getPATH_WS_STATIC(), Location.CLASSPATH); // Serve from the 'static' directory within resources
            }).start(address, port);
        }else {
            //HTTPS setup
            logger.info("Application running using HTTPS");
            String f1,f2;
            if (!cfg.isON_DEVICE()){
                f1 = cfg.getPATH_RELATIVE_CERTIFICATE();
                f2 = cfg.getPATH_RELATIVE_PRIVATE_KEY();
            }else {
                f1 = cfg.getPATH_RELATIVE_CERTIFICATE_ON_DEVICE();
                f2 = cfg.getPATH_RELATIVE_PRIVATE_KEY_ON_DEVICE();
            }
            SslPlugin plugin = new SslPlugin(conf -> {
                conf.pemFromPath(f1, f2);
            });
            app = Javalin.create(javalinConfig -> {
                javalinConfig.registerPlugin(plugin);
                javalinConfig.staticFiles.add(cfg.getPATH_WS_STATIC(), Location.CLASSPATH); // Serve from the 'static' directory within resources
            }).start(address, port);
        }

        //Setup RequestHandler
        RequestHandler requestHandler = new RequestHandler(app);
        requestHandler.handleRequests();
        logger.info("#Startup RequestHandler initialized");


        //Creating Geolocation-Management Instance
        GeolocationProcessing geolocation;
        if(!cfg.isON_DEVICE()){
            geolocation = new GeolocationProcessing(cfg.getPATH_DB_API_KEY());
        }else {
            geolocation = new GeolocationProcessing(cfg.getPATH_DB_API_KEY_ON_DEVICE());
        }
        //Obtaining the API Key
        try {
            geolocation.obtainKey();
        } catch (Exception e) {
            logger.error("Error obtaining API Key - Geolocation: " + e.getMessage());
            printDebugOnDevice(logger, ums);
            System.exit(1);
        }
        //Test Request
        //proj.entities.Location loc = geolocation.getCoordinates("8.8.8.8");
        //logger.trace(loc.toString());

        //logger.info("---------########## Application Running ##########---------");
        logger.info(
                """
\n
                          ___                 _  _               _    _                 _____  _                 _              _   _   \s
                         / _ \\               | |(_)             | |  (_)               /  ___|| |               | |            | | | |  \s
                        / /_\\ \\ _ __   _ __  | | _   ___   __ _ | |_  _   ___   _ __   \\ `--. | |_   __ _  _ __ | |_   ___   __| | | |  \s
                        |  _  || '_ \\ | '_ \\ | || | / __| / _` || __|| | / _ \\ | '_ \\   `--. \\| __| / _` || '__|| __| / _ \\ / _` | | |  \s
                        | | | || |_) || |_) || || || (__ | (_| || |_ | || (_) || | | | /\\__/ /| |_ | (_| || |   | |_ |  __/| (_| | |_|  \s
                        \\_| |_/| .__/ | .__/ |_||_| \\___| \\__,_| \\__||_| \\___/ |_| |_| \\____/  \\__| \\__,_||_|    \\__| \\___| \\__,_| (_)  \s
                               | |    | |                                                                                               \s
                               |_|    |_|                                                                                               \s                                                                                                                                                                                                                         \s                                                               \s
\n
                """
        );

        //printDebugOnDevice(logger, ums);
    }


    /**
     * Prints debug information for debugging path errors when running on other devices
     * NO practical function other than debugging
     * @param logger the loger for printing the debug messages
     * @param ums instance of UserManagementSystem to be analyzed
     */
    public static void printDebugOnDevice(Logger logger, UserManagementSystem ums){
    //EXTRA LOGGING Data for diagnosing problems when executing jar on other device
    logger.trace("DEBUG ON DEVICE");
    Path currentRelativePath = Paths.get("");
    String s = currentRelativePath.toAbsolutePath().toString();
    logger.trace("Current absolute path is: \n" + s);
    logger.trace("Working Directory getProperty: \n"+System.getProperty("user.dir"));
    if (ums != null)
        logger.trace("Session Data loaded: \n" + ums.getAllUsersAsString());
    else
        logger.trace("Session Data not loaded so not showing users list \n");
}
}

