package Database;

import operations.entities.Activity;
import operations.entities.Booking;
import operations.entities.ContactDetails;
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

    // Connection info: using v501.city.ac.uk and your database "in2033t23"
    private final String url = "jdbc:mysql://v501.city.ac.uk:3306/in2033t23";
    private final String dbUser = "in2033t23_a";
    private final String dbPassword = "XUBLJfsYMHY";

    // Listeners for database update notifications
    private final List<DatabaseUpdateListener> listeners = new ArrayList<>();

    public void registerUpdateListener(DatabaseUpdateListener listener) {
        listeners.add(listener);
    }

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
                valid = rs.next();
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
                con.commit();
                updated = true;
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
     * Inserts a new booking into the database.
     * This method assumes the Booking table has columns:
     * booking_id, booking_DateStart, booking_DateEnd, activity_id, venue_id,
     * held, hold_expiry_date, staff_ID, client_id.
     * The start_time and end_time are stored in the BookingVenue table.
     *
     * For simplicity, we assume that the insertion into BookingVenue is handled separately.
     *
     * @param booking The Booking object to insert.
     * @return true if insertion was successful.
     */
    public boolean createBooking(Booking booking) {
        String query = "INSERT INTO Booking (booking_id, booking_DateStart, booking_DateEnd, " +
                "activity_id, venue_id, held, hold_expiry_date, staff_ID, client_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query)) {
            con.setAutoCommit(false);
            ps.setInt(1, booking.getId());
            ps.setDate(2, java.sql.Date.valueOf(booking.getStartDate()));
            ps.setDate(3, java.sql.Date.valueOf(booking.getEndDate()));
            ps.setInt(4, booking.getActivityId());  // using helper method
            ps.setInt(5, booking.getVenue().getVenueId());
            ps.setBoolean(6, booking.isHeld());
            if (booking.getHoldExpiryDate() != null && !booking.getHoldExpiryDate().isEmpty()) {
                ps.setDate(7, java.sql.Date.valueOf(booking.getHoldExpiryDate()));
            } else {
                ps.setNull(7, Types.DATE);
            }
            ps.setString(8, booking.getBookedBy());  // Staff ID as string
            // Here we assume that the company name comes from Client; so we store client_id as integer.
            // For simplicity, we pass the client_id in the companyName field as a string.
            ps.setString(9, booking.getCompanyName());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                con.commit();
                notifyUpdateListeners("bookingCreated", booking.getId());
                return true;
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Fetches diary bookings (for the given date range) from the database.
     * Joins with ContactDetails (if available) to retrieve client contact info.
     *
     * This query selects fields from the Booking table.
     *
     * @param startDate Start date of range.
     * @param endDate End date of range.
     * @return List of Booking objects.
     */
    public List<Booking> fetchDiaryBookings(LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.booking_id, b.booking_DateStart, b.booking_DateEnd, " +
                "bv.start_time, bv.end_time, " +
                "b.activity_id, b.venue_id, b.held, b.hold_expiry_date, " +
                "b.staff_ID as bookedBy, " +
                "v.room, " +
                "c.client_name as companyName, c.contact_name, c.phone_number, c.email " +
                "FROM Booking b " +
                "JOIN BookingVenue bv ON b.booking_id = bv.booking_id " +
                "JOIN Venue v ON b.venue_id = v.venue_id " +
                "LEFT JOIN Client c ON b.client_id = c.client_id " +
                "WHERE b.booking_DateStart BETWEEN ? AND ?";
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("booking_id");
                LocalDate bStartDate = rs.getDate("booking_DateStart").toLocalDate();
                LocalDate bEndDate = rs.getDate("booking_DateEnd").toLocalDate();
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();
                int actId = rs.getInt("activity_id");
                int venId = rs.getInt("venue_id");
                boolean held = rs.getBoolean("held");
                String holdExpiry = rs.getString("hold_expiry_date") != null ? rs.getString("hold_expiry_date") : "";
                String bookedBy = rs.getString("bookedBy");
                String room = rs.getString("room");
                String companyName = rs.getString("companyName");
                String contactName = rs.getString("contact_name");
                String phone = rs.getString("phone_number");
                String email = rs.getString("email");

                Activity activity = new Activity(actId, "Activity " + actId);
                Venue venue = new Venue(venId, room, "Type", 0);
                ContactDetails contactDetails = null;
                if (contactName != null) {
                    contactDetails = new ContactDetails(contactName, phone, email);
                }
                List<Seat> seats = new ArrayList<>();
                Booking booking = new Booking(
                        id,
                        bStartDate,
                        bEndDate,
                        startTime,
                        endTime,
                        activity,
                        venue,
                        held,
                        holdExpiry,
                        seats,
                        bookedBy,
                        room,
                        companyName,
                        contactDetails
                );
                bookings.add(booking);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // this is the insert method for Calendar panel New Event button

    /**
     * Inserts a new booking (event) into the database.
     * Assumes the Booking table has the following columns:
     * - booking_id (INT)
     * - booking_DateStart (DATE)
     * - booking_DateEnd (DATE)
     * - start_time (TIME)
     * - end_time (TIME)
     * - activity_id (INT)
     * - venue_id (INT)
     * - held (BOOLEAN)
     * - hold_expiry_date (DATE, nullable)
     * - staff_ID (VARCHAR) - represents the ID of the staff who booked the event
     * - room (VARCHAR) - the room name from the Venue
     * - company_name (VARCHAR) - the company name from the Client
     *
     * @param booking the Booking object containing event details.
     * @return true if the insertion was successful, false otherwise.
     */
    public boolean insertBooking(Booking booking) {
        String query = "INSERT INTO Booking (booking_id, booking_DateStart, booking_DateEnd, start_time, end_time, " +
                "activity_id, venue_id, held, hold_expiry_date, staff_ID, room, company_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query)) {

            con.setAutoCommit(false);

            // Set booking ID
            ps.setInt(1, booking.getId());
            // Set start and end dates (as java.sql.Date)
            ps.setDate(2, java.sql.Date.valueOf(booking.getStartDate()));
            ps.setDate(3, java.sql.Date.valueOf(booking.getEndDate()));
            // Set start and end times (as java.sql.Time)
            ps.setTime(4, java.sql.Time.valueOf(booking.getStartTime()));
            ps.setTime(5, java.sql.Time.valueOf(booking.getEndTime()));
            // Set activity ID and venue ID (extracted from associated objects)
            ps.setInt(6, booking.getActivityId());
            ps.setInt(7, booking.getVenue().getVenueId());
            // Set held flag
            ps.setBoolean(8, booking.isHeld());
            // Set hold expiry date if provided, otherwise set SQL NULL
            if (booking.getHoldExpiryDate() != null && !booking.getHoldExpiryDate().isEmpty()) {
                ps.setDate(9, java.sql.Date.valueOf(booking.getHoldExpiryDate()));
            } else {
                ps.setNull(9, java.sql.Types.DATE);
            }
            // Set staff_ID (bookedBy), room, and company_name
            ps.setString(10, booking.getBookedBy());
            ps.setString(11, booking.getRoom());
            ps.setString(12, booking.getCompanyName());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                con.commit();
                notifyUpdateListeners("bookingCreated", booking.getId());
                return true;
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }






// this is the fetch method for dairy booking/event details
    /**
     * Fetch full event details for a given booking.
     * This query joins Booking, BookingVenue, Activity, Venue, and Client tables.
     *
     * @param bookingId The booking/event ID.
     * @return A Booking object with complete details, or null if not found.
     */
    public Booking fetchEventDetails(int bookingId) {
        Booking booking = null;
        String query = "SELECT b.booking_id, b.booking_DateStart, b.booking_DateEnd, " +
                "bv.start_time, bv.end_time, " +
                "a.activity_id, " +
                "v.venue_id, v.room, " +
                "b.staff_ID as bookedBy, " +
                "c.client_name as companyName, c.contact_name, c.phone_number, c.email, " +
                "b.booking_status " +
                "FROM Booking b " +
                "JOIN BookingVenue bv ON b.booking_id = bv.booking_id " +
                "JOIN Activity a ON b.activity_id = a.activity_id " +
                "JOIN Venue v ON b.venue_id = v.venue_id " +
                "JOIN Client c ON b.client_id = c.client_id " +
                "WHERE b.booking_id = ?";
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("booking_id");
                LocalDate bStartDate = rs.getDate("booking_DateStart").toLocalDate();
                LocalDate bEndDate = rs.getDate("booking_DateEnd").toLocalDate();
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();
                int actId = rs.getInt("activity_id");
                int venId = rs.getInt("venue_id");
                String room = rs.getString("room");
                String bookedBy = rs.getString("bookedBy");
                String companyName = rs.getString("companyName");
                String contactName = rs.getString("contact_name");
                String phone = rs.getString("phone_number");
                String email = rs.getString("email");
                String status = rs.getString("booking_status");
                boolean held = status.equalsIgnoreCase("held");

                Activity activity = new Activity(actId, "Activity " + actId);
                Venue venue = new Venue(venId, room, "Type", 0);
                ContactDetails contactDetails = new ContactDetails(contactName, phone, email);
                List<Seat> seats = new ArrayList<>();
                booking = new Booking(
                        id,
                        bStartDate,
                        bEndDate,
                        startTime,
                        endTime,
                        activity,
                        venue,
                        held,
                        "",  // Assume holdExpiryDate is empty for now
                        seats,
                        bookedBy,
                        room,
                        companyName,
                        contactDetails
                );
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return booking;
    }

    // Additional methods such as getAllBookings(), updateBooking, deleteBooking, etc. can be added here.

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.booking_id, b.booking_DateStart, b.booking_DateEnd, b.start_time, b.end_time, " +
                "b.activity_id, b.venue_id, b.held, b.hold_expiry_date, " +
                "b.staff_ID as bookedBy, b.room, b.company_name, " +
                "c.primary_contact, c.telephone, c.email " +
                "FROM Booking b " +
                "LEFT JOIN ContactDetails c ON b.booking_id = c.booking_id";
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                LocalDate bStartDate = rs.getDate("booking_DateStart").toLocalDate();
                LocalDate bEndDate = rs.getDate("booking_DateEnd").toLocalDate();
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();
                int activityId = rs.getInt("activity_id");
                int venueId = rs.getInt("venue_id");
                boolean held = rs.getBoolean("held");
                String holdExpiry = rs.getString("hold_expiry_date");
                String bookedBy = rs.getString("bookedBy");
                String room = rs.getString("room");
                String companyName = rs.getString("company_name");

                ContactDetails contactDetails = null;
                String primaryContact = rs.getString("primary_contact");
                if (primaryContact != null) {
                    contactDetails = new ContactDetails(primaryContact, rs.getString("telephone"), rs.getString("email"));
                }
                Activity activity = new Activity(activityId, "Activity" + activityId);
                Venue venue = new Venue(venueId, "Venue" + venueId, "Hall", 300);
                List<Seat> seats = new ArrayList<>();
                Booking booking = new Booking(
                        bookingId,
                        bStartDate,
                        bEndDate,
                        startTime,
                        endTime,
                        activity,
                        venue,
                        held,
                        holdExpiry != null ? holdExpiry : "",
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
        // Implement as needed.
        return null;
    }

    public void updateBooking(Booking booking) {
        // Implement update logic as needed.
    }

    public void deleteBooking(int id) {
        // Implement delete logic as needed.
    }
}
