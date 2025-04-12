package Database;

import operations.entities.Event;
import java.io.File;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.sql.Statement;

/**
 * The SQLConnection class manages connections to the MySQL database and provides methods to
 * perform various database operations such as querying, updating, inserting, and deleting records.
 * <p>
 * It implements the SQLInterface and includes methods for user authentication, booking management,
 * financial reporting, and handling custom stored function calls.
 * </p>
 */
public class SQLConnection implements SQLInterface {
    /**
     * JDBC URL for database connection. Includes server address, port, and database name.
     * <p><b>Format:</b> jdbc:mysql://host:port/database</p>
     */
    private static final String url = "jdbc:mysql://sst-stuproj00.city.ac.uk:3306/in2033t23";

    /**
     * Database username with restricted permissions.
     * <p><b>Security:</b> Should only have necessary CRUD permissions</p>
     */
    private final String dbUser = "in2033t23_a";

    /**
     * Database password stored as final field.
     * <p><b>Security:</b> Never logged or exposed in any way</p>
     */
    private final String dbPassword = "XUBLJfsYMHY";

    /**
     * List of registered listeners for database update events.
     * <p><b>Usage:</b> Notified after significant database changes</p>
     */
    private final List<DatabaseUpdateListener> listeners = new ArrayList<>();

    /**
     * Static initializer for loading MySQL JDBC driver.
     * <p><b>Error Handling:</b> Fails fast if driver not available</p>
     */
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    /**
     * Constructs a new SQLConnection instance that implements the SQLInterface.
     * <p>
     * This default constructor initializes the database connection handler
     * </p>
     */
    public SQLConnection() {
        // Default constructor
    }

    /**
     * Registers a listener to receive database update notifications.
     *
     * @param listener the {@code DatabaseUpdateListener} to add to the listeners list
     */
    public void registerUpdateListener(DatabaseUpdateListener listener) {
        listeners.add(listener);
    }

    /**
     * Notifies all registered listeners of a database update event.
     *
     * @param updateType a {@code String} representing the type of update (e.g., "passwordReset", "bookingUpdated")
     * @param data       additional data related to the update event
     */
    private void notifyUpdateListeners(String updateType, Object data) {
        for (DatabaseUpdateListener listener : listeners) {
            listener.databaseUpdated(updateType, data);
        }
    }

    /**
     * Centralized method to obtain a database connection using the current connection details.
     *
     * @return a {@code Connection} object, or {@code null} if the connection fails
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

    /**
     * Connects to the database using the provided username and password, and queries the Booking table
     * to print all booking IDs.
     *
     * @param username the username for the database connection
     * @param password the password for the database connection
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Authenticates a staff member using the provided staff ID and password.
     *
     * @param staffId  the staff ID to log in
     * @param password the password for the staff member
     * @return {@code true} if the staff ID is valid and login is successful; {@code false} otherwise
     */
    @Override
    public boolean loginStaff(String staffId, String password) {
        boolean valid = false;
        // Use the correct column name, e.g. "staff_id", instead of "id"
        String query = "SELECT staff_id FROM Staff WHERE staff_id = ? AND password = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, staffId);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    valid = true;
                    // Retrieve the staff id from the correct column and store it.
                    int id = rs.getInt("staff_id");
                    setCurrentStaffId(id);
                    System.out.println("Logged in Staff ID: " + id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return valid;
    }

    /**
     * Resets the password for a staff member, given the staff ID, email, and new password.
     * This method updates the Staff table and notifies update listeners upon success.
     *
     * @param staffId    the staff ID for the password reset
     * @param email      the email associated with the staff account
     * @param newPassword the new password to be set
     * @return {@code true} if the password was updated successfully; {@code false} otherwise
     */
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

    // Try to get staff id.
    private Integer currentStaffId;

    /**
     * Sets the current staff ID after successful authentication.
     *
     * @param staffId the staff ID to set
     */
    public void setCurrentStaffId(Integer staffId) {
        this.currentStaffId = staffId;
    }

    /**
     * Retrieves the current staff ID.
     *
     * @return the current staff ID, or {@code null} if not set
     */
    public Integer getCurrentStaffId() {
        return currentStaffId;
    }

    /**
     * Retrieves a {@code DefaultTableModel} containing bookings data.
     * <p>
     * The table contains the following columns: "ID No.", "Name", "Start Date", "End Date", and "Status".
     * Data is obtained by joining the Booking and Clients tables.
     * </p>
     *
     * @return a {@code DefaultTableModel} with booking information
     */
    public DefaultTableModel getBookingsTableModel() {
        String[] columnNames = {"ID No.", "Name", "Start Date", "End Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Updated query with a JOIN on Clients to get the "Company Name"
        String query = "SELECT B.booking_id, C.`Company Name` AS clientName, "
                + "B.booking_DateStart, B.booking_DateEnd, B.booking_status "
                + "FROM Booking B "
                + "JOIN Clients C ON B.client_id = C.client_id";

        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                String clientName = rs.getString("clientName");
                String startDate = rs.getDate("booking_DateStart").toString();
                String endDate = rs.getDate("booking_DateEnd").toString();
                String status = rs.getString("booking_status");
                Object[] row = {bookingId, clientName, startDate, endDate, status};
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    /**
     * A helper class for encapsulating financial report data.
     */
    public static class ReportData {
        private final double totalRevenue;
        private final double totalProfit;

        /**
         * Constructs a new {@code ReportData} instance.
         *
         * @param totalRevenue the total revenue
         * @param totalProfit  the total profit
         */
        public ReportData(double totalRevenue, double totalProfit) {
            this.totalRevenue = totalRevenue;
            this.totalProfit = totalProfit;
        }

        /**
         * Returns the total revenue.
         *
         * @return the total revenue
         */
        public double getTotalRevenue() {
            return totalRevenue;
        }

        /**
         * Returns the total profit.
         *
         * @return the total profit
         */
        public double getTotalProfit() {
            return totalProfit;
        }
    }

    /**
     * Retrieves total revenue and total profit for financial records within the given date range.
     *
     * @param startDate the start date for the report
     * @param endDate   the end date for the report
     * @return a {@code ReportData} object with total revenue and total profit, or {@code null} if an error occurs
     */
    public ReportData getReportData(LocalDate startDate, LocalDate endDate) {
        ReportData reportData = null;
        String query = "SELECT " +
                "SUM(revenue) AS total_revenue, " +
                "SUM(profit) AS total_profit " +
                "FROM FinancialRecord " +
                "WHERE financial_record_date BETWEEN ? AND ?";
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

    /**
     * Inserts a full booking including client details, events, contract, and an associated invoice record.
     * <p>
     * This method performs multiple insert operations:
     * <ol>
     *   <li>Insert into Clients and retrieve generated client_id</li>
     *   <li>Insert into Booking and retrieve generated booking_id</li>
     *   <li>Insert an Invoice record for the booking</li>
     *   <li>Insert each Event record</li>
     *   <li>Insert Contract details if a file is provided</li>
     * </ol>
     *
     * @param bookingEventName the event name for the booking (not used if event object contains its own name)
     * @param bookingStartDate the booking start date
     * @param bookingEndDate   the booking end date
     * @param bookingStatus    the booking status
     * @param companyName      the client company name
     * @param primaryContact   the client primary contact name
     * @param telephone        the client telephone number
     * @param email            the client email address
     * @param events           the list of {@code Event} objects to be inserted
     * @param customerBillTotal the total bill amount for the booking
     * @param ticketPrice      the ticket price for the booking
     * @param customerAccount  the customer account number
     * @param customerSortCode the customer sort code
     * @param streetAddress    the client's street address
     * @param city             the client's city
     * @param postcode         the client's postcode
     * @param paymentDueDate   the payment due date
     * @param paymentStatus    the payment status
     * @param contractDetails  details of the contract
     * @param contractFile     a {@code File} object representing the contract file (or {@code null} if none)
     * @param maxDiscount      the maximum discount allowed
     * @param staffId          the staff ID who processed the booking
     * @return {@code true} if the full booking was inserted successfully; {@code false} otherwise
     */
    public boolean insertFullBooking(String bookingEventName,
                                     LocalDate bookingStartDate,
                                     LocalDate bookingEndDate,
                                     String bookingStatus,
                                     String companyName,
                                     String primaryContact,
                                     String telephone,
                                     String email,
                                     List<Event> events,
                                     double customerBillTotal,
                                     double ticketPrice,
                                     String customerAccount,
                                     String customerSortCode,
                                     String streetAddress,
                                     String city,
                                     String postcode,
                                     LocalDate paymentDueDate,
                                     String paymentStatus,
                                     String contractDetails,
                                     File contractFile,
                                     double maxDiscount,
                                     Integer staffId) {
        // 1. Booking: Now includes payment_due_date, staff_id, client_id, and max_discount.
        String insertBooking = "INSERT INTO Booking (booking_DateStart, booking_DateEnd, booking_status, ticket_price, total_cost, payment_status, payment_due_date, staff_id, client_id, max_discount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // 2. Event: Now includes booking_id, client_id, location, description, layout, and max_discount.
        String insertEvent = "INSERT INTO Event (name, start_date, end_date, start_time, end_time, event_type, venue_id, booking_id, client_id, location, description, layout, max_discount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // 3. Clients: Now includes Street Address, City, Postcode.
        String insertClient = "INSERT INTO Clients (`Company Name`, `Contact Name`, `Phone Number`, `Contact Email`, `Customer Account Number`, `Customer Sort Code`, `Payment Due Date`, `Street Address`, `City`, `Postcode`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // 4. Contract: remains unchanged.
        String insertContract = "INSERT INTO Contract (details, client_id, booking_id, file_data) VALUES (?, ?, ?, ?)";
        // 5. NEW: Invoice insertion.
        String insertInvoice = "INSERT INTO Invoice (booking_id, date, total, client_id) VALUES (?, ?, ?, ?)";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            int clientId = 0;
            int bookingId = 0;

            // 1. Insert Client details and retrieve the generated client_id.
            try (PreparedStatement psClient = con.prepareStatement(insertClient, Statement.RETURN_GENERATED_KEYS)) {
                psClient.setString(1, companyName);
                psClient.setString(2, primaryContact);
                psClient.setString(3, telephone);
                psClient.setString(4, email);
                psClient.setString(5, customerAccount);
                psClient.setString(6, customerSortCode);
                if (paymentDueDate != null) {
                    psClient.setDate(7, java.sql.Date.valueOf(paymentDueDate));
                } else {
                    psClient.setDate(7, null);
                }
                psClient.setString(8, streetAddress);
                psClient.setString(9, city);
                psClient.setString(10, postcode);
                psClient.executeUpdate();
                try (ResultSet rs = psClient.getGeneratedKeys()) {
                    if (rs.next()) {
                        clientId = rs.getInt(1);
                    } else {
                        throw new Exception("Client ID not generated.");
                    }
                }
            }

            // 2. Insert Booking details and retrieve the generated booking_id.
            try (PreparedStatement psBooking = con.prepareStatement(insertBooking, Statement.RETURN_GENERATED_KEYS)) {
                psBooking.setDate(1, java.sql.Date.valueOf(bookingStartDate));
                psBooking.setDate(2, java.sql.Date.valueOf(bookingEndDate));
                psBooking.setString(3, bookingStatus);
                psBooking.setDouble(4, ticketPrice);
                psBooking.setDouble(5, customerBillTotal); // total_cost
                psBooking.setString(6, paymentStatus);
                if (paymentDueDate != null) {
                    psBooking.setDate(7, java.sql.Date.valueOf(paymentDueDate));
                } else {
                    psBooking.setDate(7, null);
                }
                if (staffId != null) {
                    psBooking.setInt(8, staffId);
                } else {
                    psBooking.setNull(8, java.sql.Types.INTEGER);
                }
                psBooking.setInt(9, clientId);
                psBooking.setDouble(10, maxDiscount);
                psBooking.executeUpdate();
                try (ResultSet rs = psBooking.getGeneratedKeys()) {
                    if (rs.next()) {
                        bookingId = rs.getInt(1);
                    } else {
                        throw new Exception("Booking ID not generated.");
                    }
                }
            }

            // 5. NEW: Insert Invoice record.
            try (PreparedStatement psInvoice = con.prepareStatement(insertInvoice)) {
                psInvoice.setInt(1, bookingId);
                psInvoice.setDate(2, java.sql.Date.valueOf(bookingStartDate));
                psInvoice.setDouble(3, customerBillTotal);
                psInvoice.setInt(4, clientId);
                psInvoice.executeUpdate();
            }

            // 3. Insert each Event.
            for (Event event : events) {
                try (PreparedStatement psEvent = con.prepareStatement(insertEvent)) {
                    // Use the event's own name (do not fallback to bookingEventName).
                    String eventName = (event.getName() != null && !event.getName().isEmpty()) ? event.getName() : "";
                    psEvent.setString(1, eventName);
                    psEvent.setDate(2, java.sql.Date.valueOf(event.getStartDate()));
                    psEvent.setDate(3, java.sql.Date.valueOf(event.getEndDate()));
                    psEvent.setTime(4, java.sql.Time.valueOf(event.getStartTime()));
                    psEvent.setTime(5, java.sql.Time.valueOf(event.getEndTime()));
                    psEvent.setString(6, event.getEventType());
                    psEvent.setInt(7, event.getVenue().getVenueId());
                    psEvent.setInt(8, bookingId);
                    psEvent.setInt(9, clientId);
                    psEvent.setString(10, event.getRoom());
                    psEvent.setString(11, event.getDescription());
                    psEvent.setString(12, event.getLayout());
                    psEvent.setDouble(13, maxDiscount);
                    psEvent.executeUpdate();
                }
            }

            // 4. Insert Contract details, linking to the new booking and client.
            if (contractFile != null) {
                try (PreparedStatement psContract = con.prepareStatement(insertContract)) {
                    psContract.setString(1, contractDetails != null ? contractDetails : "N/A");
                    psContract.setInt(2, clientId);
                    psContract.setInt(3, bookingId);
                    psContract.setBlob(4, new java.io.FileInputStream(contractFile));
                    psContract.executeUpdate();
                }
            }

            con.commit();
            notifyUpdateListeners("fullBookingCreated", null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Calls the MySQL stored function {@code calculateMainHallCost} to determine the cost for the main hall.
     *
     * @param p_date      the date for which to calculate the cost
     * @param p_rate_type the rate type (e.g., standard, premium)
     * @param p_hours     the number of hours the hall is booked
     * @return the calculated cost as a {@code double}
     */
    public double calculateMainHallCost(java.sql.Date p_date, String p_rate_type, int p_hours) {
        double cost = 0.0;
        String query = "SELECT calculateMainHallCost(?, ?, ?) AS cost";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDate(1, p_date);
            ps.setString(2, p_rate_type);
            ps.setInt(3, p_hours);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cost = rs.getDouble("cost");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return cost;
    }

    /**
     * Calls the MySQL stored function {@code calculateSmallHallCost} to determine the cost for the small hall.
     *
     * @param p_date      the date for which to calculate the cost
     * @param p_rate_type the rate type (e.g., standard, premium)
     * @param p_hours     the number of hours the hall is booked
     * @return the calculated cost as a {@code double}
     */
    public double calculateSmallHallCost(java.sql.Date p_date, String p_rate_type, int p_hours) {
        double cost = 0.0;
        String query = "SELECT calculateSmallHallCost(?, ?, ?) AS cost";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDate(1, p_date);
            ps.setString(2, p_rate_type);
            ps.setInt(3, p_hours);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cost = rs.getDouble("cost");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return cost;
    }

    /**
     * Calls the MySQL stored function {@code calculateRehearsalCost} to determine the rehearsal cost.
     *
     * @param p_date      the date for which to calculate the cost
     * @param p_rate_type the rate type for rehearsal cost
     * @param p_hours     the number of hours for the rehearsal
     * @return the calculated cost as a {@code double}
     */
    public double calculateRehearsalCost(java.sql.Date p_date, String p_rate_type, int p_hours) {
        double cost = 0.0;
        String query = "SELECT calculateRehearsalCost(?, ?, ?) AS cost";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDate(1, p_date);
            ps.setString(2, p_rate_type);
            ps.setInt(3, p_hours);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cost = rs.getDouble("cost");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return cost;
    }

    /**
     * Calls the MySQL stored function {@code calculateRoomCost} to determine the cost for a room.
     *
     * @param p_venue_id     the venue ID where the room is located
     * @param p_room_name    the name of the room
     * @param p_duration_type the type of duration for which cost is calculated
     * @return the calculated cost as a {@code double}
     */
    public double calculateRoomCost(int p_venue_id, String p_room_name, String p_duration_type) {
        double cost = 0.0;
        String query = "SELECT calculateRoomCost(?, ?, ?) AS cost";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, p_venue_id);
            ps.setString(2, p_room_name);
            ps.setString(3, p_duration_type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cost = rs.getDouble("cost");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return cost;
    }

    /**
     * Calls the MySQL stored function {@code calculateVenueCost} to determine the cost for a venue.
     *
     * @param p_date         the booking date
     * @param p_booking_type the type of booking
     * @return the calculated cost as a {@code double}
     */
    public double calculateVenueCost(java.sql.Date p_date, String p_booking_type) {
        double cost = 0.0;
        String query = "SELECT calculateVenueCost(?, ?) AS cost";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDate(1, p_date);
            ps.setString(2, p_booking_type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cost = rs.getDouble("cost");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return cost;
    }

    /**
     * Retrieves event details for a given booking ID by joining the Event and Venue tables.
     * <p>
     * Fields returned include event_id, name, start_date, end_date, start_time, end_time,
     * a default event_type ('N/A'), and venue_name.
     * </p>
     *
     * @param bookingId the booking ID used to filter event details
     * @return a {@code ResultSet} containing event details, or {@code null} if an error occurs
     */
    public ResultSet getEventDetails(int bookingId) {
        String query = "SELECT e.event_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "'N/A' AS event_type, v.venue_name " +
                "FROM Event e " +
                "JOIN Venue v ON e.venue_id = v.venue_id " +
                "WHERE e.booking_id = ?";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, bookingId);
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves client details for a given booking by joining the Booking and Clients tables.
     *
     * @param bookingId the booking ID used to find the client details
     * @return a {@code ResultSet} containing client details, or {@code null} if an error occurs
     */
    public ResultSet getClientDetails(int bookingId) {
        String query = "SELECT c.`Company Name`, c.`Contact Name`, c.`Phone Number`, c.`Contact Email`, " +
                "c.`Customer Account Number`, c.`Customer Sort Code` " +
                "FROM Clients c " +
                "JOIN Booking b ON c.client_id = b.client_id " +
                "WHERE b.booking_id = ?";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, bookingId);
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves contract details for the specified booking.
     *
     * @param bookingId the booking ID for which the contract details are retrieved
     * @return a {@code ResultSet} containing contract_id, details, and file_data, or {@code null} if an error occurs
     */
    public ResultSet getContractDetails(int bookingId) {
        String query = "SELECT contract_id, details, file_data FROM Contract WHERE booking_id = ?";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, bookingId);
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves event details for a given event ID for calendar view purposes.
     *
     * @param eventId the event ID used to filter the event details
     * @return a {@code ResultSet} containing event details, or {@code null} if an error occurs
     */
    public ResultSet getEventDetailsByEventId(int eventId) {
        String query = "SELECT e.event_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "e.`event_type`, e.description, e.booked_by, v.venue_name " +
                "FROM Event e " +
                "LEFT JOIN Venue v ON e.venue_id = v.venue_id " +
                "WHERE e.event_id = ?";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, eventId);
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves booking details for the given booking ID.
     *
     * @param bookingId the booking ID used to retrieve booking details
     * @return a {@code ResultSet} containing booking details, or {@code null} if an error occurs
     */
    public ResultSet getBookingDetails(int bookingId) {
        String query = "SELECT b.booking_DateStart, b.booking_DateEnd, b.booking_status, b.total_cost, b.payment_status, " +
                "b.client_id, c.`Company Name` AS company_name " +
                "FROM Booking b " +
                "JOIN Clients c ON b.client_id = c.client_id " +
                "WHERE b.booking_id = ?";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, bookingId);
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves film event details and computes available free time intervals for each venue on the given date.
     * <p>
     * The method groups film events by venue and calculates the free time intervals (based on fixed operating hours, 08:00 to 22:00)
     * by subtracting the booked intervals.
     * </p>
     *
     * @param date the date for which the film event availability is calculated
     * @return a {@code Map} where each key is a venue (location) and the value is a {@code String} describing the free time intervals
     */
    public Map<String, String> getFilmEventDetailsWithAvailability(LocalDate date) {
        Map<String, List<Interval>> bookedSlotsByVenue = new HashMap<>();
        Map<String, String> freeTimeByVenue = new HashMap<>();
        String query = "SELECT location, start_time, end_time FROM Event " +
                "WHERE event_type = 'Film' AND start_date = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                String venue = rs.getString("location");
                java.sql.Time sqlStartTime = rs.getTime("start_time");
                java.sql.Time sqlEndTime = rs.getTime("end_time");
                LocalTime startTime = sqlStartTime.toLocalTime();
                LocalTime endTime = sqlEndTime.toLocalTime();
                bookedSlotsByVenue.computeIfAbsent(venue, k -> new ArrayList<>())
                        .add(new Interval(startTime, endTime));
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return freeTimeByVenue;
        }
        // Assume operating hours for each venue: 08:00 to 22:00.
        LocalTime operatingStart = LocalTime.of(8, 0);
        LocalTime operatingEnd = LocalTime.of(22, 0);
        // For each venue, compute free intervals.
        for (Map.Entry<String, List<Interval>> entry : bookedSlotsByVenue.entrySet()) {
            String venue = entry.getKey();
            List<Interval> intervals = entry.getValue();
            intervals.sort(Comparator.comparing(i -> i.start));
            List<String> freeIntervals = new ArrayList<>();
            LocalTime current = operatingStart;
            for (Interval interval : intervals) {
                if (current.isBefore(interval.start)) {
                    freeIntervals.add(current + " to " + interval.start);
                }
                if (current.isBefore(interval.end)) {
                    current = interval.end;
                }
            }
            if (current.isBefore(operatingEnd)) {
                freeIntervals.add(current + " to " + operatingEnd);
            }
            freeTimeByVenue.put(venue, String.join(", ", freeIntervals));
        }
        // Optionally, for venues without any film events, you could add full operating hours.
        return freeTimeByVenue;
    }

    /**
     * A helper class to represent a time interval.
     */
    private static class Interval {
        LocalTime start;
        LocalTime end;

        /**
         * Constructs an {@code Interval} with a start and end time.
         *
         * @param start the start time of the interval
         * @param end   the end time of the interval
         */
        Interval(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * Updates a full booking and its associated records such as booking, invoice, client, events, and contract.
     * <p>
     * This method uses COALESCE in the update queries so that if a passed parameter is {@code null},
     * the current value in the database remains unchanged.
     * </p>
     *
     * @param bookingId         the ID of the booking to update (as a {@code String})
     * @param bookingEventName  the new booking event name (if applicable)
     * @param bookingStartDate  the new booking start date
     * @param bookingEndDate    the new booking end date
     * @param bookingStatus     the new booking status
     * @param companyName       the new company name for the client
     * @param primaryContact    the new primary contact name
     * @param telephone         the new telephone number
     * @param email             the new email address
     * @param events            the list of {@code Event} objects to update
     * @param customerBillTotal the new customer bill total
     * @param ticketPrice       the new ticket price
     * @param customerAccount   the new customer account number
     * @param customerSortCode  the new customer sort code
     * @param streetAddress     the new street address
     * @param city              the new city
     * @param postcode          the new postcode
     * @param paymentDueDate    the new payment due date
     * @param paymentStatus     the new payment status
     * @param contractDetails   the new contract details
     * @param contractFile      the new contract {@code File} (or {@code null} if not updated)
     * @param maxDiscount       the new maximum discount
     * @param staffId           the new staff ID
     * @return {@code true} if the update is successful; {@code false} otherwise
     */
    public boolean updateFullBooking(String bookingId,
                                     String bookingEventName,
                                     LocalDate bookingStartDate,
                                     LocalDate bookingEndDate,
                                     String bookingStatus,
                                     String companyName,
                                     String primaryContact,
                                     String telephone,
                                     String email,
                                     List<Event> events,
                                     double customerBillTotal,
                                     Double ticketPrice,
                                     String customerAccount,
                                     String customerSortCode,
                                     String streetAddress,
                                     String city,
                                     String postcode,
                                     LocalDate paymentDueDate,
                                     String paymentStatus,
                                     String contractDetails,
                                     File contractFile,
                                     Double maxDiscount,
                                     Integer staffId) {
        // Use COALESCE so that if the passed parameter is null, the current value remains.
        String updateBooking = "UPDATE Booking SET " +
                "booking_DateStart = COALESCE(?, booking_DateStart), " +
                "booking_DateEnd = COALESCE(?, booking_DateEnd), " +
                "booking_status = COALESCE(?, booking_status), " +
                "ticket_price = COALESCE(?, ticket_price), " +
                "total_cost = COALESCE(?, total_cost), " +
                "payment_status = COALESCE(?, payment_status), " +
                "payment_due_date = COALESCE(?, payment_due_date), " +
                "staff_id = COALESCE(?, staff_id), " +
                "max_discount = COALESCE(?, max_discount) " +
                "WHERE booking_id = ?";

        String updateInvoice = "UPDATE Invoice SET " +
                "date = COALESCE(?, date), " +
                "total = COALESCE(?, total) " +
                "WHERE booking_id = ?";

        String updateClient = "UPDATE Clients SET " +
                "`Company Name` = COALESCE(?, `Company Name`), " +
                "`Contact Name` = COALESCE(?, `Contact Name`), " +
                "`Phone Number` = COALESCE(?, `Phone Number`), " +
                "`Contact Email` = COALESCE(?, `Contact Email`), " +
                "`Customer Account Number` = COALESCE(?, `Customer Account Number`), " +
                "`Customer Sort Code` = COALESCE(?, `Customer Sort Code`), " +
                "`Payment Due Date` = COALESCE(?, `Payment Due Date`), " +
                "`Street Address` = COALESCE(?, `Street Address`), " +
                "`City` = COALESCE(?, `City`), " +
                "`Postcode` = COALESCE(?, `Postcode`) " +
                "WHERE client_id = (SELECT client_id FROM Booking WHERE booking_id = ?)";

        // NOTE: Removed event_type update.
        String updateEvent = "UPDATE Event SET " +
                "name = COALESCE(?, name), " +
                "start_date = COALESCE(?, start_date), " +
                "end_date = COALESCE(?, end_date), " +
                "start_time = COALESCE(?, start_time), " +
                "end_time = COALESCE(?, end_time), " +
                "venue_id = COALESCE(?, venue_id), " +
                "location = COALESCE(?, location), " +
                "description = COALESCE(?, description), " +
                "layout = COALESCE(?, layout), " +
                "max_discount = COALESCE(?, max_discount) " +
                "WHERE event_id = ?";

        String updateContract = "UPDATE Contract SET " +
                "details = COALESCE(?, details), " +
                "file_data = COALESCE(?, file_data) " +
                "WHERE booking_id = ?";

        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);

            // 1. Update Booking record.
            try (PreparedStatement psBooking = con.prepareStatement(updateBooking)) {
                psBooking.setDate(1, (bookingStartDate != null) ? java.sql.Date.valueOf(bookingStartDate) : null);
                psBooking.setDate(2, (bookingEndDate != null) ? java.sql.Date.valueOf(bookingEndDate) : null);
                psBooking.setString(3, bookingStatus);
                psBooking.setObject(4, ticketPrice, java.sql.Types.DOUBLE);
                psBooking.setDouble(5, customerBillTotal);
                psBooking.setString(6, paymentStatus);
                psBooking.setDate(7, (paymentDueDate != null) ? java.sql.Date.valueOf(paymentDueDate) : null);
                psBooking.setObject(8, staffId, java.sql.Types.INTEGER);
                psBooking.setObject(9, maxDiscount, java.sql.Types.DOUBLE);
                psBooking.setInt(10, Integer.parseInt(bookingId));
                psBooking.executeUpdate();
            }

            // 2. Update Invoice record.
            try (PreparedStatement psInvoice = con.prepareStatement(updateInvoice)) {
                psInvoice.setDate(1, (bookingStartDate != null) ? java.sql.Date.valueOf(bookingStartDate) : null);
                psInvoice.setDouble(2, customerBillTotal);
                psInvoice.setInt(3, Integer.parseInt(bookingId));
                psInvoice.executeUpdate();
            }

            // 3. Update Client record.
            try (PreparedStatement psClient = con.prepareStatement(updateClient)) {
                psClient.setString(1, companyName);
                psClient.setString(2, primaryContact);
                psClient.setString(3, telephone);
                psClient.setString(4, email);
                psClient.setString(5, customerAccount);
                psClient.setString(6, customerSortCode);
                psClient.setDate(7, (paymentDueDate != null) ? java.sql.Date.valueOf(paymentDueDate) : null);
                psClient.setString(8, streetAddress);
                psClient.setString(9, city);
                psClient.setString(10, postcode);
                psClient.setInt(11, Integer.parseInt(bookingId));
                psClient.executeUpdate();
            }

            // 4. Update each Event record.
            for (Event event : events) {
                try (PreparedStatement psEvent = con.prepareStatement(updateEvent)) {
                    psEvent.setString(1, event.getName());
                    psEvent.setDate(2, (event.getStartDate() != null) ? java.sql.Date.valueOf(event.getStartDate()) : null);
                    psEvent.setDate(3, (event.getEndDate() != null) ? java.sql.Date.valueOf(event.getEndDate()) : null);
                    psEvent.setTime(4, (event.getStartTime() != null) ? java.sql.Time.valueOf(event.getStartTime()) : null);
                    psEvent.setTime(5, (event.getEndTime() != null) ? java.sql.Time.valueOf(event.getEndTime()) : null);
                    // Skipping event_type update.
                    Integer venueId = (event.getVenue() != null && event.getVenue().getVenueId() != 0)
                            ? event.getVenue().getVenueId() : null;
                    psEvent.setObject(6, venueId, java.sql.Types.INTEGER);
                    psEvent.setString(7, event.getRoom());
                    psEvent.setString(8, event.getDescription());
                    psEvent.setString(9, event.getLayout());
                    psEvent.setObject(10, maxDiscount, java.sql.Types.DOUBLE);
                    psEvent.setInt(11, event.getId());
                    psEvent.executeUpdate();
                }
            }

            // 5. Update Contract details.
            if (contractFile != null) {
                try (PreparedStatement psContract = con.prepareStatement(updateContract)) {
                    psContract.setString(1, contractDetails != null ? contractDetails : "N/A");
                    psContract.setBlob(2, new java.io.FileInputStream(contractFile));
                    psContract.setInt(3, Integer.parseInt(bookingId));
                    psContract.executeUpdate();
                }
            } else {
                try (PreparedStatement psContract = con.prepareStatement("UPDATE Contract SET details = COALESCE(?, details) WHERE booking_id = ?")) {
                    psContract.setString(1, contractDetails != null ? contractDetails : "N/A");
                    psContract.setInt(2, Integer.parseInt(bookingId));
                    psContract.executeUpdate();
                }
            }

            con.commit();
            System.out.println("Booking " + bookingId + " updated successfully.");
            notifyUpdateListeners("bookingUpdated", bookingId);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Deletes a full booking and its associated records from the database.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Checks if the booking exists and has a "held" status</li>
     *   <li>Deletes the Contract, Invoice, Event, Booking, and Client records associated with the booking</li>
     * </ol>
     *
     * @param bookingId the booking ID to delete
     * @return {@code true} if the booking was successfully deleted; {@code false} otherwise
     */
    public boolean deleteFullBooking(int bookingId) {
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);

            // 1. Check if the booking exists and that its status is "held"
            String checkQuery = "SELECT booking_status, client_id FROM Booking WHERE booking_id = ?";
            int clientId = 0;
            try (PreparedStatement psCheck = con.prepareStatement(checkQuery)) {
                psCheck.setInt(1, bookingId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        String status = rs.getString("booking_status");
                        if (!"held".equalsIgnoreCase(status)) {
                            System.out.println("Booking " + bookingId + " is not held and cannot be deleted.");
                            return false;
                        }
                        clientId = rs.getInt("client_id");
                    } else {
                        System.out.println("Booking " + bookingId + " not found.");
                        return false;
                    }
                }
            }

            // 2. Delete Contract record.
            try (PreparedStatement psContract = con.prepareStatement("DELETE FROM Contract WHERE booking_id = ?")) {
                psContract.setInt(1, bookingId);
                psContract.executeUpdate();
            }

            // 3. Delete Invoice record.
            try (PreparedStatement psInvoice = con.prepareStatement("DELETE FROM Invoice WHERE booking_id = ?")) {
                psInvoice.setInt(1, bookingId);
                psInvoice.executeUpdate();
            }

            // 4. Delete Event records.
            try (PreparedStatement psEvent = con.prepareStatement("DELETE FROM Event WHERE booking_id = ?")) {
                psEvent.setInt(1, bookingId);
                psEvent.executeUpdate();
            }

            // 5. Delete Booking record.
            try (PreparedStatement psBooking = con.prepareStatement("DELETE FROM Booking WHERE booking_id = ?")) {
                psBooking.setInt(1, bookingId);
                psBooking.executeUpdate();
            }

            // 6. Delete Client record.
            try (PreparedStatement psClient = con.prepareStatement("DELETE FROM Clients WHERE client_id = ?")) {
                psClient.setInt(1, clientId);
                psClient.executeUpdate();
            }

            con.commit();
            System.out.println("Booking " + bookingId + " has been deleted.");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
