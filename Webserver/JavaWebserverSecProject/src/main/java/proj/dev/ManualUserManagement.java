package proj.dev;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import proj.core.UserManagementSystem;
import proj.entities.User;

import java.util.LinkedList;
//Just for testing and for programmatically adding users to the Database, not used during runtime
public class ManualUserManagement {

    static boolean  addUsers = true;
    static boolean formatJson = false;

    public static void main(String[] args) {
        if (addUsers) {
            addUsers();
        }
        if (formatJson) {
            formatJson();
        }
    }


    //Adds a set of users to the user list
    public static void addUsers() {
        //print out working directory
        System.out.println(System.getProperty("user.dir"));

        UserManagementSystem ums = UserManagementSystem.getInstance();

        //comment out to overwrite users list, uncomment to append users
        //ums.loadUsers();

        LinkedList<User> users = ums.getUsers();
        //This is obviously for testing and would be done differently if handling real user data
        users.add(new User("michi", "edacb633b0cdf2af598c4b90c3a7941a0015dcea1e8acb292107649e846cd405", "x",""));
        users.add(new User("admin", "edacb633b0cdf2af598c4b90c3a7941a0015dcea1e8acb292107649e846cd405", "x",""));
        users.add(new User("test", "edacb633b0cdf2af598c4b90c3a7941a0015dcea1e8acb292107649e846cd405", "","x"));
        ums.saveUsers();
        //Validation
        System.out.println(users.get(0).getUsername());
    }

    //formats the json User DB file to make it more readable
    public static void formatJson() {
;
    }

}
