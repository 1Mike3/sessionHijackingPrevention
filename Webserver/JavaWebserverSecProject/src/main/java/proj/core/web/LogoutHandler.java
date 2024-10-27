package proj.core.web;

import io.javalin.Javalin;
import org.slf4j.Logger;
import proj.core.SessionManagementSystem;
import proj.core.UserManagementSystem;

/**
 * separated from the RequestHandler because it is one of the complex handler
 */
public class LogoutHandler {
    public static void setHandler(Javalin app, Logger logger, UserManagementSystem ums, SessionManagementSystem sms){

    }
}
