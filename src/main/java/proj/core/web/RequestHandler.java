package proj.core.web;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.LoggerFactory;
import proj.core.Main;
import proj.core.fingerprinting.FpRequestProcessor;
import proj.core.SessionManagementSystem;
import proj.core.DataManagementSystem;
import proj.entities.FingerprintData;
import proj.entities.Location;
import proj.entities.UserAgent;
import proj.core.fingerprinting.UaParserUtil;


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
        DataManagementSystem ums = DataManagementSystem.getInstance();

        //Check before or after  request (rem. path for every request)
            app.before("/*", ctx -> {
                //analyzeHeader(ctx);
            });
            app.before("/", ctx -> {
                analyzeHeader(ctx);
            });

        //app.after("/path/*", ctx -> {
        app.after("/*", ctx -> {
        });

        //Special for accessing the real accept header, because else overwritten ( */* )
        app.get("/capture-accept", ctx -> {
            String acceptHeader = ctx.header("Accept");
            //Bec exception ";" in cookie ... fair enough
            acceptHeader = acceptHeader.replaceAll(";", "#");
            if (acceptHeader != null) {
                // Store the real Accept header in a session or cookie
                ctx.sessionAttribute("storedAcceptHeader", acceptHeader); //Server side storage
                ctx.cookie("storedAcceptHeader", acceptHeader);
            }
            logger.trace("Captured Accept Header: " + acceptHeader);
            ctx.result(acceptHeader);
        });
        app.get("/get-stored-accept", ctx -> {
            String acceptHeader = ctx.sessionAttribute("storedAcceptHeader");
            if (acceptHeader == null) {
                acceptHeader = "Unknown";
            }
            ctx.result(acceptHeader);
        });



//++++++++++++++++++++++++++++++++ LOGIN HANDLER ++++++++++++++++++++++++++++++++++++++++++++++++
    HandlerLogin.setHandler(app,logger,ums,sms);
//++++++++++++++++++++++++++++++++ LOGOT HANDLER ++++++++++++++++++++++++++++++++++++++++++++++++
    HandlerLogout.setHandler(app,logger,ums,sms);
//++++++++++++++++++++++++++++++++ ACCESS SENSITIVE CONTENT HANDLER ++++++++++++++++++++++++++++++++++++++++++++++++
    HandlerAccessSensitiveContent.setHandler(app,logger,ums,sms);

    }//M

    /**
     * Just Written for Debugging purposes
     */
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
            //Test Request Processor
            if(
                    ctx.header("X-Forwarded-For") == null ||
                            ctx.header("X-Forwarded-For").contains("127.0.0") ||
                            ctx.header("X-Forwarded-For").contains("localhost")
            ){
                ctx.header("X-Forwarded-For", "8.8.8.8"); //Replace with Google DNS for local ip tetsting
            }
            FingerprintData fi = FpRequestProcessor.processRequestFp(ctx);
            if(fi != null){
                logger.trace(fi.toString());
            }else {
                logger.error("Error processing request");
            }



            assert ua != null;
            String sb =
                    "##DEBUG-META-DATA-DUMP##" + "\n" +
                    "Request (Caution may be set to / only)): " + ctx.path() + "\n" +
                    "BC1 Header-IP:" + ctx.header("X-Forwarded-For") + "\n" +
                    "\tProxy-IP:" + ctx.ip() + "\n" +
                    "BC2 Accept Header:" + ctx.header("Accept") + "\n" +
                    "BC2* Real Accept Header: " + ctx.header("Full-Accept") + "\n" +
                    "BC2 Encoding:" + ctx.header("Accept-Encoding") + "\n" +
                    "BC3 Geolocation:" + loc.getLatitude() + " " + loc.getLongitude() + "\n" +
                    "BC4 Screen:" + ctx.header("Screen-Resolution") + "\n" +
                    "BC5 Language:" + ctx.header("Accept-Language") + "\n" +
                    "BC6 Timezone:" + ctx.header("Timezone") + "\n" +
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
