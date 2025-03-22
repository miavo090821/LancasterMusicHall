package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLConnection implements SQLInterface {
    // Database URL and credentials for connection.
    private String url = "jdbc:mysql://sst-stuproj.city.ac.uk:3306/xxx";
    // In a real application, use secure methods to manage database credentials.
    private String dbUser = "your_db_username";
    private String dbPassword = "your_db_password";
}
