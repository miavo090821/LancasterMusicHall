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

public class MarketingService implements MarketingInterface {

    private final CalendarModule calendarModule;
    private final RoomConfigurationSystem roomConfigSystem;
    private final IncomeTracker incomeTracker;
    private final SQLConnection sqlCon; // SQL connection dependency

    // Constructor with SQLConnection injection
    public MarketingService(CalendarModule calendarModule, RoomConfigurationSystem roomConfigSystem, IncomeTracker incomeTracker, SQLConnection sqlCon) {
        this.calendarModule = calendarModule;
        this.roomConfigSystem = roomConfigSystem;
        this.incomeTracker = incomeTracker;
        this.sqlCon = sqlCon;
    }

    // ------------------ SQL Methods Directly in Marketing ------------------

    // 1. Get Calendar Bookings: Retrieve bookings between startDate and endDate.
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

    // 2. Get Booking Details: Retrieve booking details joined with client info.
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

    // 3. Get Client Details: Retrieve client info for a booking.
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

    // 4. Get Event Details: Retrieve events for a booking (joined with Venue).
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

    // 5. Get Contract Details: Retrieve contract data for a booking.
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

    // 6. Get Film Event Details With Availability: Retrieve film events on a date and calculate free slots.
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

    // Helper class for time intervals.
    private static class Interval {
        LocalTime start;
        LocalTime end;
        Interval(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }
    }

    // 7. Fetch Daily Sheet: Retrieve summary of events on a given date.
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

    @Override
    public void notifyCalendarChange(int bookingId, String changeType) {
        System.out.println("Booking " + bookingId + " has been " + changeType);
    }

    @Override
    public Map<String, String> getSpaceAvailability(LocalDate date) {
        return roomConfigSystem.getRoomAvailability(date);
    }

    @Override
    public String getSeatingPlan(int activityId) {
        return roomConfigSystem.getSeatingPlan(activityId);
    }

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

    @Override
    public String getRevenueInfo(int activityId) {
        return incomeTracker.getRevenueForActivity(activityId);
    }

    @Override
    public String getUsageReports(LocalDate startDate, LocalDate endDate) {
        return incomeTracker.getUsageReport(startDate, endDate);
    }

    @Override
    public String getHeldSpaces() {
        return roomConfigSystem.getHeldSpaces();
    }

    @Override
    public boolean scheduleFilm(int filmId, LocalDate proposedDate) {
        Map<String, String> freeTime = getFilmEventDetailsWithAvailability(proposedDate);
        System.out.println("Free time slots for venues on " + proposedDate + ":");
        for (Map.Entry<String, String> entry : freeTime.entrySet()) {
            System.out.println("Venue: " + entry.getKey() + " | Free Slots: " + entry.getValue());
        }
        return calendarModule.scheduleFilm(filmId, proposedDate);
    }

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
