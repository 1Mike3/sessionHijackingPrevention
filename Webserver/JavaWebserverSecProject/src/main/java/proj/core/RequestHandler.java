package proj.core;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.slf4j.LoggerFactory;
import proj.util.CryptoFunc;

import java.io.IOException;
import java.util.HashMap;

/**
 * Class to handle requests
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
        //react to recieved login form
        app.post("/login", ctx -> {
            // some code
            logger.atInfo().log("Login attempt from: " + ctx.ip());
            logger.atInfo().log(ctx.body());

            String username = "";
            String password = "";
            //Obtain Information from Form
            try{
                HashMap loginData = ctx.bodyAsClass(HashMap.class);
                username = loginData.get("username").toString();
                password = loginData.get("password").toString();
                //System.out.println("Deserialized Data(n): " + loginData.get("username"));
                //System.out.println("Deserialized Data(p): " + loginData.get("password"));
            }catch (Exception e){
                logger.atError().log("Error parsing login data");
                ctx.result("{ \"Success\":false}").status(HttpStatus.INTERNAL_SERVER_ERROR.getCode()); //500
            }

            //React to empty input
            if(username.isEmpty() || password.isEmpty()){
                logger.atWarn().log("Empty input");
                ctx.result("{ \"Success\":false}").status(HttpStatus.NO_CONTENT.getCode()); //204
            }else {

                //Check if user exists
                if ( ! ums.isUsernameValid(username)) {
                    logger.atWarn().log("Nonexistent User");
                    ctx.result("{\"Success\":false}").status(HttpStatus.UNAUTHORIZED.getCode()); //561
                }else {

                    //Check if Password Matches Username
                    //not differentiating status code on purpose (best practice)
                    logger.debug(CryptoFunc.hashSHA256(password));
                    logger.debug(ums.getUserByName(username).getPasswordHashed());
                    if(! CryptoFunc.hashSHA256(password).equals(ums.getUserByName(username).getPasswordHashed())){
                        logger.atWarn().log("Password Hash did not match");
                        ctx.result("{\"Success\":false}").status(HttpStatus.UNAUTHORIZED.getCode()); //561
                    }else{


                        logger.atInfo().log("login credentials matched");
                        //Generate a Session Token Associated with this sesstion of the user
                        String token = sms.generateUniqueToken();

                            //save token, if saving fails abort
                            if ( ! ums.setUserTokenByName(username,token)){
                                logger.atError().log("Error saving JSON to server");
                                ctx.result("{\"Success\":false}").status(HttpStatus.UNAUTHORIZED.getCode()); //561
                                return;
                            }

                        //craft login respone message to the frontend
                        //ctx.result()
                        ctx.result("{\"Success\":true}").status(HttpStatus.OK.getCode()); //200
                        //TODO upgrade JDK to 21/15 to use block literal string
                        //TODO finsih crafting login response to frontend
                    }


                }




            }

        });//login handler
    }//meth
} //class


//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

   /*
        //Check before every request , OPT, see if needed (only if not 404)
        app.beforeMatched(ctx -> {
            // runs before all matched requests (including static files)
        });
        */
