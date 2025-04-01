package marketing;

import operations.entities.Booking;
import operations.module.CalendarModule;
import operations.module.IncomeTracker;
import operations.module.RoomConfigurationSystem;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MarketingService implements MarketingInterface {

    private final CalendarModule calendarModule;
    private final RoomConfigurationSystem roomConfigSystem;
    private final IncomeTracker incomeTracker;

    // Constructor to inject dependencies
    public MarketingService(CalendarModule calendarModule, RoomConfigurationSystem roomConfigSystem, IncomeTracker incomeTracker) {
        this.calendarModule = calendarModule;
        this.roomConfigSystem = roomConfigSystem;
        this.incomeTracker = incomeTracker;
    }

    // --- 1. Calendar Access ---
    @Override
    public String viewCalendar(LocalDate startDate, LocalDate endDate) {
        // Fetch bookings within the specified date range
        List<Booking> bookings = calendarModule.getBookingsForDate(startDate, endDate);
        StringBuilder calendarData = new StringBuilder();
        for (Booking booking : bookings) {
            calendarData.append("Booking ID: ").append(booking.getId())
                    .append(", Activity: ").append(booking.getActivityName())
                    .append(", Start Date: ").append(booking.getStartDate())
                    .append(", End Date: ").append(booking.getEndDate())
                    .append("\n");
        }
        return calendarData.toString();
    }

    @Override
    public void notifyCalendarChange(int bookingId, String changeType) {
        // Notify Marketing of booking changes
        System.out.println("Booking " + bookingId + " has been " + changeType);
    }

    // --- 2. Space Availability ---
    @Override
    public Map<String, String> getSpaceAvailability(LocalDate date) {
        // Fetch room availability for the specified date
        return roomConfigSystem.getRoomAvailability(date);
    }

    // --- 3. Seating Plans ---
    @Override
    public String getSeatingPlan(int activityId) {
        // Fetch seating plan for the specified activity
        return roomConfigSystem.getSeatingPlan(activityId);
    }

    @Override
    public String getConfigurationDetails(int bookingId) {
        // Fetch the booking details
        Booking booking = calendarModule.getBookingById(bookingId);
        if (booking == null) {
            return "Booking not found for ID: " + bookingId;
        }

        // Fetch the configuration details for the booking
        String configuration = roomConfigSystem.getConfiguration(booking);
        return "Configuration details for booking ID " + bookingId + ":\n" + configuration;
    }



    // --- 5. Revenue Information ---
    @Override
    public String getRevenueInfo(int activityId) {
        // Fetch revenue information for the specified activity
        return incomeTracker.getRevenueForActivity(activityId);
    }

    // --- 6. Usage Reports ---
    @Override
    public String getUsageReports(LocalDate startDate, LocalDate endDate) {
        // Fetch usage reports for the specified date range
        return incomeTracker.getUsageReport(startDate, endDate);
    }

    // --- 7. Communication on Held Spaces ---
    @Override
    public String getHeldSpaces() {
        // Fetch held spaces and their expiry details
        return roomConfigSystem.getHeldSpaces();
    }

    // --- 8. Film Showings ---
    @Override
    public boolean scheduleFilm(int filmId, LocalDate proposedDate) {
        // Schedule a film for the proposed date
        return calendarModule.scheduleFilm(filmId, proposedDate);
    }

    // --- 9. Daily Sheets ---
    @Override
    public String getDailySheet(LocalDate date) {
        // Fetch daily usage sheet for the specified date
        return incomeTracker.getDailySheet(date);
    }
}