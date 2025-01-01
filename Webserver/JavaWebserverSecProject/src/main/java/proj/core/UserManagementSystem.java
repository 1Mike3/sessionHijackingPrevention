package proj.core;


import lombok.Getter;
import org.slf4j.LoggerFactory;
import proj.entities.User;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import proj.util.DatabaseUtil;



/**
 * UserManagementSystem class
 * This class is used to manage the users which can access the website.
 * It contains the methods to load and save the users from and to a JSON file,
 * as well as methods to check if a username is valid, get a user by username,
 * Singleton Pattern
 */
public class UserManagementSystem {

    private final Logger logger;

    private static UserManagementSystem instance;


    //instance management
    private UserManagementSystem() {
        this.logger = LoggerFactory.getLogger(UserManagementSystem.class);
        initializeDatabase();
        //Inside the function check if console enabled in config
        DatabaseUtil.startWebConsole();
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




    //Checks if a username exists
    //Returns TRUE if the username exists in the list
    public boolean isUsernameValid(String username) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String checkUsername =
                    """
            SELECT * FROM users WHERE username = ?;
        """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(checkUsername)) {
                preparedStatement.setString(1, username);
               try(ResultSet resSet = preparedStatement.executeQuery()){
                   if(resSet.next()){
                       System.out.println("User Found:" + resSet.getString("username"));
                       return true;
                   }else{
                       System.out.println("User not Found");
                          return false;
                   }
               }
            }
        } catch (Exception e) {
            logger.error("Error checking Username " + e.getMessage(), e);
            return false;
        }
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
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "UPDATE users SET sessionToken = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, token);
                preparedStatement.setString(2, username);
                int rowsUpdated = preparedStatement.executeUpdate();
                return rowsUpdated > 0; // Return true if at least one row was updated
            }
        } catch (Exception e) {
            logger.error("Error setting token for user " + e.getMessage(), e);
            return false;
        }
    }

    public String getUserPasswordByName(String username) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "SELECT passwordHashed FROM users WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("passwordHashed");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching password for user " + e.getMessage(), e);
        }
        logger.info("getUserPasswordByName--User not found");
        return null;
    }

    //Method to fetch all users and return them as a String for debugging
    public String getAllUsersAsString() {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "SELECT * FROM users";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    StringBuilder returnSring = new StringBuilder();
                    while (resultSet.next()) {
                        returnSring.append("User: ").append(resultSet.getString("username")).append("\n");
                        returnSring.append("Password: ").append(resultSet.getString("passwordHashed")).append("\n");
                        returnSring.append("Token: ").append(resultSet.getString("sessionToken")).append("\n");;
                        returnSring.append("IP: ").append(resultSet.getString("IP")).append("\n");
                    }
                    return returnSring.toString();
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching all users: " + e.getMessage(), e);
            return "Error fetching all users";
        }
    }




}//end of class

