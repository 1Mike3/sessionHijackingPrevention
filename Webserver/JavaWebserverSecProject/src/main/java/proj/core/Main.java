package proj.core;
import io.javalin.Javalin;
import org.slf4j.Logger;

public class Main {
    public static void main(String[] args) {

        //TODO insert Parameters from Enum into Code

        //Setup Logger for Logging custom Messages
        org.slf4j.ILoggerFactory loggerFactory = org.slf4j.LoggerFactory.getILoggerFactory();
        Logger logger = loggerFactory.getLogger(Main.class.getName());

        //PARAM server address and port
        String address = "localhost";
        int port = 3000;

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/static"); //PARAM file dir
        }).start(address, port);

        //Redirect root to starter page
        app.get("/", ctx -> {
            ctx.redirect("/index.html");
        });

        /*
        //Check before every request , OPT, see if needed (only if not 404)
        app.beforeMatched(ctx -> {
            // runs before all matched requests (including static files)
        });
        */

        //Check before or after  request (rem. path for every request)
        app.before("/path/*", ctx -> {
            // runs before request to /path/*
        });
        //app.after("/path/*", ctx -> {
        app.after( ctx -> {
            // runs after request to /path/*
            logger.atDebug().log("Request to: " + ctx.path());
        });
        //react to recieved login form
        app.post("/login", ctx -> {
            // some code
            logger.atDebug().log("Login form submitted");
            ctx.result("{ \"Data\" :\"data\" }").status(200);
        });
    }
}