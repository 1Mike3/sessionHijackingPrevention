package proj.core;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import proj.config.ConfigManager;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import io.javalin.community.ssl.SslPlugin;

/**
 * Main class to start the application
 **/
public class Main {
    public static void main(String[] args) {

        //Setup Logger
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        Logger logger = loggerFactory.getLogger(Main.class.getName());
        logger.info("\n\n\n\n###Startup### -- Logger initialized#");


        //Setup Access to config
        //Config fetched from
        ConfigManager cfg = ConfigManager.getInstance();
        if(cfg == null){
            logger.error("Failed to fetch Config Data, SHUTTING DOWN");
            System.exit(1);
        }


            //These two are not strictly necessary but i want to control where the instance is created
        //Setup UserManagementSystem
        UserManagementSystem ums = UserManagementSystem.getInstance();
        //logger.info().log("#Startup UserManagementSystem initialized#");
        //Setup SessionManagementSystem
        SessionManagementSystem sms = SessionManagementSystem.getInstance();
       // logger.info().log("#Startup SessionManagementSystem initialized#");


        //Get server address and port from Parameters, conditional http/https
        String address;
        int port;
        if(!cfg.isHTTPS()){
        address = cfg.getADDRESS();
        port = cfg.getPORT();
        }else{
        address = cfg.getADDRESS_SECURE();
        port = cfg.getPORT_SECURE();
        }

        //Start Javalin app
        //Example from Javalin repo for https: https://github.com/javalin/javalin/blob/master/javalin/src/test/java/io/javalin/examples/HelloWorldSecure.java
        Javalin app;
        if(!cfg.isHTTPS()){
            logger.info("Application running using HTTP");
             app = Javalin.create(config -> {
                config.staticFiles.add(cfg.getPATH_WS_STATIC(), Location.CLASSPATH); // Serve from the 'static' directory within resources
            }).start(address, port);
        }else {
            logger.info("Application running using HTTPS");
            //Simpler Approach
            File f1 = new File(cfg.getPATH_RELATIVE_CERTIFICATE());
            File f2 = new File(cfg.getPATH_RELATIVE_PRIVATE_KEY());
            //Check if files exist
            if(f1.exists() && f2.exists()){
                logger.info("Certificate files regular path exist");
            }else {
                f1 = new File(cfg.getPATH_RELATIVE_CERTIFICATE_ON_DEVICE());
                f2 = new File(cfg.getPATH_RELATIVE_PRIVATE_KEY_ON_DEVICE());
                if(f1.exists() && f2.exists()){
                    logger.info("Certificate files on device path exist");
                }else{
                    logger.error("Error no valid certificate files found");
                    System.exit(1);
                }
                logger.error("Certificate files do not exist");
                System.exit(1);
            }
            SslPlugin plugin = new SslPlugin(conf -> {
                conf.pemFromPath("src/main/resources/certsTest/sec.test.crt", "src/main/resources/certsTest/sec.test.key");
            });
            app = Javalin.create(javalinConfig -> {
                javalinConfig.registerPlugin(plugin);
                javalinConfig.staticFiles.add(cfg.getPATH_WS_STATIC(), Location.CLASSPATH); // Serve from the 'static' directory within resources
            }).start(address, port);
        }


        //Setup RequestHandler
        RequestHandler requestHandler = new RequestHandler(app);
        requestHandler.handleRequests();
       // logger.info().log("#Startup RequestHandler initialized#");
        logger.info("---------########## Application Running ##########---------");
        //EXTRA LOGGING Data for diagnosing problems when executing jar on other device
        logger.info("DEBUG ON DEVICE");
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current absolute path is: \n" + s);
        logger.info("Working Directory getProperty: \n"+System.getProperty("user.dir"));
        logger.info("Session Data loaded: \n" + ums.getUsers().toString());

    }
/* //Untested testing simpler approach first
    // For setting up the connection with https
    private static SslContextFactory.Server getSslContextFactory() {
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(Main.class.getResource("/keystore.jks").toExternalForm());
        sslContextFactory.setKeyStorePassword("password");
        return sslContextFactory;
    }
    */
}