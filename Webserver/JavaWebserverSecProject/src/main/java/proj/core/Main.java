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
            printDebugOnDevice(logger, null);
            System.exit(1);
        }else {
            cfg.printActiveConfiguration();
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
       // logger.info().log("#Startup RequestHandler initialized#");
        logger.info("---------########## Application Running ##########---------");
        printDebugOnDevice(logger, ums);

    }

public static void printDebugOnDevice(Logger logger, UserManagementSystem ums){
    //EXTRA LOGGING Data for diagnosing problems when executing jar on other device
    logger.info("DEBUG ON DEVICE");
    Path currentRelativePath = Paths.get("");
    String s = currentRelativePath.toAbsolutePath().toString();
    System.out.println("Current absolute path is: \n" + s);
    logger.info("Working Directory getProperty: \n"+System.getProperty("user.dir"));
    if (ums != null)
        logger.info("Session Data loaded: \n" + ums.getUsers().toString());
    else
        logger.info("Session Data not loaded so not showing users list \n");
}
}

