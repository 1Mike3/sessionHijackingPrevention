package proj.core;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import proj.config.ConfigManager;

import java.nio.file.Path;
import java.nio.file.Paths;

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

        //PARAM server address and port
        String address = cfg.getADDRESS();
        int port = cfg.getPORT();

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
            app = Javalin.create(config -> {
                config.jetty.addConnector((server, httpConfiguration) -> {
                    ServerConnector sslConnector = new ServerConnector(server, getSslContextFactory());
                    sslConnector.setPort(443);
                    return sslConnector;
                });
                config.jetty.addConnector((server, httpConfiguration) -> {
                    ServerConnector connector = new ServerConnector(server);
                    connector.setPort(80);
                    return connector;
                });

            }).start().get("/", ctx -> ctx.result("Hello World")); // valid endpoint for both connectors
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

    // For setting up the connection with https
    private static SslContextFactory.Server getSslContextFactory() {
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(Main.class.getResource("/keystore.jks").toExternalForm());
        sslContextFactory.setKeyStorePassword("password");
        return sslContextFactory;
    }
}