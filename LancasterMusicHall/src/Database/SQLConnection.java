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

    @Override
    public void connectToAndQueryDatabase(String username, String password) throws SQLException{
        // This method demonstrates a simple query execution.
        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = con.prepareStatement("SELECT a, b, c FROM Table1");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int x = rs.getInt("a");
                String s = rs.getString("b");
                float f = rs.getFloat("c");
                System.out.println("a: " + x + ", b: " + s + ", c: " + f);
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute query");
            e.printStackTrace();
        }
    }
    /**
     * Validates staff login credentials by querying the Staff table.
     * @param staffId The staff ID provided by the user.
     * @param password The password provided by the user.
     * @return true if credentials are valid, false otherwise.
     */
    @Override
    public boolean loginStaff(String staffId, String password) {
        boolean valid = false;
        String query = "SELECT * FROM Staff WHERE staff_id = ? AND password = ?";
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, staffId);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                valid = rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return valid;
    }

    /**
     * Resets the staff password after verifying the staff ID and email.
     * @param staffId The staff ID provided.
     * @param email The email provided for authentication.
     * @param newPassword The new password to set.
     * @return true if the password was successfully updated, false otherwise.
     */
    @Override
    public boolean resetPassword(String staffId, String email, String newPassword) {
        boolean updated = false;
        String query = "UPDATE Staff SET password = ? WHERE staff_id = ? AND email = ?";
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query)) {

            // Begin transaction
            con.setAutoCommit(false);
            ps.setString(1, newPassword);
            ps.setString(2, staffId);
            ps.setString(3, email);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                updated = true;
                con.commit();
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updated;
    }
}
