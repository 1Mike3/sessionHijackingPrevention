package proj.core.web;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import proj.core.SessionManagementSystem;
import proj.core.DataManagementSystem;

import java.util.HashMap;

/**
 * separated from the RequestHandler because it is one of the complex handler
 */
public class HandlerLogout {
    //Method which is called once to setup the handler of an endpoint
    public static void setHandler(Javalin app, Logger logger, DataManagementSystem ums, SessionManagementSystem sms){
        //react to recieved login form
        app.post("/logout", ctx -> {
            // some code
            logger.info("Logout attempt from: " + ctx.ip());
            logger.info(ctx.body());

            String token = "";
            String username = "";

            //parse data to obtain information on which cookie to destroy
            try{
                HashMap loginData = ctx.bodyAsClass(HashMap.class);
                token = loginData.get("token").toString();
                username = loginData.get("username").toString();
                logger.info("Logout req. Recieved :: Token: " + token + " Username: " + username);
            }catch (Exception e){
                logger.error("Error parsing login data");
                ctx.result("").status(HttpStatus.INTERNAL_SERVER_ERROR.getCode()); //500
                return;
            }

            //find User in the Stored data and delete the token
            if(ums.dbIsUserNameValid(username)){
                ums.setUserTokenByName(username,""); //Invalidate Token
                logger.info("Logout successfully, Token deleted");
                ctx.result("").status(HttpStatus.OK.getCode()); //200
                return;
            }else{
                logger.warn("User does not exist");
                ctx.result("").status(HttpStatus.UNAUTHORIZED.getCode()); //401
                return;
            }

        });//logout handler
    }
}
