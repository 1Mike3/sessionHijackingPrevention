package proj.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proj.entities.User;

import java.util.LinkedList;
import java.util.UUID;

public class SessionManagementSystem {

    //Setup Logger for Logging custom Messages
    private final Logger logger;
    private final UserManagementSystem ums;

    private static SessionManagementSystem instance;

    //Constructor
    private SessionManagementSystem() {
      this.logger =  LoggerFactory.getLogger(SessionManagementSystem.class.getName());
      this.ums = UserManagementSystem.getInstance();
    }
    //Get Instance for Singleton Pattern
    public static synchronized SessionManagementSystem getInstance() {
        if (instance == null) {
            instance = new SessionManagementSystem();
            return instance;
        }
        return instance;
    }

    //Checks if a provided username-token pair is valid and returns a boolean
    public boolean authenticator(String username, String token) {
        LinkedList<User> users = ums.getUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getSessionToken().equals(token)) {
                return true;
            }
        }
        logger.error("Username and String did not Match, stored Data for debugging:");
        for(User u : ums.getUsers()){
            logger.info(u.getUsername());
            logger.info(u.getSessionToken());
        }
        return false;
    }


    //does what it says on the tin :)
    public String generateUniqueToken() {
        String token = UUID.randomUUID().toString();
        //Do not see a high risk for collisions with three test users ... but still
        LinkedList<User> users = ums.getUsers();
        for (User user : users) {
            if (user.getSessionToken().equals(token)) {
                logger.warn("Token already exists, generating new one");
                return generateUniqueToken();
            }
        }
        return token;
    }





}
