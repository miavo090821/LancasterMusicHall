package Database;

import operations.entities.Activity;
import operations.entities.Booking;
import operations.entities.ContactDetails;
import operations.entities.Seat;
import operations.entities.Venue;

import javax.swing.table.DefaultTableModel;
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
import java.util.Properties;
import java.sql.Statement;


public class SQLConnection implements SQLInterface {

    // Updated connection info with new database name and user.
    // Note: The JDBC URL now reflects the new server and database ("in2033t23_a")
    private static final String url = "jdbc:mysql://sst-stuproj00.city.ac.uk:3306/in2033t23";
    private final String dbUser = "in2033t23_a";
    private final String dbPassword = "XUBLJfsYMHY";

    // Listeners for database update notifications.
    private final List<DatabaseUpdateListener> listeners = new ArrayList<>();

    // Load the MySQL JDBC Driver.
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public void registerUpdateListener(DatabaseUpdateListener listener) {
        listeners.add(listener);
    }

    private void notifyUpdateListeners(String updateType, Object data) {
        for (DatabaseUpdateListener listener : listeners) {
            listener.databaseUpdated(updateType, data);
        }
    }

    /**
     * Centralized method to obtain a database connection using the new connection details.
     *
     * @return a Connection object or null if connection fails.
     */
    public Connection getConnection() {
        Connection con = null;
        try {
            // Directly obtain a connection using the JDBC URL, username, and password.
            con = DriverManager.getConnection(url, dbUser, dbPassword);
            System.out.println("Connected to MySQL database successfully!");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e);
        }
        return con;
    }

    @Override
    public void connectToAndQueryDatabase(String username, String password) throws SQLException {
        try (Connection con = DriverManager.getConnection(url, username, password);
             Statement statement = con.createStatement();
             // Query to select the booking_id column from the Booking table.
             ResultSet resultSet = statement.executeQuery("SELECT booking_id FROM Booking")) {

            System.out.println("Booking IDs:");
            while (resultSet.next()) {
                int bookingId = resultSet.getInt("booking_id");
                System.out.println(bookingId);
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute query: " + e);
            e.printStackTrace();
        }
    }

    // Additional methods (loginStaff, resetPassword, createBooking, etc.) remain unchanged
    // or can be updated similarly if desired.

    @Override
    public boolean loginStaff(String staffId, String password) {
        boolean valid = false;
        String query = "SELECT 1 FROM Staff WHERE staff_id = ? AND password = ?";
        try (Connection con = getConnection();
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
        try (Connection con = getConnection();
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
     */
    public boolean createBooking(Booking booking) {
        String query = "INSERT INTO Booking (booking_id, booking_DateStart, booking_DateEnd, " +
                "activity_id, venue_id, held, hold_expiry_date, staff_ID, client_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            con.setAutoCommit(false);
            ps.setInt(1, booking.getId());
            ps.setDate(2, java.sql.Date.valueOf(booking.getStartDate()));
            ps.setDate(3, java.sql.Date.valueOf(booking.getEndDate()));
            ps.setInt(4, booking.getActivityId());
            ps.setInt(5, booking.getVenue().getVenueId());
            ps.setBoolean(6, booking.isHeld());
            if (booking.getHoldExpiryDate() != null && !booking.getHoldExpiryDate().isEmpty()) {
                ps.setDate(7, java.sql.Date.valueOf(booking.getHoldExpiryDate()));
            } else {
                ps.setNull(7, Types.DATE);
            }
            ps.setString(8, booking.getBookedBy());
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
     */
    // this is for the details of the booking on dairy panel
    public List<Booking> fetchDiaryBookings(LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.booking_id, b.booking_DateStart, b.booking_DateEnd, " +
                "bv.start_time, bv.end_time, " +
                "b.staff_id AS bookedBy, " +
                "b.booking_status, " +
                "v.venue_id, " +
                "b.location AS Room, " +
                "c.client_name AS companyName, " +
                "c.contact_name AS primaryContact, " +
                "c.phone_number AS contactPhone, " +
                "c.email AS contactEmail " +
                "FROM Booking b " +
                "JOIN BookingVenue bv ON b.booking_id = bv.booking_id " +
                "JOIN Venue v ON v.venue_id " +
                "LEFT JOIN Client c ON b.client_id = c.client_id " +
                "WHERE b.booking_DateStart BETWEEN ? AND ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                LocalDate bookingStart = rs.getDate("booking_DateStart").toLocalDate();
                LocalDate bookingEnd = rs.getDate("booking_DateEnd").toLocalDate();
                String bookingStatus = rs.getString("booking_status");
                boolean confirmed = bookingStatus != null && bookingStatus.equalsIgnoreCase("confirmed");

                // BookingVenue fields
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();

                // Venue fields: retrieve venue_id from Venue table, and room from b.location
                int venueId = rs.getInt("venue_id");
                String room = rs.getString("Room");

                // BookedBy field from b.staff_id
                String bookedBy = String.valueOf(rs.getInt("bookedBy"));

                // Client fields
                String companyName = rs.getString("companyName");
                String primaryContact = rs.getString("primaryContact");
                String contactPhone = rs.getString("contactPhone");
                String contactEmail = rs.getString("contactEmail");

                // No Activity is used in this version.
                Activity activity = null;
                // Create a Venue object using the retrieved venue_id and room (from b.location)
                Venue venue = new Venue(venueId, room, "Default Type", 0);
                ContactDetails contactDetails = null;
                if (primaryContact != null) {
                    contactDetails = new ContactDetails(primaryContact, contactPhone, contactEmail);
                }
                List<Seat> seats = new ArrayList<>();

                Booking booking = new Booking(
                        bookingId,
                        bookingStart,
                        bookingEnd,
                        startTime,
                        endTime,
                        activity,       // No activity
                        venue,
                        confirmed,
                        "",             // holdExpiryDate not provided in this query
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



    /**
     * Inserts a new booking (event) into the database.
     */
    // this is for Create new Event button
    public boolean insertEvent(Booking booking) {
        // SQL to insert core booking details into the Booking table.
        // Note: We no longer insert an activity_id; instead, we use b.location for room.
        String bookingQuery = "INSERT INTO Booking " +
                "(booking_id, booking_DateStart, booking_DateEnd, venue_id, booking_status, staff_ID, location, company_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        // SQL to insert time details into the BookingVenue table.
        String venueQuery = "INSERT INTO BookingVenue " +
                "(booking_id, start_time, end_time) " +
                "VALUES (?, ?, ?)";
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);

            // Insert into Booking table.
            try (PreparedStatement psBooking = con.prepareStatement(bookingQuery)) {
                psBooking.setInt(1, booking.getId());
                psBooking.setDate(2, java.sql.Date.valueOf(booking.getStartDate()));
                psBooking.setDate(3, java.sql.Date.valueOf(booking.getEndDate()));
                psBooking.setInt(4, booking.getVenue().getVenueId());
                // Set booking_status based on held flag. (You can adjust the logic as needed.)
                psBooking.setString(5, booking.isHeld() ? "held" : "confirmed");
                psBooking.setString(6, booking.getBookedBy());
                psBooking.setString(7, booking.getRoom()); // b.location
                psBooking.setString(8, booking.getCompanyName());

                int rowsBooking = psBooking.executeUpdate();
                if (rowsBooking <= 0) {
                    con.rollback();
                    return false;
                }
            }

            // Insert into BookingVenue table.
            try (PreparedStatement psVenue = con.prepareStatement(venueQuery)) {
                psVenue.setInt(1, booking.getId());
                psVenue.setTime(2, java.sql.Time.valueOf(booking.getStartTime()));
                psVenue.setTime(3, java.sql.Time.valueOf(booking.getEndTime()));

                int rowsVenue = psVenue.executeUpdate();
                if (rowsVenue <= 0) {
                    con.rollback();
                    return false;
                }
            }

            con.commit();
            notifyUpdateListeners("eventCreated", booking.getId());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


// room = b.location
    // activity remove
    /**
     * Fetches full event details for a given booking.
     */
    public Booking fetchEventDetails(int bookingId) {
        Booking booking = null;
        String query = "SELECT b.booking_id, b.booking_DateStart, b.booking_DateEnd, " +
                "bv.start_time, bv.end_time, " +
                "v.venue_id, " +
                "b.location as Room, " +
                "b.staff_ID as bookedBy, " +
                "c.client_name as companyName, c.contact_name, c.phone_number, c.email, " +
                "b.booking_status " +
                "FROM Booking b " +
                "JOIN BookingVenue bv ON b.booking_id = bv.booking_id " +
                "JOIN Venue v ON v.venue_id " +
                "JOIN Client c ON b.client_id = c.client_id " +
                "WHERE b.booking_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("booking_id");
                LocalDate bStartDate = rs.getDate("booking_DateStart").toLocalDate();
                LocalDate bEndDate = rs.getDate("booking_DateEnd").toLocalDate();
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();
                int venId = rs.getInt("venue_id");
                String room = rs.getString("Room"); // from b.location as Room
                String bookedBy = rs.getString("bookedBy");
                String companyName = rs.getString("companyName");
                String contactName = rs.getString("contact_name");
                String phone = rs.getString("phone_number");
                String email = rs.getString("email");
                String status = rs.getString("booking_status");
                boolean held = status.equalsIgnoreCase("held");

                // Remove activity completely.
                Activity activity = null; // no activity in this version
                Venue venue = new Venue(venId, room, "Type", 0); // using room from b.location
                ContactDetails contactDetails = new ContactDetails(contactName, phone, email);
                List<Seat> seats = new ArrayList<>();
                booking = new Booking(
                        id,
                        bStartDate,
                        bEndDate,
                        startTime,
                        endTime,
                        activity,  // activity is null
                        venue,
                        held,
                        "",        // holdExpiryDate (empty for now)
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

//    public List<Booking> getAllBookings() {
//        List<Booking> bookings = new ArrayList<>();
//        String query = "SELECT b.booking_id, b.booking_DateStart, b.booking_DateEnd, b.start_time, b.end_time, " +
//                "b.activity_id, b.venue_id, b.held, b.hold_expiry_date, " +
//                "b.staff_ID as bookedBy, b.room, b.company_name, " +
//                "c.primary_contact, c.telephone, c.email " +
//                "FROM Booking b " +
//                "LEFT JOIN ContactDetails c ON b.booking_id = c.booking_id";
//        try (Connection con = getConnection();
//             PreparedStatement ps = con.prepareStatement(query);
//             ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//                int bookingId = rs.getInt("booking_id");
//                LocalDate bStartDate = rs.getDate("booking_DateStart").toLocalDate();
//                LocalDate bEndDate = rs.getDate("booking_DateEnd").toLocalDate();
//                LocalTime startTime = rs.getTime("start_time").toLocalTime();
//                LocalTime endTime = rs.getTime("end_time").toLocalTime();
//                int activityId = rs.getInt("activity_id");
//                int venueId = rs.getInt("venue_id");
//                boolean held = rs.getBoolean("held");
//                String holdExpiry = rs.getString("hold_expiry_date");
//                String bookedBy = rs.getString("bookedBy");
//                String room = rs.getString("room");
//                String companyName = rs.getString("company_name");
//
//                ContactDetails contactDetails = null;
//                String primaryContact = rs.getString("primary_contact");
//                if (primaryContact != null) {
//                    contactDetails = new ContactDetails(primaryContact, rs.getString("telephone"), rs.getString("email"));
//                }
//                Activity activity = new Activity(activityId, "Activity" + activityId);
//                Venue venue = new Venue(venueId, "Venue" + venueId, "Hall", 300);
//                List<Seat> seats = new ArrayList<>();
//                Booking booking = new Booking(
//                        bookingId,
//                        bStartDate,
//                        bEndDate,
//                        startTime,
//                        endTime,
//                        activity,
//                        venue,
//                        held,
//                        holdExpiry != null ? holdExpiry : "",
//                        seats,
//                        bookedBy,
//                        room,
//                        companyName,
//                        contactDetails
//                );
//                bookings.add(booking);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return bookings;
//    }

    /**
     * Returns a DefaultTableModel containing bookings data.
     * The table has 5 columns:
     * "ID No.", "Name" (from location), "Start Date", "End Date", "Status"
     */
    // this is for the booking panel
    public DefaultTableModel getBookingsTableModel() {
        String[] columnNames = {"ID No.", "Name", "Start Date", "End Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String query = "SELECT booking_id, location, booking_DateStart, booking_DateEnd, booking_status FROM Booking";
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()){
                int bookingId = rs.getInt("booking_id");
                String location = rs.getString("location");
                String startDate = rs.getDate("booking_DateStart").toString();
                String endDate = rs.getDate("booking_DateEnd").toString();
                String status = rs.getString("booking_status");
                Object[] row = {bookingId, location, startDate, endDate, status};
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }


    // hold report panel data
    public static class ReportData {
        private final double totalRevenue;
        private final double totalProfit;

        public ReportData(double totalRevenue, double totalProfit) {
            this.totalRevenue = totalRevenue;
            this.totalProfit = totalProfit;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }

        public double getTotalProfit() {
            return totalProfit;
        }
    }

    /**
     * Retrieves total revenue and total profit for invoices within the given date range.
     * This method uses a SQL query that sums the total column and calculates profit as:
     * total_profit = SUM(total) - SUM(CAST(costs AS DECIMAL(10,2))).
     *
     * @param startDate The start date for the report.
     * @param endDate The end date for the report.
     * @return A ReportData object with total revenue and total profit, or null if an error occurs.
     */
    public ReportData getReportData(LocalDate startDate, LocalDate endDate) {
        ReportData reportData = null;
        // Note: costs is stored as VARCHAR, so we cast it to DECIMAL for proper calculation.
        String query = "SELECT " +
                "SUM(total) AS total_revenue, " +
                "SUM(total) - SUM(CAST(costs AS DECIMAL(10,2))) AS total_profit " +
                "FROM Invoice " +
                "WHERE date BETWEEN ? AND ?";
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double revenue = rs.getDouble("total_revenue");
                double profit = rs.getDouble("total_profit");
                reportData = new ReportData(revenue, profit);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportData;
    }

// for new report panel

    // --------------------------------------------------------------------------------
    // NEW: Method to automatically calculate the report date range
    // --------------------------------------------------------------------------------

    /**
     * Simple container class to hold a report date range.
     */
    public static class ReportDateRange {
        private final LocalDate startDate;
        private final LocalDate endDate;

        public ReportDateRange(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }
    }

    /**
     * Calculates a report date range based on the current local date and report type.
     * For a daily report, the range is just today.
     * For a quarterly report, it calculates the first day of the current quarter.
     * For a yearly report, it calculates the first day of the current year.
     *
     * @param reportType the report type ("daily", "quarterly", or "yearly").
     * @return a ReportDateRange containing the start and end dates.
     */
    public ReportDateRange getReportDateRange(String reportType) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        switch (reportType.toLowerCase()) {
            case "daily":
                // Daily report covers today only.
                startDate = today;
                break;
            case "quarterly":
                int currentMonth = today.getMonthValue();
                // Calculate the first month of the quarter.
                int startMonth = ((currentMonth - 1) / 3) * 3 + 1;
                startDate = LocalDate.of(today.getYear(), startMonth, 1);
                break;
            case "yearly":
                startDate = LocalDate.of(today.getYear(), 1, 1);
                break;
            default:
                // Fallback to today's date.
                startDate = today;
        }
        return new ReportDateRange(startDate, today);
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
