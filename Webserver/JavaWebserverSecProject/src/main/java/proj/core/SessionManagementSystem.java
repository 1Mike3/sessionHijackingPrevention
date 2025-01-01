package proj.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import proj.entities.User;
import proj.util.DatabaseUtil;
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
        try {
            User user = ums.getUserByName(username);
            if (user != null && token.equals(user.getSessionToken())) {
                return true;
            }
            logger.error("Username and token did not match. Debugging information:");
            if (user != null) {
                logger.info("Stored token for {}: {}", user.getUsername(), user.getSessionToken());
            } else {
                logger.info("User not found: {}", username);
            }
        } catch (Exception e) {
            logger.error("Error during authentication: " + e.getMessage(), e);
        }
        return false;
    }

    public String generateUniqueToken() {
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (isTokenInUse(token)); // Check if token is in use
        return token;
    }

    /**
     * Checks if a token is already in use by querying the database.
     */
    private boolean isTokenInUse(String token) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE sessionToken = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, token);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0; // Return true if the token exists
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error checking if token is in use: " + e.getMessage(), e);
        }
        return false; // If there's an error, assume the token is not in use
    }



}
