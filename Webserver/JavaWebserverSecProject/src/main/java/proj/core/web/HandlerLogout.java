package proj.core.web;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import proj.core.SessionManagementSystem;
import proj.core.UserManagementSystem;
import proj.entities.User;
import java.util.HashMap;

/**
 * separated from the RequestHandler because it is one of the complex handler
 */
public class HandlerLogout {
    //Method which is called once to setup the handler of an endpoint
    public static void setHandler(Javalin app, Logger logger, UserManagementSystem ums, SessionManagementSystem sms){
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
            User u = ums.getUserByName(username);
            if(u != null){
                //Check if token and username match
                if(u.getSessionToken().equals(token) && u.getUsername().equals(username)) {
                    u.setLoginToken(""); //delete token
                    //save updated user data to the list
                    if (ums.saveUsers()) {
                        logger.info("Token deleted successfully");
                        ctx.result("").status(HttpStatus.OK.getCode()); //200
                        return;
                    } else {
                        logger.warn("Error saving User To Database");
                        ctx.result("").status(HttpStatus.INTERNAL_SERVER_ERROR.getCode()); //500
                        return;
                    }

                }
                    logger.warn("Token does not exist");
                    ctx.result("").status(HttpStatus.INTERNAL_SERVER_ERROR.getCode()); //500
            }

        });//logout handler
    }
}
