package proj.core;

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import proj.entities.User;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;

import org.slf4j.Logger;
import proj.util.JSON_Deserialize;
import proj.util.JSON_Serialize;

public class UserManagementSystem {
    /*
    List containing all users.
    Because user Creation is not the main focus of this project,
    users are created manually, and stored in the "userDB.json" file.
    The content of the JSON is loaded into this list on startup.
     */
    private LinkedList<User> users = new LinkedList<User>();
    private Logger logger;

    public UserManagementSystem(Logger logger) {
        this.logger = logger;
        //loadUsers();
    }

    //Getts and Setters
    public LinkedList<User> getUsers() {
        return users;
    }

    //Loads the users from the JSON file into memory
    public void loadUsers() {
        try {
            // Define the path to the user database file
            File file = new File("./src/main/resources/persistence/userDB.json");

            // Check if the file exists
            if (file.exists()) {
                logger.atDebug().log("User Database found!");
                // Read the file content as a string
                String fileContent = Files.readString(Paths.get(file.getPath()));
                    // Check if the file is empty
                if (fileContent.isEmpty() || fileContent.length() < 2) {
                    logger.error("User Database is empty on load!");
                    return;
                }

                // Deserialize the JSON content into a LinkedList<User>
                users = JSON_Deserialize.deserialize(fileContent, new TypeReference<LinkedList<User>>() {});
                logger.atDebug().log("User Database loaded successfully!");
            } else {
                logger.error("User Database not found!");
            }
        } catch (Exception e) {
            logger.error("Error loading User Database: " + e.getMessage(), e);
        }
    }

    public void saveUsers() {
        try {
            // Convert users LinkedList to JSON string
            String jsonContent = JSON_Serialize.serialize(users);

            // Define the path to the user database file
            File file = new File("./src/main/resources/persistence/userDB.json");

            // Write JSON string to the file (overwrite existing content)
            Files.write(Paths.get(file.getPath()), jsonContent.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            logger.atDebug().log("User Database saved successfully!");
        } catch (Exception e) {
            logger.error("Error saving User Database: " + e.getMessage(), e);
        }
    }



}
