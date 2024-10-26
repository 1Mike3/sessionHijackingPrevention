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

    private static SessionManagementSystem instance = null;

    //Constructor
    private SessionManagementSystem() {
      this.logger =  LoggerFactory.getLogger(SessionManagementSystem.class.getName());
      this.ums = UserManagementSystem.getInstance();
    }

    public static synchronized SessionManagementSystem getInstance() {
        if (instance == null) {
            return new SessionManagementSystem();
        }
        return instance;
    }



    //generates a random token
    public String generateUniqueToken() {
        String token = UUID.randomUUID().toString();
        LinkedList<User> users = ums.getUsers();
        return UUID.randomUUID().toString();
    }



}
