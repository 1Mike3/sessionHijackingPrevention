package proj.core;
import io.javalin.Javalin;

/**
 * Class to handle requests
 + * @param app instance of Javalin "app" to handle requests
 + * @param logger instance of Logger to log custom messages inherited from Main
 */
public class RequestHandler {

    //instance of Javalin "app" to handle requests
    private Javalin app;
    //instance of Logger to log custom messages inherited from Main
    private org.slf4j.Logger logger;

    //constructor
    public RequestHandler(Javalin app, org.slf4j.Logger logger) {
        this.app = app;
        this.logger = logger;
    }

    //Method which sets up the request handlers
    public void handleRequests() {
        //Redirect root to starter page
        app.get("/", ctx -> {
            ctx.redirect("/index.html");
        });

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


   /*
        //Check before every request , OPT, see if needed (only if not 404)
        app.beforeMatched(ctx -> {
            // runs before all matched requests (including static files)
        });
        */
