package Database;

import java.sql.SQLException;

/**
 * A simple interface defining the core methods for connecting
 * and interacting with the database.
 */
public interface SQLInterface {
    // Test connection or run a basic query
    void connectToAndQueryDatabase(String username, String password) throws SQLException;

    // Validate a staff login
    boolean loginStaff(String staffId, String password);

    // Reset a staff password (with email verification)
    boolean resetPassword(String staffId, String email, String newPassword);
}
