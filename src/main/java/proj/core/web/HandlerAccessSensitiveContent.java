package proj.core.web;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proj.config.ConfigManager;
import proj.core.Main;
import proj.core.SessionManagementSystem;
import proj.core.DataManagementSystem;
import proj.core.fingerprinting.FpRequestProcessor;
import proj.core.fingerprinting.FpRequestValidator;
import proj.entities.FingerprintData;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class HandlerAccessSensitiveContent {
    //used for special log messages
    public static final Logger analysisLogger = Main.analysisLogger;

    //Method which is called once to setup the handler of an endpoint
    public static void setHandler(Javalin app, Logger logger, DataManagementSystem dms, SessionManagementSystem sms){


        //Generating instances needed to handle the request
        ConfigManager cfg = ConfigManager.getInstance();
        //FpRequestProcessor rp = new FpRequestProcessor(); //Static
        FpRequestValidator rv = new FpRequestValidator(Objects.requireNonNull(ConfigManager.getInstance()).getFINGERPRINT_SENSITIVITY());


        app.get("/restricted/userSpace.html", ctx -> {
            logger.info("User Page Request from: " + ctx.ip());
            //obtain the username and token from the header to check if the user is allowed to access the page
            String username = ctx.header("Authorization-Username");
            String token = ctx.header("Authorization-Token");
            logger.info("User Page Request :: Token: " + token + " Username: " + username);
            analysisLogger.info("New Request\n\n\n\n\n\n\n");
            analysisLogger.info("!#####! User Page Request !#####!\n" + "Username: " + username + "\nToken: " + token  );

            //check if the submitted data is valid
            if (username == null || token == null || username.isEmpty() || token.isEmpty()) {
                logger.warn("Invalid Request");
                ctx.result("").status(HttpStatus.UNAUTHORIZED); //401
                return;
            }

            //check with the authenticator if the provided credetials are valid
            if (! sms.authenticator(username, token)) {
                logger.warn("User Page Request :: Token and Username did not match");
                ctx.result("").status(HttpStatus.UNAUTHORIZED); //401
                return;
            }
            logger.info("User Page Request :: Token and Username Matched");


            //Performing fingerprint validation
            FingerprintData fpNew = FpRequestProcessor.processRequestFp(ctx);
            FingerprintData fpOld = dms.dbGetUserFingerprintData(username);
            if( ! rv.validateMetadata(fpNew, fpOld)) { //If Fingerprint Validation fails
                logger.warn("User Page Request :: Fingerprint Validation Failed");
                analysisLogger.warn("User Page Request :: Fingerprint Validation Failed");
                dms.invalidateUserToken(username);
                ctx.result("").status(HttpStatus.CONFLICT); //409
                return;
            } else {
                //Fingerprint validation successful, normal proceeding to return the user page
                analysisLogger.info("User Page Request :: Fingerprint Validation Passed");
                try {
                    // Defining the path to HTML file (different for on-device and off-device)
                    String filePath;
                    assert cfg != null;
                    if(cfg.isON_DEVICE())
                        filePath = ConfigManager.getInstance().getPATH_RELATIVE_USERSPACE_HTML_ON_DEVICE();
                    else
                        filePath = ConfigManager.getInstance().getPATH_RELATIVE_USERSPACE_HTML();
                    String htmlContent = Files.readString(Paths.get(filePath));

                    // Returning the HTML content
                    ctx.contentType("text/html");
                    ctx.result(htmlContent).status(HttpStatus.OK.getCode());
                    return;

                } catch (Exception e) {
                    logger.error("Error loading user page: " + e.getMessage());
                    ctx.status(HttpStatus.INTERNAL_SERVER_ERROR.getCode()).result("Internal Server Error"); //500
                }
                return;
            }

        });
    }
}
