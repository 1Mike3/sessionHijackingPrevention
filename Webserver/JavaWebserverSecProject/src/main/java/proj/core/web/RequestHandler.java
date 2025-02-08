package proj.core.web;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.LoggerFactory;
import proj.core.SessionManagementSystem;
import proj.core.UserManagementSystem;
import proj.util.UaParserUtil;


/**
 * Class to handle requests
 * Some of the larger handlers are outsorced to other classes in the web package and called here
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
        app.before("/", ctx -> {
        analyzeHeader(ctx);
        });

        //app.after("/path/*", ctx -> {
        app.after("/*", ctx -> {
;;
        });
//++++++++++++++++++++++++++++++++ LOGIN HANDLER ++++++++++++++++++++++++++++++++++++++++++++++++
    HandlerLogin.setHandler(app,logger,ums,sms);
//++++++++++++++++++++++++++++++++ LOGOT HANDLER ++++++++++++++++++++++++++++++++++++++++++++++++
    HandlerLogout.setHandler(app,logger,ums,sms);
//++++++++++++++++++++++++++++++++ ACCESS SENSITIVE CONTENT HANDLER ++++++++++++++++++++++++++++++++++++++++++++++++
    HandlerAccessSensitiveContent.setHandler(app,logger,ums,sms);
    }//M

    public void analyzeHeader(Context ctx ){
        try {
            String headersJson = new ObjectMapper().writeValueAsString(ctx.headerMap());
            String sb = "##DEBUG-META-DATA-DUMP##" + "\n" +
                    "Request to: " + ctx.path() + "\n" +
                    "IP:" + ctx.ip() + "\n" + //This is just the IP of the proxy
                    //Can also be changed to X-Real-IP below
                    "Header-IP:" + ctx.header("X-Forwarded-For") + "\n" +//This is the real IP inserted by the proxy
                    "User-Agent:" + ctx.userAgent() + "\n" +
                    "Content-Type:" + ctx.contentType() + "\n" +
                    "Accept:" + ctx.header("Accept") + "\n" +
                    "language:" + ctx.header("Accept-Language") + "\n" +
                    "timezone:" + ctx.header("Time-Zone") + "\n" +
                    "screen:" + ctx.header("Screen-Resolution") + "\n" +
                    "## UNCURATED HEADERS ##" + "\n" +
                    "Headers: " + headersJson + "\n\n";
            logger.info(sb);
            UaParserUtil.parse(ctx.userAgent());
        } catch (Exception e) {
            logger.error("Error creating Header Debug Information: " + e.getMessage(), e);
        }
    }

} //C






   /*
        //Check before every request , OPT, see if needed (only if not 40X)
        app.beforeMatched(ctx -> {
            // runs before all matched requests (including static files)
        });
        */
