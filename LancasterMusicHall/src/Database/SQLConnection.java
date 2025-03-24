package Database;
// this supports 3 teams in connecting databases with the interfaces
import operations.entities.Activity;
import operations.entities.Booking;
import operations.entities.Seat;
import operations.entities.Venue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SQLConnection implements SQLInterface {

    // Example: Adjust these to your actual DB connection info
    private final String url = "jdbc:mysql://sst-stuproj.city.ac.uk:3306/in2033t23";
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
     * Both the Operations and Box Office teams can call this method.
     *
     * Assumptions:
     * - Booking's startDate and endDate are stored as ISO strings ("yyyy-MM-dd").
     * - holdExpiryDate is also in ISO format (if provided); otherwise, it is null.
     * - Venue is stored using its venueId.
     *
     * @param booking the Booking object to insert.
     * @return true if insertion was successful, false otherwise.
     */
    public boolean createBooking(Booking booking) {
        boolean success = false;
        // Include start_time and end_time columns
        String query = "INSERT INTO Booking "
                + "(booking_id, start_date, end_date, start_time, end_time, "
                + " activity_id, venue_id, held, hold_expiry_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query)) {

            con.setAutoCommit(false);

            // 1) Booking ID
            ps.setInt(1, booking.getId());

            // 2) Start Date (LocalDate -> java.sql.Date)
            ps.setDate(2, java.sql.Date.valueOf(booking.getStartDate()));

            // 3) End Date (LocalDate -> java.sql.Date)
            ps.setDate(3, java.sql.Date.valueOf(booking.getEndDate()));

            // 4) Start Time (LocalTime -> java.sql.Time)
            ps.setTime(4, java.sql.Time.valueOf(booking.getStartTime()));

            // 5) End Time (LocalTime -> java.sql.Time)
            ps.setTime(5, java.sql.Time.valueOf(booking.getEndTime()));

            // 6) Activity ID
            ps.setInt(6, booking.getActivityID());

            // 7) Venue ID
            if (booking.getVenue() != null) {
                ps.setInt(7, booking.getVenue().getVenueId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            // 8) Held
            ps.setBoolean(8, booking.isHeld());

            // 9) Hold Expiry Date
            if (booking.getHoldExpiryDate() != null && !booking.getHoldExpiryDate().isEmpty()) {
                ps.setDate(9, java.sql.Date.valueOf(booking.getHoldExpiryDate()));
            } else {
                ps.setNull(9, Types.DATE);
            }

            int rows = ps.executeUpdate();
            if (rows > 0) {
                con.commit();
                success = true;
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

    /**
     * Updates a marketing campaign in the database.
     * This method is called by the Marketing team.
     * After updating, it notifies all registered listeners so that the Operations team
     * (or any other interested modules) can refresh the latest marketing data.
     *
     * @param campaignId the ID of the marketing campaign.
     * @param newContent the new content or status for the campaign.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateMarketingCampaign(int campaignId, String newContent) {
        boolean success = false;
        String query = "UPDATE MarketingCampaign SET content = ? WHERE campaign_id = ?";

        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query)) {

            con.setAutoCommit(false);
            ps.setString(1, newContent);
            ps.setInt(2, campaignId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                con.commit();
                success = true;
                // Notify listeners that a marketing update occurred.
                notifyUpdateListeners("marketingUpdate", campaignId);
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

//    here is the method to get the bookings on calendar
    /**
     * Example read method to retrieve all bookings.
     * This can be used by the Operations team to refresh their view.
     */
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT booking_id, start_date, end_date, start_time, end_time, "
                + "activity_id, venue_id, held, hold_expiry_date "
                + "FROM Booking";

        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date").toLocalDate();
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();

                int activityId = rs.getInt("activity_id");
                int venueId = rs.getInt("venue_id");
                boolean held = rs.getBoolean("held");
                String holdExpiryDateStr = null;
                if (rs.getDate("hold_expiry_date") != null) {
                    holdExpiryDateStr = rs.getDate("hold_expiry_date").toString(); // or convert to a string
                }

                // For now, create a placeholder Activity/Venue (or fetch them properly if you have separate tables).
                Activity activity = new Activity(activityId, "Activity" + activityId);
                Venue venue = new Venue(venueId, "Venue" + venueId, "Hall", 300);

                // You can load seats from another table if you store them separately.
                List<Seat> seats = new ArrayList<>();

                Booking booking = new Booking(
                        bookingId,
                        startDate,
                        endDate,
                        startTime,
                        endTime,
                        activity,
                        venue,
                        held,
                        holdExpiryDateStr,
                        seats
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }


    // Additional methods (e.g., cancelBooking) can follow a similar pattern.
}
