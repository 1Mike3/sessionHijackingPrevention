package proj.core;

import org.slf4j.LoggerFactory;
import proj.entities.FingerprintData;
import proj.entities.Location;
import proj.entities.User;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import proj.entities.UserAgent;
import proj.util.DatabaseUtil;

/**
 * - Class to manage the user data
 * - Singleton class
 * - Bulk of the database operations are done here, including initialization
 * - Because Fingerprint Data is tied to user it is also read out from the database here
 * - Formerly known as User Management System
 */
public class DataManagementSystem {

    private final Logger logger;
    private static DataManagementSystem instance;

    private DataManagementSystem() {
        this.logger = LoggerFactory.getLogger(DataManagementSystem.class);
        dbInitialize();
        //Inside the function check if console enabled in config
        DatabaseUtil.startWebConsole();
    }
    public static synchronized DataManagementSystem getInstance() {
        if (instance == null) {
            instance = new DataManagementSystem();
            return instance;
        }
        return instance;
    }


    /**
     * - Initializes the embeded H2 Database
     * - (Creates tables if they don't exist)
     */
    public void dbInitialize() {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS monitoringData (
                blockid INT PRIMARY KEY,
                ip VARCHAR(45),
                accept VARCHAR(255),
                encoding VARCHAR(255),
                longitude DECIMAL(10,8),
                latitude DECIMAL(10,8),
                screen VARCHAR(20),
                language VARCHAR(100),
                timezone VARCHAR(50),
                browser VARCHAR(30),
                browser_version VARCHAR(20),
                platform VARCHAR(50),
                canvas TEXT,
                webglVendor VARCHAR(50),
                webglRenderer VARCHAR(100),
                deviceMemory VARCHAR(10),
                cookiesAccepted BOOLEAN
                );
                
                CREATE TABLE IF NOT EXISTS users (
                 username VARCHAR(255) PRIMARY KEY,
                 passwordHashed VARCHAR(255),
                 sessionToken VARCHAR(255),
                 monitoringData INT,
                 FOREIGN KEY (monitoringData) REFERENCES monitoringData(blockid)
                );
        """;
            try (var statement = connection.createStatement()) {
                statement.execute(createTableSQL);
            }
        } catch (Exception e) {
            logger.error("Error initializing H2 Database: " + e.getMessage(), e);
        }
    }


    /**
     * - Checks if a username is valid, (exists in the database)
     * @param username name of the user that will be searched in the Database
     * @return true if the username exists in the database, false otherwise
     */
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


    /**
     * Returns a user Object including metadata from the Database
     * @param username name of the user that will be searched in the Database
     * @return User object with metadata if exists, else null
     */
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
                                        resultSet.getString("accept"),
                                        resultSet.getString("encoding"),
                                        new Location(
                                                resultSet.getBigDecimal("longitude"),
                                                resultSet.getBigDecimal("latitude")
                                        ),
                                        resultSet.getString("screen"),
                                        resultSet.getString("language"),
                                        resultSet.getString("timezone"),
                                        new UserAgent(
                                                resultSet.getString("browser"),
                                                resultSet.getString("browser_Version"),
                                                resultSet.getString("platform")
                                        ),
                                        resultSet.getString("canvas"),
                                        resultSet.getString("webglVendor"),
                                        resultSet.getString("webglRenderer"),
                                        resultSet.getString("deviceMemory"),
                                        resultSet.getBoolean("cookiesAccepted")
                                )
                        );
                    }
                    //debugprint of whole result set:
                    System.out.println(resultSet.getStatement().toString());
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching user by name from H2: " + e.getMessage(), e);
            return null;
        }
        logger.warn("getUserByName--User not found");
        return null;
    }



    /**
     * - Obtains a user's fingerprint data from the database
     * - Performance wise not the best way to do it, but fine for a Proof of Concept
     * @param username Name of the user whom's fingerprint data will be fetched
     * @return FingerprintData object if the user exists, null otherwise
     */
    public FingerprintData dbGetUserFingerprintData (String username) {
        User u  = dbGetUserByName(username);
        if(u != null){
            return u.getFdt();
        }else {
            return null;
        }
    }


    /**
     * - Updates an existing user in the databse
     * - Includes the FingerPrintData associated with the user (admittedly not the most efficient way)
     * @param username Name of the user that will be updated
     * @param user User object with the new data that will be written to the database
     * @return true if the user was updated successfully, false otherwise
     */
    public boolean dbUpdateUserByName(String username, User user) {
        Integer blockId;
        try (Connection connection = DatabaseUtil.getConnection()) {
            connection.setAutoCommit(false); // Ensure atomicity for updates


          //Get Blockid of the user
            String getBlockIdQuery = """
            SELECT monitoringData FROM users WHERE username = ?;
            """;
            try(PreparedStatement getBlockIdStmt = connection.prepareStatement(getBlockIdQuery)){
                getBlockIdStmt.setString(1, username);
                try(ResultSet resultSet = getBlockIdStmt.executeQuery()){
                    if(resultSet.next()){
                        //Setting it directly in fingerprint Data of User
                        user.getFdt().setBlockId(resultSet.getInt("monitoringData"));
                    }
                }
            }


            // Writing Session Monitoring Data
            String updateMonitoringDataQuery = """
        UPDATE monitoringData
        SET ip = ?, accept = ?, encoding = ?, longitude = ?, latitude = ?,
            screen = ?, language = ?, timezone = ?, browser = ?, browser_version = ?,
            platform = ?, canvas = ?, webglVendor = ?, webglRenderer = ?,
            deviceMemory = ?, cookiesAccepted = ?
        WHERE blockid = ?;
        """;

            try (PreparedStatement monitoringDataStmt = connection.prepareStatement(updateMonitoringDataQuery)) {
                FingerprintData fingerprint = user.getFdt();
                monitoringDataStmt.setString(1, fingerprint.getIP());
                monitoringDataStmt.setString(2, fingerprint.getAccept());
                monitoringDataStmt.setString(3, fingerprint.getEncoding());
                monitoringDataStmt.setBigDecimal(4, fingerprint.getLocation().getLongitude());
                monitoringDataStmt.setBigDecimal(5, fingerprint.getLocation().getLatitude());
                monitoringDataStmt.setString(6, fingerprint.getScreen());
                monitoringDataStmt.setString(7, fingerprint.getLanguage());
                monitoringDataStmt.setString(8, fingerprint.getTimezone());
                monitoringDataStmt.setString(9, fingerprint.getUserAgent().getBrowser());
                monitoringDataStmt.setString(10, fingerprint.getUserAgent().getBrowserVersion());
                monitoringDataStmt.setString(11, fingerprint.getUserAgent().getPlatform());
                monitoringDataStmt.setString(12, fingerprint.getCanvas());
                monitoringDataStmt.setString(13, fingerprint.getWebglVendor());
                monitoringDataStmt.setString(14, fingerprint.getWebglRenderer());
                monitoringDataStmt.setString(15, fingerprint.getDeviceMemory());
                monitoringDataStmt.setBoolean(16, fingerprint.isCookiesAccepted());
                monitoringDataStmt.setInt(17, fingerprint.getBlockId());
                monitoringDataStmt.executeUpdate();
            }

            // Writing User Table Data
            String updateUserQuery = """
        UPDATE users
        SET passwordHashed = ?, sessionToken = ?, monitoringData = ?
        WHERE username = ?;
        """;

            try (PreparedStatement userStmt = connection.prepareStatement(updateUserQuery)) {
                userStmt.setString(1, user.getPasswordHashed());
                userStmt.setString(2, user.getSessionToken());
                userStmt.setInt(3, user.getFdt().getBlockId());
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



    //Method to fetch all users and return them as a String
    // ONLY for DEBUGGING
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

