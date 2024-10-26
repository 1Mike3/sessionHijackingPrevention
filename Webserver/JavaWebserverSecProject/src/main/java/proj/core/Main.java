package proj.core;
import io.javalin.Javalin;
import org.slf4j.Logger;
import proj.config.Parameters;


/**
 * Main class to start the application
 **/
public class Main {
    public static void main(String[] args) {

        //TODO insert Parameters from Enum into Code

        //Setup Logger for Logging custom Messages
        org.slf4j.ILoggerFactory loggerFactory = org.slf4j.LoggerFactory.getILoggerFactory();
        Logger logger = loggerFactory.getLogger(Main.class.getName());

        //PARAM server address and port
        String address = Parameters.ADDRESS.getValue().toString();
        int port = (int) Parameters.PORT.getValue();

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/static"); //PARAM file dir
        }).start(address, port);

        //instance of RequestHandler to handle requests
        RequestHandler requestHandler = new RequestHandler(app, logger);
        requestHandler.handleRequests();


    }
}