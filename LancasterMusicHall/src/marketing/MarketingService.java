package marketing;

import Database.SQLConnection;
import operations.entities.Event;
import operations.entities.Venue;
import operations.module.CalendarModule;
import operations.module.IncomeTracker;
import operations.module.RoomConfigurationSystem;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code MarketingService} class implements the {@code MarketingInterface} and provides
 * methods to retrieve and manipulate marketing-related data, including calendar bookings,
 * booking details, client details, event details, contract details, film event availability,
 * daily sheets, and revenue and usage reports.
 * <p>
 * This class relies on injected dependencies for calendar operations, room configuration,
 * income tracking, and SQL connections.
 * </p>
 */
public class MarketingService implements MarketingInterface {

    private final CalendarModule calendarModule;
    private final RoomConfigurationSystem roomConfigSystem;
    private final IncomeTracker incomeTracker;
    private final SQLConnection sqlCon; // SQL connection dependency

    /**
     * Constructs a new {@code MarketingService} with the specified modules and SQL connection.
     *
     * @param calendarModule  The {@link CalendarModule} used for scheduling and calendar operations.
     * @param roomConfigSystem The {@link RoomConfigurationSystem} used to manage room configurations and availability.
     * @param incomeTracker   The {@link IncomeTracker} used for tracking revenue and usage.
     * @param sqlCon          The {@link SQLConnection} used for executing SQL queries.
     */
    public MarketingService(CalendarModule calendarModule, RoomConfigurationSystem roomConfigSystem, IncomeTracker incomeTracker, SQLConnection sqlCon) {
        this.calendarModule = calendarModule;
        this.roomConfigSystem = roomConfigSystem;
        this.incomeTracker = incomeTracker;
        this.sqlCon = sqlCon;
    }

    // ------------------ SQL Methods Directly in Marketing ------------------

    /**
     * Retrieves calendar bookings that have start and end dates within the specified range.
     *
     * @param startDate The start date of the booking range.
     * @param endDate   The end date of the booking range.
     * @return A {@link ResultSet} containing booking_id, booking_DateStart, booking_DateEnd, and booking_status,
     *         or {@code null} if an SQL exception occurs.
     */
    public ResultSet getCalendarBookings(LocalDate startDate, LocalDate endDate) {
        String query = "SELECT booking_id, booking_DateStart, booking_DateEnd, booking_status " +
                "FROM Booking WHERE booking_DateStart >= ? AND booking_DateEnd <= ?";
        try {
            Connection con = sqlCon.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves detailed booking information including client company name for a given booking ID.
     *
     * @param bookingId The booking ID for which the details are requested.
     * @return A {@link ResultSet} containing booking details such as booking_DateStart, booking_DateEnd, booking_status,
     *         total_cost, payment_status, client_id, and company_name; or {@code null} if an SQL exception occurs.
     */
    public ResultSet getBookingDetails(int bookingId) {
        String query = "SELECT b.booking_DateStart, b.booking_DateEnd, b.booking_status, b.total_cost, b.payment_status, " +
                "b.client_id, c.`Company Name` AS company_name " +
                "FROM Booking b JOIN Clients c ON b.client_id = c.client_id " +
                "WHERE b.booking_id = ?";
        try {
            Connection con = sqlCon.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, bookingId);
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves client contact details associated with a specific booking.
     *
     * @param bookingId The booking ID for which client details are requested.
     * @return A {@link ResultSet} containing client contact information such as Contact Name, Phone Number,
     *         and Contact Email, or {@code null} if an SQL exception occurs.
     */
    public ResultSet getClientDetails(int bookingId) {
        String query = "SELECT c.`Contact Name`, c.`Phone Number`, c.`Contact Email` " +
                "FROM Clients c JOIN Booking b ON c.client_id = b.client_id " +
                "WHERE b.booking_id = ?";
        try {
            Connection con = sqlCon.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, bookingId);
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves event details associated with a specific booking, joined with venue details.
     *
     * @param bookingId The booking ID for which event details are requested.
     * @return A {@link ResultSet} containing event details such as event_id, name, start_date, end_date,
     *         start_time, end_time, event_type, and venue_name, or {@code null} if an SQL exception occurs.
     */
    public ResultSet getEventDetails(int bookingId) {
        String query = "SELECT e.event_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "e.event_type, v.venue_name " +
                "FROM Event e JOIN Venue v ON e.venue_id = v.venue_id " +
                "WHERE e.booking_id = ?";
        try {
            Connection con = sqlCon.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, bookingId);
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves contract details for a given booking.
     *
     * @param bookingId The booking ID for which contract details are requested.
     * @return A {@link ResultSet} containing contract_id, details, and file_data,
     *         or {@code null} if an SQL exception occurs.
     */
    public ResultSet getContractDetails(int bookingId) {
        String query = "SELECT contract_id, details, file_data FROM Contract WHERE booking_id = ?";
        try {
            Connection con = sqlCon.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, bookingId);
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves film event details with available free time intervals for a specified date.
     * <p>
     * This method calculates free time slots for each venue by comparing existing film events with
     * assumed operating hours (08:00 to 22:00).
     * </p>
     *
     * @param date The date for which film event availability is requested.
     * @return A {@link Map} where keys are venue names and values are comma-separated strings representing free time intervals.
     */
    public Map<String, String> getFilmEventDetailsWithAvailability(LocalDate date) {
        Map<String, List<Interval>> bookedSlotsByVenue = new HashMap<>();
        Map<String, String> freeTimeByVenue = new HashMap<>();
        String query = "SELECT location, start_time, end_time FROM Event " +
                "WHERE event_type = 'Film' AND start_date = ?";
        try (Connection con = sqlCon.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                String venue = rs.getString("location");
                Time sqlStartTime = rs.getTime("start_time");
                Time sqlEndTime = rs.getTime("end_time");
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
        // Assume operating hours: 08:00 to 22:00.
        LocalTime operatingStart = LocalTime.of(8, 0);
        LocalTime operatingEnd = LocalTime.of(22, 0);
        for (Map.Entry<String, List<Interval>> entry : bookedSlotsByVenue.entrySet()){
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
        return freeTimeByVenue;
    }

    /**
     * A helper class representing a time interval with a start and end time.
     */
    private static class Interval {
        LocalTime start;
        LocalTime end;

        /**
         * Constructs a new {@code Interval} with the specified start and end times.
         *
         * @param start The start time of the interval.
         * @param end   The end time of the interval.
         */
        Interval(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * Fetches a daily sheet summary of events for the given date.
     *
     * @param date The date for which to fetch the daily sheet.
     * @return A {@link ResultSet} containing event_id, name, start_time, and end_time,
     *         or {@code null} if an SQL exception occurs.
     */
    public ResultSet fetchDailySheet(LocalDate date) {
        String query = "SELECT event_id, name, start_time, end_time FROM Event WHERE start_date = ?";
        try {
            Connection con = sqlCon.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(date));
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // ------------------ Marketing Interface Methods ------------------

    /**
     * Returns a string representation of the calendar bookings within the specified date range.
     *
     * @param startDate The starting date for the calendar view.
     * @param endDate   The ending date for the calendar view.
     * @return A string listing booking details including Booking ID, Start Date, End Date, and Status,
     *         or an error message if data retrieval fails.
     */
    @Override
    public String viewCalendar(LocalDate startDate, LocalDate endDate) {
        StringBuilder calendarData = new StringBuilder();
        try (ResultSet rs = getCalendarBookings(startDate, endDate)) {
            if (rs != null) {
                while (rs.next()) {
                    calendarData.append("Booking ID: ").append(rs.getInt("booking_id")).append(", ");
                    calendarData.append("Start Date: ").append(rs.getDate("booking_DateStart")).append(", ");
                    calendarData.append("End Date: ").append(rs.getDate("booking_DateEnd")).append(", ");
                    calendarData.append("Status: ").append(rs.getString("booking_status")).append("\n");
                }
            } else {
                return "Error retrieving calendar data.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error retrieving calendar data.";
        }
        return calendarData.toString();
    }

    /**
     * Notifies that a change has occurred on the calendar for a specific booking.
     *
     * @param bookingId The booking ID that has been changed.
     * @param changeType A string describing the type of change.
     */
    @Override
    public void notifyCalendarChange(int bookingId, String changeType) {
        System.out.println("Booking " + bookingId + " has been " + changeType);
    }

    /**
     * Retrieves space availability based on the provided date.
     *
     * @param date The date for which room availability is requested.
     * @return A {@link Map} containing availability information returned from the room configuration system.
     */
    @Override
    public Map<String, String> getSpaceAvailability(LocalDate date) {
        return roomConfigSystem.getRoomAvailability(date);
    }

    /**
     * Retrieves the seating plan for a given activity.
     *
     * @param activityId The ID of the activity.
     * @return A string representing the seating plan for the activity.
     */
    @Override
    public String getSeatingPlan(int activityId) {
        return roomConfigSystem.getSeatingPlan(activityId);
    }

    /**
     * Retrieves the configuration details of a booking including dates, status, total cost,
     * payment status, and client company name.
     *
     * @param bookingId The booking ID for which configuration details are requested.
     * @return A string containing the booking configuration details, or an error message if the booking is not found.
     */
    @Override
    public String getConfigurationDetails(int bookingId) {
        StringBuilder details = new StringBuilder();
        try (ResultSet rsBooking = getBookingDetails(bookingId)) {
            if (rsBooking.next()) {
                details.append("Booking Start Date: ").append(rsBooking.getString("booking_DateStart")).append("\n");
                details.append("Booking End Date: ").append(rsBooking.getString("booking_DateEnd")).append("\n");
                details.append("Status: ").append(rsBooking.getString("booking_status")).append("\n");
                details.append("Total Cost: ").append(rsBooking.getString("total_cost")).append("\n");
                details.append("Payment Status: ").append(rsBooking.getString("payment_status")).append("\n");
                details.append("Company Name: ").append(rsBooking.getString("company_name")).append("\n");
            } else {
                return "No booking found for ID: " + bookingId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error retrieving booking configuration details.";
        }
        return details.toString();
    }

    /**
     * Retrieves revenue information for a specified activity.
     *
     * @param activityId The ID of the activity.
     * @return A string representing the revenue information for the activity.
     */
    @Override
    public String getRevenueInfo(int activityId) {
        return incomeTracker.getRevenueForActivity(activityId);
    }

    /**
     * Retrieves a usage report for a specified date range.
     *
     * @param startDate The starting date of the usage report.
     * @param endDate   The ending date of the usage report.
     * @return A string representing the usage report for the given date range.
     */
    @Override
    public String getUsageReports(LocalDate startDate, LocalDate endDate) {
        return incomeTracker.getUsageReport(startDate, endDate);
    }

    /**
     * Retrieves information about held spaces from the room configuration system.
     *
     * @return A string representing the held spaces.
     */
    @Override
    public String getHeldSpaces() {
        return roomConfigSystem.getHeldSpaces();
    }

    /**
     * Schedules a film event by retrieving free time slots for the specified date and then delegating
     * the scheduling to the calendar module.
     *
     * @param filmId       The ID of the film event to schedule.
     * @param proposedDate The proposed date for the film event.
     * @return {@code true} if the film event is successfully scheduled; {@code false} otherwise.
     */
    @Override
    public boolean scheduleFilm(int filmId, LocalDate proposedDate) {
        Map<String, String> freeTime = getFilmEventDetailsWithAvailability(proposedDate);
        System.out.println("Free time slots for venues on " + proposedDate + ":");
        for (Map.Entry<String, String> entry : freeTime.entrySet()) {
            System.out.println("Venue: " + entry.getKey() + " | Free Slots: " + entry.getValue());
        }
        return calendarModule.scheduleFilm(filmId, proposedDate);
    }

    /**
     * Retrieves a daily sheet summarizing events for the given date.
     *
     * @param date The date for which to retrieve the daily sheet.
     * @return A string representation of the daily sheet including event IDs, names, start times,
     *         and end times, or an error message if data retrieval fails.
     */
    @Override
    public String getDailySheet(LocalDate date) {
        StringBuilder sheet = new StringBuilder();
        try (ResultSet rs = fetchDailySheet(date)) {
            if (rs != null) {
                while (rs.next()) {
                    sheet.append("Event ID: ").append(rs.getInt("event_id")).append(", ");
                    sheet.append("Name: ").append(rs.getString("name")).append(", ");
                    sheet.append("Start Time: ").append(rs.getTime("start_time")).append(", ");
                    sheet.append("End Time: ").append(rs.getTime("end_time")).append("\n");
                }
            } else {
                return "Error retrieving daily sheet.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error retrieving daily sheet.";
        }
        return sheet.toString();
    }
}
