package Database;

import operations.entities.Booking;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SQLConnection implements SQLInterface {

    // Example: Adjust these to your actual DB connection info
    private final String url = "jdbc:mysql://sst-stuproj.city.ac.uk:3306/xxx";
    private final String dbUser = "your_db_username";
    private final String dbPassword = "your_db_password";

    // Listeners to notify on any database update
    private final List<DatabaseUpdateListener> listeners = new ArrayList<>();

    /**
     * Registers a listener to be notified on database updates.
     */
    public void registerUpdateListener(DatabaseUpdateListener listener) {
        listeners.add(listener);
    }

    /**
     * Notifies all registered listeners about a specific update.
     */
    private void notifyUpdateListeners(String updateType, Object data) {
        for (DatabaseUpdateListener listener : listeners) {
            listener.databaseUpdated(updateType, data);
        }
    }

    @Override
    public void connectToAndQueryDatabase(String username, String password) throws SQLException {
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
            System.err.println("Failed to execute test query.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean loginStaff(String staffId, String password) {
        boolean valid = false;
        String query = "SELECT 1 FROM Staff WHERE staff_id = ? AND password = ?";

        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, staffId);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                valid = rs.next();  // A row indicates valid credentials.
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return valid;
    }

    @Override
    public boolean resetPassword(String staffId, String email, String newPassword) {
        boolean updated = false;
        String query = "UPDATE Staff SET password = ? WHERE staff_id = ? AND email = ?";

        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query)) {

            con.setAutoCommit(false);
            ps.setString(1, newPassword);
            ps.setString(2, staffId);
            ps.setString(3, email);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                updated = true;
                con.commit();
                // Notify listeners that a password was reset.
                notifyUpdateListeners("passwordReset", staffId);
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updated;
    }

    /**
     * Creates a new booking record in the database.
     * Both the Operation team and the Box Office team can call this method.
     *
     * Assumptions:
     * - Booking's startDate and endDate are stored as ISO strings (yyyy-MM-dd).
     * - holdExpiryDate is also in ISO format (if provided); otherwise, it is null.
     * - Venue is stored using its venueId.
     *
     * @param booking the Booking object to insert.
     * @return true if insertion was successful, false otherwise.
     */
    public boolean createBooking(Booking booking) {
        boolean success = false;
        String query = "INSERT INTO Booking " +
                "(booking_id, start_date, end_date, activity_id, venue_id, held, hold_expiry_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query)) {

            con.setAutoCommit(false);

            // Set booking ID
            ps.setInt(1, booking.getId());

            // Convert date strings (ISO format "yyyy-MM-dd") to java.sql.Date.
            ps.setDate(2, java.sql.Date.valueOf(booking.getStartDate()));
            ps.setDate(3, java.sql.Date.valueOf(booking.getEndDate()));

            // Activity ID from the associated Activity.
            ps.setInt(4, booking.getActivityID());

            // Use getVenueId() from Venue class.
            ps.setInt(5, (booking.getVenue() != null ? booking.getVenue().getVenueId() : 0));

            // Held flag.
            ps.setBoolean(6, booking.isHeld());

            // Hold expiry date: if provided, convert to sql.Date; else set as NULL.
            if (booking.getHoldExpiryDate() != null && !booking.getHoldExpiryDate().isEmpty()) {
                ps.setDate(7, java.sql.Date.valueOf(booking.getHoldExpiryDate()));
            } else {
                ps.setNull(7, Types.DATE);
            }

            int rows = ps.executeUpdate();
            if (rows > 0) {
                con.commit();
                success = true;
                // Notify all listeners that a booking was created.
                notifyUpdateListeners("bookingCreated", booking.getId());
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }

    /**
     * Example method to update a booking in the DB.
     * This can be called when either team modifies an existing booking.
     */
    public boolean updateBookingInDB(int bookingId, String newDate) {
        boolean success = false;
        String query = "UPDATE Booking SET start_date = ?, end_date = ? WHERE booking_id = ?";

        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query)) {

            con.setAutoCommit(false);
            // For simplicity, update both start and end dates to the newDate.
            ps.setDate(1, java.sql.Date.valueOf(newDate));
            ps.setDate(2, java.sql.Date.valueOf(newDate));
            ps.setInt(3, bookingId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                con.commit();
                success = true;
                notifyUpdateListeners("bookingUpdate", bookingId);
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }
}
