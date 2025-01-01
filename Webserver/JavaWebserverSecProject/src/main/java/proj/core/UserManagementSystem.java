package proj.core;


import org.slf4j.LoggerFactory;
import proj.entities.FingerprintData;
import proj.entities.User;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import proj.util.DatabaseUtil;



/**
 * INFO OBSOLETE UPDATE
 */
public class UserManagementSystem {

    private final Logger logger;

    private static UserManagementSystem instance;


    //instance management
    private UserManagementSystem() {
        this.logger = LoggerFactory.getLogger(UserManagementSystem.class);
        dbInitialize();
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

    public void dbInitialize() {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String createTableSQL = """
            CREATE TABLE IF NOT EXISTS monitoringData (
                  blockid INT PRIMARY KEY,
                  ip VARCHAR(15),
                  longitude FLOAT,
                  latitude FLOAT,
                  cookiesAccepted BOOLEAN,
                  user_agent VARCHAR(200),
                  content_language VARCHAR(20),
                  timezone VARCHAR(20),
                  screen VARCHAR(20)
            );
            CREATE TABLE IF NOT EXISTS users (
                 username VARCHAR(255) PRIMARY KEY,
                 passwordHashed VARCHAR(255),
                 sessionToken VARCHAR(255),
                 monitoringData INT,
                 FOREIGN KEY (monitoringData) REFERENCES monitoringdata(BlockID)
            );
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
    public boolean dbIsUserNameValid(String username) {
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
    //
    public User dbGetUserByName(String username) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = """
SELECT * FROM users join monitoringData where MonitoringData =Blockid and users.username = ?;
            """;
            try (var preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new User(
                                resultSet.getString("username"),
                                resultSet.getString("passwordHashed"),
                                resultSet.getString("sessionToken"),
                                new FingerprintData(
                                        resultSet.getInt("blockid"),
                                        resultSet.getString("ip"),
                                        resultSet.getFloat("longitude"),
                                        resultSet.getFloat("latitude"),
                                        resultSet.getBoolean("cookiesAccepted"),
                                        resultSet.getString("user_agent"),
                                        resultSet.getString("content_language"),
                                        resultSet.getString("timezone"),
                                        resultSet.getString("screen")
                                )
                        );
                    }
                    //debugprint of whole result set:
                    System.out.println(resultSet.getStatement().toString());
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching user by name from H2: " + e.getMessage(), e);
        }
        logger.info("getUserByName--User not found");
        return null;
    }


    public boolean dbUpdateUserByName(String username, User user) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            connection.setAutoCommit(false); // Ensure atomicity for updates

            // Writing Session Monitoring Data
            String updateMonitoringDataQuery = """
            UPDATE monitoringData
            SET ip = ?, longitude = ?, latitude = ?, cookiesAccepted = ?,
                user_agent = ?, content_language = ?, timezone = ?, screen = ?
            WHERE blockid = ?;
        """;

            try (PreparedStatement monitoringDataStmt = connection.prepareStatement(updateMonitoringDataQuery)) {
                FingerprintData fingerprint = user.getFdt();
                monitoringDataStmt.setString(1, fingerprint.getIP());
                monitoringDataStmt.setFloat(2, fingerprint.getLongitude());
                monitoringDataStmt.setFloat(3, fingerprint.getLatitude());
                monitoringDataStmt.setBoolean(4, fingerprint.isCookiesAccepted());
                monitoringDataStmt.setString(5, fingerprint.getUserAgent());
                monitoringDataStmt.setString(6, fingerprint.getContent_Language());
                monitoringDataStmt.setString(7, fingerprint.getTimezone());
                monitoringDataStmt.setString(8, fingerprint.getScreen());
                monitoringDataStmt.setInt(9, fingerprint.getBlockId());
                monitoringDataStmt.executeUpdate();
            }

            // Writing User Table Datas
            String updateUserQuery = """
        UPDATE users
        SET passwordHashed = ?, sessionToken = ?, monitoringData = ?
        WHERE username = ?;
        """;

            try (PreparedStatement userStmt = connection.prepareStatement(updateUserQuery)) {
                userStmt.setString(1, user.getPasswordHashed());
                userStmt.setString(2, user.getSessionToken());
                userStmt.setInt(3, user.getFdt().getBlockId());;
                userStmt.setString(4, username);
                userStmt.executeUpdate();
            }

            connection.commit();
            logger.info("Successfully updated user: " + username);
            return true;
        } catch (Exception e) {
            logger.error("Error updating user in database: " + e.getMessage(), e);
            return false;
            }
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

