package proj.core.web;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import proj.config.Parameters;
import proj.core.SessionManagementSystem;
import proj.core.UserManagementSystem;

import java.nio.file.Files;
import java.nio.file.Paths;

public class HandlerAccessSensitiveContent {
    //Method which is called once to setup the handler of an endpoint
    public static void setHandler(Javalin app, Logger logger, UserManagementSystem ums, SessionManagementSystem sms){
        app.get("/restricted/userSpace.html", ctx -> {
            logger.atInfo().log("User Page Request from: " + ctx.ip());

            //obtain the username and token from the header to check if the user is allowed to access the page
            String username = ctx.header("Authorization-Username");
            String token = ctx.header("Authorization-Token");
            logger.atInfo().log("User Page Request :: Token: " + token + " Username: " + username);

            //check if the submitted data is valid
            if (username == null || token == null || username.isEmpty() || token.isEmpty()) {
                logger.atWarn().log("Invalid Request");
                ctx.result("").status(HttpStatus.UNAUTHORIZED); //401
                return;
            }

            //check with the authenticator if the provided credetials are valid
            if (sms.authenticator(username, token)) {
                logger.atInfo().log("User Page Request :: Token and Username Matched");
                //return the user page to the user

                try {
                    // Define the path to your HTML file
                    String filePath = Parameters.PATH_RELATIVE_USERSPACE_HTML.getValue().toString();
                    // Read the HTML file content as a string
                    String htmlContent = Files.readString(Paths.get(filePath));
                    // Return the HTML content as the response
                    ctx.contentType("text/html"); // Set content type to HTML
                    ctx.result(htmlContent).status(HttpStatus.OK.getCode());
                    return;
                } catch (Exception e) {
                    logger.atError().log("Error loading user page: " + e.getMessage());
                    ctx.status(HttpStatus.INTERNAL_SERVER_ERROR.getCode()).result("Internal Server Error"); //500
                }
                return;
            } else {
                logger.atWarn().log("User Page Request :: Token and Username did not match");
                ctx.result("").status(HttpStatus.UNAUTHORIZED); //401
                return;
            }

        });
    }
}
