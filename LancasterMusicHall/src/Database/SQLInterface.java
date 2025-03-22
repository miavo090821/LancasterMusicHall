package Database;

import java.sql.SQLException;

/**
 * SQLInterface defines the database operations for the application.
 * It includes methods for general queries, staff login validation, and password reset.
 */
public interface SQLInterface {
    // Executes a sample query to test the database connection.
    void connectToAndQueryDatabase(String username, String password) throws SQLException;

    // Validates staff login credentials.
    boolean loginStaff(String staffId, String password);

    // Resets the staff password based on staff ID and email verification.
    boolean resetPassword(String staffId, String email, String newPassword);
}
