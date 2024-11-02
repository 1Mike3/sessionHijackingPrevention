package proj.core.web;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import proj.core.SessionManagementSystem;
import proj.core.UserManagementSystem;
import proj.util.CryptoFunc;
import java.util.HashMap;

/**
 * separated from the RequestHandler because it is one of the complex handler
 */
public class HandlerLogin {
    //Method which is called once to setup the handler of an endpoint
    public static void setHandler(Javalin app, Logger logger, UserManagementSystem ums, SessionManagementSystem sms){
        //react to recieved login form
        app.post("/login", ctx -> {
            // some code
            logger.info("Login attempt from: " + ctx.ip());
            logger.info(ctx.body());

            String username;
            String password;
            //parse data to obtain information which cookie to destroy
            try{
                HashMap loginData = ctx.bodyAsClass(HashMap.class);
                username = loginData.get("username").toString();
                password = loginData.get("password").toString();
            }catch (Exception e){
                logger.error("Error parsing login data");
                ctx.result("").status(HttpStatus.INTERNAL_SERVER_ERROR.getCode()); //500
                return;
            }

            //React to empty input
            if(username.isEmpty() || password.isEmpty()){
                logger.warn("Empty input");
                ctx.result("").status(HttpStatus.NO_CONTENT.getCode()); //204
                return;
            }else {

                //Check if user exists
                if ( ! ums.isUsernameValid(username)) {
                    logger.warn("Nonexistent User");
                    ctx.result("").status(HttpStatus.UNAUTHORIZED.getCode()); //561
                    return;
                }else {

                    //Check if Password Matches Username
                    //not differentiating status code on purpose (best practice) so no guessing usernames
                    logger.debug(CryptoFunc.hashSHA256(password));
                    logger.debug(ums.getUserByName(username).getPasswordHashed());
                    if(! CryptoFunc.hashSHA256(password).equals(ums.getUserByName(username).getPasswordHashed())){
                        logger.warn("Password Hash did not match");
                        ctx.result("").status(HttpStatus.UNAUTHORIZED.getCode()); //561
                        return;
                    }else{

                        logger.info("login credentials matched");
                        //Generate a Session Token Associated with this sesstion of the user
                        String token = sms.generateUniqueToken();

                        //save token, if saving fails abort
                        if ( ! ums.setUserTokenByName(username,token)){
                            logger.error("Error saving JSON to server");
                            ctx.result("").status(HttpStatus.INTERNAL_SERVER_ERROR.getCode()); //500
                            return;
                        }

                        //craft login respone message to the frontend
                        //usually I would build the json programmatically, but for the limited number of responses this will do
                        //ctx.result()
                        ctx.result(String.format("""
                        {
                            "username": "%s",
                            "token": "%s"
                        }
                        """, username, token)).status(HttpStatus.OK.getCode()); // 200

                    }

                }


            }

        });//login handler

    }
}
