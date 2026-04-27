package jdbc.demo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private DatabaseConnection() {
        // Prevent creating objects from this class.
    }

    public static Connection getConnection() throws SQLException {
        // One place for database connection settings.
        return DriverManager.getConnection("jdbc:mysql://localhost:3307/borrowingsystem", "root", "");
    }
}