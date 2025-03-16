package proj.core.web;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import proj.core.fingerprinting.FpRequestProcessor;
import proj.core.SessionManagementSystem;
import proj.core.DataManagementSystem;
import proj.entities.FingerprintData;
import proj.entities.User;
import proj.util.CryptoFunc;
import java.util.HashMap;

/**
 * separated from the RequestHandler because it is one of the complex handler
 */
public class HandlerLogin {
    //Method which is called once to setup the handler of an endpoint
    public static void setHandler(Javalin app, Logger logger, DataManagementSystem dms, SessionManagementSystem sms){
        //react to received login form
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
                if ( ! dms.dbIsUserNameValid(username)) {
                    logger.warn("Nonexistent User");
                    ctx.result("").status(HttpStatus.UNAUTHORIZED.getCode()); //561
                    return;
                }else {

                    //Check if Password Matches Username
                    //not differentiating status code on purpose (best practice) so no guessing usernames
                    logger.trace(CryptoFunc.hashSHA256(password));
                    logger.trace(dms.dbGetUserByName(username).getPasswordHashed());
                    if(! CryptoFunc.hashSHA256(password).equals(dms.dbGetUserByName(username).getPasswordHashed())){
                        logger.warn("Password Hash did not match");
                        ctx.result("").status(HttpStatus.UNAUTHORIZED.getCode()); //561
                        return;
                    }else{

                        logger.info("login credentials matched");
                        //Generate a Session Token Associated with this sesstion of the user
                        String token = sms.generateUniqueToken();

                        //OLD save token, if saving fails abort
                        /*
                        if ( ! ums.setUserTokenByName(username,token)){
                            logger.error("Error updating database with token");
                            ctx.result("").status(HttpStatus.INTERNAL_SERVER_ERROR.getCode()); //500
                            return;
                        }*/
                        //Previouly setUserTokenByName, now fingerprint data and token added to user and

                        //NEW
                        //Get the fingerprint data from the request
                        FingerprintData fingerprintData;
                        try {
                            fingerprintData = FpRequestProcessor.processRequestFp(ctx);
                            if (fingerprintData == null) {
                                throw new Exception("Fingerprint Data is null");
                            }
                        }catch (Exception e){
                            logger.error("Error Parsing Fingerprint Data");
                            ctx.result("").status(HttpStatus.INTERNAL_SERVER_ERROR.getCode()); //500
                            return;
                        }
                        //Fetching the user from the database, adding request Data and writing them back to the db
                        try{
                            //Getting user from DB
                            User requestUser = dms.dbGetUserByName(username);
                            //Adding Data to User Object
                            requestUser.setFdt(fingerprintData);
                            requestUser.setSessionToken(token);
                            //Writing User back to DB
                            dms.dbUpdateUserByName(username,requestUser);
                        }catch (Exception e){
                            logger.error("Error updating database with token");
                            ctx.result("").status(HttpStatus.INTERNAL_SERVER_ERROR.getCode()); //500
                            return;
                        }


                        //craft simple login response message to the frontend
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
