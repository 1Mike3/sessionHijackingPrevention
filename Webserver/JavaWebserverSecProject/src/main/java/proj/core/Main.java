package proj.core;
import io.javalin.Javalin;
import org.slf4j.Logger;
import proj.config.Parameters;


/**
 * Main class to start the application
 **/
public class Main {
    public static void main(String[] args) {

        //Setup Logger
        org.slf4j.ILoggerFactory loggerFactory = org.slf4j.LoggerFactory.getILoggerFactory();
        Logger logger = loggerFactory.getLogger(Main.class.getName());
        logger.atInfo().log("#Startup Logger initialized#");

        //Setup UserManagementSystem
        UserManagementSystem ums = UserManagementSystem.getInstance();
        logger.atInfo().log("#Startup UserManagementSystem initialized#");

        //Setup SessionManagementSystem
        SessionManagementSystem sms = SessionManagementSystem.getInstance();
        logger.atInfo().log("#Startup SessionManagementSystem initialized#");

        //PARAM server address and port
        String address = Parameters.ADDRESS.getValue().toString();
        int port = (int) Parameters.PORT.getValue();

        //Start Javalin app
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/static"); //PARAM file dir
        }).start(address, port);
        logger.atInfo().log("#Startup Javalin app started#");

        //Setup RequestHandler
        RequestHandler requestHandler = new RequestHandler(app);
        requestHandler.handleRequests();
        logger.atInfo().log("#Startup RequestHandler initialized#");
        logger.info("---------########## Application Running ##########---------");

    }
}