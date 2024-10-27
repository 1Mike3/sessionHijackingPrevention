package proj.core.web;
import io.javalin.Javalin;
import org.slf4j.LoggerFactory;
import proj.core.SessionManagementSystem;
import proj.core.UserManagementSystem;


/**
 * Class to handle requests
 * Some of the larger handlers are outsorced to other classes and called here
 + * @param app instance of Javalin "app" to handle requests
 + * @param logger instance of Logger to log custom messages inherited from Main
 */
public class RequestHandler {

    //instance of Javalin "app" to handle requests
    private final Javalin app;
    //instance of Logger to log custom messages inherited from Main
    private final org.slf4j.Logger logger;

    //constructor
    public RequestHandler(Javalin app) {
        this.app = app;
        this.logger = LoggerFactory.getLogger(RequestHandler.class);
    }

    //Method which sets up the request handlers
    public void handleRequests() {
        //get Instances to handle user and session management
        SessionManagementSystem sms = SessionManagementSystem.getInstance();
        UserManagementSystem ums = UserManagementSystem.getInstance();

        //Redirect root to starter page
       // app.get("/", ctx -> {
          //  ctx.redirect("/index.html");
       // });

        //Check before or after  request (rem. path for every request)
        app.before("/path/*", ctx -> {
            // runs before request to /path/*
        });

        //app.after("/path/*", ctx -> {
        app.after( ctx -> {
            // runs after request to /path/*
            logger.atDebug().log("Request to: " + ctx.path());
        });

//++++++++++++++++++++++++++++++++ LOGIN HANDLER ++++++++++++++++++++++++++++++++++++++++++++++++
    LoginHandler.setHandler(app,logger,ums,sms);
//++++++++++++++++++++++++++++++++ LOGOT HANDLER ++++++++++++++++++++++++++++++++++++++++++++++++
    LogoutHandler.setHandler(app,logger,ums,sms);

    }//M
} //C




   /*
        //Check before every request , OPT, see if needed (only if not 40X)
        app.beforeMatched(ctx -> {
            // runs before all matched requests (including static files)
        });
        */
