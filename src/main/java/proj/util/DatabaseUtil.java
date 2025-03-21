package proj.util;

import java.sql.Connection;
import java.sql.DriverManager;
import org.h2.server.*;
import org.h2.tools.Server;
import proj.config.ConfigManager;

/**
 * Utility class to handle database connections
 * Utilized Heavily in the UserManagementSystem
 */
public class DatabaseUtil {

    private static final ConfigManager cfg = ConfigManager.getInstance();

    private static final String JDBC_URL;
    static {
        assert cfg != null;
        if( ! cfg.isON_DEVICE()) {
            JDBC_URL = "jdbc:h2:./src/main/resources/persistence/userDB";
        } else {
            JDBC_URL = "jdbc:h2:./target/classes/persistence/userDB";
        }
    }
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static Server webServer;

    public static void startWebConsole() {
        assert cfg != null;
        if(cfg.isDB_WEBSERVER_ENABLED()) {
            try {
                webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8932").start();
                System.out.println("H2 Console started at http://localhost:8932");
            } catch (Exception e) {
                System.err.println("Error starting H2 Console: " + e.getMessage());
            }

        }
    }

    public static void stopWebConsole() {
        if (webServer != null) {
            webServer.stop();
            System.out.println("H2 Console stopped.");
        }
    }

    public static Connection getConnection() throws Exception {
        Class.forName("org.h2.Driver"); // Explicitly load the driver
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

}
