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
import java.util.LinkedList;

import org.slf4j.Logger;
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
        loadUsers();
    }

    public static synchronized UserManagementSystem getInstance() {
        if (instance == null) {
            instance = new UserManagementSystem();
            return instance;
        }
        return instance;
    }


    //Loads the users from the JSON file into memory
    public void loadUsers() {
        try {
            // Define the path to the user database file
            File file;
            if(cfg.isON_DEVICE())
                file = new File(cfg.getPATH_RELATIVE_USER_DB_ON_DEVICE());
            else
                file = new File(cfg.getPATH_RELATIVE_USER_DB());


            // Check if the file exists
            if (file.exists()) {
                logger.debug("User Database found!");
                // Read the file content as a string
                String fileContent = Files.readString(Paths.get(file.getPath()));
                // Check if the file is empty
                if (fileContent.isEmpty() || fileContent.length() < 2) {
                    logger.error("User Database is empty on load!");
                    return;
                }

                // Deserialize the JSON content into a LinkedList<User>
                this.users = JSON_Deserialize.deserialize(fileContent, new TypeReference<LinkedList<User>>() {
                });
                logger.debug("User Database loaded successfully!");
            } else {
                logger.error("User Database not found!");
            }
        } catch (Exception e) {
            logger.error("Error loading User Database: " + e.getMessage(), e);
        }
    }

    //saves users to db (JSON)
    //returns true on success and false on fail
    public boolean saveUsers() {
        try {
            // Convert users LinkedList to JSON string
            String jsonContent = JSON_Serialize.serialize(users);

            // Define the path to the user database file
            File file;
            if(cfg.isON_DEVICE())
                file = new File(cfg.getPATH_RELATIVE_USER_DB_ON_DEVICE());
            else
                file = new File(cfg.getPATH_RELATIVE_USER_DB());


            // Write JSON string to the file (overwrite existing content)
            Files.write(Paths.get(file.getPath()), jsonContent.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            logger.debug("User Database saved successfully!");
            return true;
        } catch (Exception e) {
            logger.error("Error saving User Database: " + e.getMessage(), e);
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
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
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

