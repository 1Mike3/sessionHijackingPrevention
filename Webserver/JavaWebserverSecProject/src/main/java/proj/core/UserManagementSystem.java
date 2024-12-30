package proj.core;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import org.slf4j.LoggerFactory;
import proj.config.ConfigManager;
import proj.entities.User;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.util.LinkedList;

import org.slf4j.Logger;
import proj.util.DatabaseUtil;
import proj.util.JSON_Deserialize;
import proj.util.JSON_Serialize;


/**
 * UserManagementSystem class
 * This class is used to manage the users which can access the website.
 * It contains the methods to load and save the users from and to a JSON file,
 * as well as methods to check if a username is valid, get a user by username,
 * Singleton Pattern
 */
public class UserManagementSystem {
    /*
    List containing all users.
    Because user Creation is not the main focus of this project,
    users are created manually, and stored in the "userDB.json" file.
    The content of the JSON is loaded into this list on startup.
     */
    @Getter
    private LinkedList<User> users = new LinkedList<>();


    private final Logger logger;
    private final ConfigManager cfg;

    private static UserManagementSystem instance;


    //instance management
    private UserManagementSystem() {
        this.logger = LoggerFactory.getLogger(UserManagementSystem.class);
        this.cfg = ConfigManager.getInstance();
        initializeDatabase();
        loadUsers();
    }

    public static synchronized UserManagementSystem getInstance() {
        if (instance == null) {
            instance = new UserManagementSystem();
            return instance;
        }
        return instance;
    }

    public void initializeDatabase() {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) PRIMARY KEY,
                passwordHashed VARCHAR(255),
                sessionToken VARCHAR(255),
                IP VARCHAR(255)
            )
        """;
            try (var statement = connection.createStatement()) {
                statement.execute(createTableSQL);
            }
        } catch (Exception e) {
            logger.error("Error initializing H2 Database: " + e.getMessage(), e);
        }
    }


    //Rewritten to use H2 database
    public void loadUsers() {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "SELECT * FROM users";
            try (var statement = connection.createStatement();
                 var resultSet = statement.executeQuery(query)) {
                users.clear();
                while (resultSet.next()) {
                    users.add(new User(
                            resultSet.getString("username"),
                            resultSet.getString("passwordHashed"),
                            resultSet.getString("sessionToken"),
                            resultSet.getString("IP")
                    ));
                }
                logger.debug("User Database loaded successfully from H2!");
            }
        } catch (Exception e) {
            logger.error("Error loading User Database from H2: " + e.getMessage(), e);
        }
    }


    // Rewritten to use H2 database
    public boolean saveUsers() {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String deleteQuery = "DELETE FROM users";
            try (var deleteStatement = connection.createStatement()) {
                deleteStatement.executeUpdate(deleteQuery);
            }

            String insertQuery = "INSERT INTO users (username, passwordHashed, sessionToken, IP) VALUES (?, ?, ?, ?)";
            try (var preparedStatement = connection.prepareStatement(insertQuery)) {
                for (User user : users) {
                    preparedStatement.setString(1, user.getUsername());
                    preparedStatement.setString(2, user.getPasswordHashed());
                    preparedStatement.setString(3, user.getSessionToken());
                    preparedStatement.setString(4, user.getIP());
                    preparedStatement.executeUpdate();
                }
            }
            logger.debug("User Database saved successfully to H2!");
            return true;
        } catch (Exception e) {
            logger.error("Error saving User Database to H2: " + e.getMessage(), e);
            return false;
        }
    }


    //Checks if a username exists
    //Returns TRUE if the username exists in the list
    public boolean isUsernameValid(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    //Returns a user object by username from the list
    public User getUserByName(String username) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ?";
            try (var preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new User(
                                resultSet.getString("username"),
                                resultSet.getString("passwordHashed"),
                                resultSet.getString("sessionToken"),
                                resultSet.getString("IP")
                        );
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching user by name from H2: " + e.getMessage(), e);
        }
        logger.info("getUserByName--User not found");
        return null;
    }



    //Sets the token of a user by username
    //Returns TRUE if the token was set successfully
    public boolean setUserTokenByName(String username, String token) {
        User user = getUserByName(username);
        if (user != null) {
            user.setLoginToken(token);
         return saveUsers();
        } else {
            logger.info("setUserTokenByName--User not found");
            return false;
        }
    }

    public String getUserPasswordByName(String username) {
        User user = getUserByName(username);
        if (user != null) {
            return user.getPasswordHashed();
        } else {
            logger.info("getUserPasswordByName--User not found");
            return null;
        }
    }





}//end of class

