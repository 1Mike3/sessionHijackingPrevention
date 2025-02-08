package proj.core.web;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.LoggerFactory;
import proj.core.Main;
import proj.core.SessionManagementSystem;
import proj.core.UserManagementSystem;
import proj.entities.Location;
import proj.entities.UserAgent;
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

            UserAgent ua = UaParserUtil.parse(ctx.userAgent());
            //for Debug purposes replacing localhost with google dns ip
            Location loc;
            if(
                ctx.header("X-Forwarded-For") == null ||
                ctx.header("X-Forwarded-For").contains("127.0.0") ||
                ctx.header("X-Forwarded-For").contains("localhost")
                ){
                loc = Main.geolocation_instance.getCoordinates("8.8.8.8");
            } else {
                loc = Main.geolocation_instance.getCoordinates(ctx.header("X-Forwarded-For"));
            }
            assert ua != null;
            String sb =
                    /*
                    Inital Rough attempt
                         "##DEBUG-META-DATA-DUMP##" + "\n" +
                    "Request to: " + ctx.path() + "\n" +
                    "Proxy-IP:" + ctx.ip() + "\n" + //This is just the IP of the proxy
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
                     */
                    "##DEBUG-META-DATA-DUMP##" + "\n" +
                    "Request to: " + ctx.path() + "\n" +
                    "BC1 Header-IP:" + ctx.header("X-Forwarded-For") + "\n" +
                    "\tProxy-IP:" + ctx.ip() + "\n" +
                    "BC2 Accept Header:" + ctx.header("Accept") + "\n" +
                    "BC2 Encoding:" + ctx.header("Accept-Encoding") + "\n" +
                    "BC3 Geolocation:" + loc.getLatitude() + " " + loc.getLongitude() + "\n" +
                    "BC4 Screen:" + ctx.header("Screen-Resolution") + "\n" +
                    "BC5 Language:" + ctx.header("Accept-Language") + "\n" +
                    "BC6 Timezone:" + ctx.header("Time-Zone") + "\n" +
                    "User-Agent:" +  "\n" +
                    "\t BC7 Browser:" + ua.getBrowser() + "\n" +
                    "\t BC8 Browser Version:" + ua.getBrowserVersion() + "\n" +
                    "\t BC9 Platform:" + ua.getPlatform() + "\n" +
                    "BC10 Canvas:" + ctx.header("Canvas") + "\n" +
                    "BC11 WebGL-Vendor:" + ctx.header("WebGL-Vendor") + "\n" +
                    "BC12 WebGL-Renderer:" + ctx.header("WebGL-Renderer") + "\n" +
                    "BC13 Device-Memory:" + ctx.header("Device-Memory") + "\n" +
                    "## UN-CURATED HEADER ##" + "\n" +
                    "Headers: " + headersJson + "\n\n";
            logger.trace(sb);
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
