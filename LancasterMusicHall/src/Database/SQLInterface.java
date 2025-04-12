package Database;

import java.sql.SQLException;

/**
 * Defines the core database operations for the venue management system.
 * This interface abstracts the database layer and provides methods for:
 * <ul>
 *   <li>Establishing database connections</li>
 *   <li>Handling staff authentication</li>
 *   <li>Managing password recovery</li>
 * </ul>
 *
 * Implementations should handle all SQL exceptions and provide proper connection pooling.
 */
public interface SQLInterface {

    /**
     * Establishes a database connection and executes a test query.
     * Used to verify database availability and credentials.
     * <p>
     * <b>Implementation Note:</b> This method must close all database resources
     * (Connection, Statement, ResultSet) after test completion.
     * </p>
     *
     * @param username The database username credential
     * @param password The database password credential
     * @throws SQLException if connection fails or query execution errors occur
     */
    void connectToAndQueryDatabase(String username, String password) throws SQLException;

    /**
     * Authenticates staff members against the database.
     * <p>
     * <b>Implementation Requirements:</b>
     * <ul>
     *   <li>Passwords should be compared using secure hashing</li>
     *   <li>Must close all database resources after completion</li>
     * </ul>
     *
     * @param staffId The staff identification number
     * @param password The staff member's password (plaintext)
     * @return true if credentials match an active staff record, false otherwise
     * @throws SQLException if database access errors occur
     */
    boolean loginStaff(String staffId, String password) throws SQLException;

    /**
     * Resets a staff member's password after email verification.
     * <p>
     * <b>Implementation Requirements:</b>
     * <ul>
     *   <li>Verify staffId and email match</li>
     *   <li>Hash the new password before storage</li>
     *   <li>Update last_password_change timestamp</li>
     * </ul>
     *
     * @param staffId The staff identification number
     * @param email The registered email address for verification
     * @param newPassword The new password to set (plaintext)
     * @return true if reset was successful, false if verification failed
     * @throws SQLException if database access errors occur
     */
    boolean resetPassword(String staffId, String email, String newPassword) throws SQLException;
}