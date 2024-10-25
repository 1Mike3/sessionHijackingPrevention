package proj.core;

import proj.entities.User;

import java.util.ArrayList;

public class UserManagementSystem {
    /*
    List containing all users.
    Because user Creation is not the main focus of this project,
    users are created manually, and stored in the "userDB.json" file.
    The content of the JSON is loaded into this list on startup.
     */
    ArrayList<User> users = new ArrayList<User>();

}
