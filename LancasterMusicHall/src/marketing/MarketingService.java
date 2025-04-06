package marketing;

import Database.SQLConnection;
import com.sun.jdi.connect.spi.Connection;
import operations.entities.Booking;
import operations.module.CalendarModule;
import operations.module.IncomeTracker;
import operations.module.RoomConfigurationSystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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

    // --- 1. Calendar Access ---
    @Override
    public String viewCalendar(LocalDate startDate, LocalDate endDate) {
        StringBuilder calendarData = new StringBuilder();
        try (ResultSet rs = sqlCon.getCalendarBookings(startDate, endDate)) {
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

    // --- 4. Booking Details (Configuration Details) ---
    @Override
    public String getConfigurationDetails(int bookingId) {
        StringBuilder details = new StringBuilder();
        try {
            // Use getBookingDetails method from SQLConnection.
            ResultSet rsBooking = sqlCon.getBookingDetails(bookingId);
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
            rsBooking.close();
            // Optionally, you could also use getBookingsTableModel() to display a table.
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error retrieving booking configuration details.";
        }
        return details.toString();
    }

    // --- 5. Revenue Information ---
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

    // --- 8. Film Showings ---
    @Override
    public boolean scheduleFilm(int filmId, LocalDate proposedDate) {
        // Use the new SQLConnection method to get film event details and availability.
        Map<String, String> freeTime = sqlCon.getFilmEventDetailsWithAvailability(proposedDate);
        System.out.println("Free time slots for venues on " + proposedDate + ":");
        for (Map.Entry<String, String> entry : freeTime.entrySet()) {
            System.out.println("Venue: " + entry.getKey() + " | Free Slots: " + entry.getValue());
        }
        // Delegate to the calendar module (or incorporate further scheduling logic as needed)
        return calendarModule.scheduleFilm(filmId, proposedDate);
    }

    // --- 9. Daily Sheets ---
    @Override
    public String getDailySheet(LocalDate date) {
        StringBuilder sheet = new StringBuilder();
        try (ResultSet rs = sqlCon.getDailySheet(date)) {
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