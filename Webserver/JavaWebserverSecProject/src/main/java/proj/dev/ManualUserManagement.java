package proj.dev;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import proj.core.UserManagementSystem;
import proj.entities.User;

import java.util.LinkedList;

public class ManualUserManagement {

    //Just for testing and for programmatically adding users to the Database, not used during runtime
    public static void main(String[] args) {
        //print out working directory
        System.out.println(System.getProperty("user.dir"));
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        UserManagementSystem ums = new UserManagementSystem(loggerFactory.getLogger(UserManagementSystem.class.getName()));
        //comment out to overwrite users list, uncomment to append users
        //ums.loadUsers();
        LinkedList<User> users = ums.getUsers();
        users.add(new User("michi", "x", "x"));
        users.add(new User("admin", "x", "x"));
        users.add(new User("test", "x", "x"));
        ums.saveUsers();

        System.out.println(users.get(0).getUsername());
    }

}
