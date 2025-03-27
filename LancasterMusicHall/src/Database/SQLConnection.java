package Database;

import operations.entities.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SQLConnection implements SQLInterface {

    // Updated connection info: use v501.city.ac.uk as the server address.
    private final String url = "jdbc:mysql://v501.city.ac.uk:3306/in2033t23"; // Replace "xxx" with your actual database name
    private final String dbUser = "in2033t23_a";
    private final String dbPassword = "XUBLJfsYMHY";

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
     * <p>
     * Assumptions:
     * - Booking's startDate and endDate are stored as ISO strings ("yyyy-MM-dd").
     * - start_time and end_time are stored as TIME.
     * - holdExpiryDate is also in ISO format (if provided); otherwise, it is null.
     * - Venue is stored using its venueId.
     *
     * @param booking the Booking object to insert.
     * @return true if insertion was successful, false otherwise.
     */
    public boolean createBooking(Booking booking) {
        return false;
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
            // For simplicity, update both start_date and end_date to the newDate.
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
                notifyUpdateListeners("marketingUpdate", campaignId);
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * Example read method to retrieve all bookings.
     * This can be used by the Operations team to refresh their view.
     */
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.booking_id, b.start_date, b.end_date, b.start_time, b.end_time, " +
                "b.activity_id, b.venue_id, b.held, b.hold_expiry_date, " +
                "b.booked_by, b.room, b.company_name, " +
                "c.primary_contact, c.telephone, c.email " + // Contact details fields
                "FROM Booking b " +
                "LEFT JOIN ContactDetails c ON b.booking_id = c.booking_id"; // Join with contact table

        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Extract basic booking info
                int bookingId = rs.getInt("booking_id");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date").toLocalDate();
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();

                // Extract activity and venue info
                int activityId = rs.getInt("activity_id");
                int venueId = rs.getInt("venue_id");
                boolean confirm = rs.getBoolean("confirm");
                String holdExpiryDate = rs.getString("hold_expiry_date");

                // Extract booking info
                String bookedBy = rs.getString("booked_by");
                String room = rs.getString("room");
                String companyName = rs.getString("company_name");

                // Create ContactDetails object
                ContactDetails contactDetails = null;
                String primaryContact = rs.getString("primary_contact");
                if (primaryContact != null) {  // Only create if contact info exists
                    contactDetails = new ContactDetails(
                            primaryContact,
                            rs.getString("telephone"),
                            rs.getString("email")
                    );
                }

                // Create objects
                Activity activity = new Activity(activityId, "Activity" + activityId); // Placeholder
                Venue venue = new Venue(venueId, "Venue" + venueId, "Hall", 300); // Placeholder
                List<Seat> seats = new ArrayList<>(); // Empty list - load separately if needed

                // Create Booking object
                Booking booking = new Booking(
                        bookingId,
                        startDate,
                        endDate,
                        startTime,
                        endTime,
                        activity,
                        venue,
                        confirm,
                        seats,
                        bookedBy,
                        room,
                        companyName,
                        contactDetails
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Venue> getAllVenues() {
        return null;
    }

    public void updateBooking(Booking booking) {
    }

    public void deleteBooking(int id) {
    }
}
// we will need more queries for the calendar, create event form, display the diary
// create booking, create the report, on selecting the day.
// the report will need to be managed carefully and automatically as ut==it require
// the selection then pass it to insertion , then pass it to sql, then retrieve back.