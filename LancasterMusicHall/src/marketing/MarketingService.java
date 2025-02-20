package marketing;


import operations.entities.Booking;
import operations.module.CalendarModule;
import operations.module.IncomeTracker;
import operations.module.RoomConfigurationSystem;

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
    public String viewCalendar(String startDate, String endDate) {
        // Fetch bookings within the specified date range
        List<Booking> bookings = calendarModule.getBookingsForDate(startDate, endDate);
        return "Formatted Calendar Data for " + startDate + " to " + endDate + ": " + bookings.toString();
    }

    @Override
    public void notifyCalendarChange(int bookingId, String changeType) {
        // Notify Marketing of booking changes
        System.out.println("Booking " + bookingId + " has been " + changeType);
    }

    // --- 2. Space Availability ---
    @Override
    public Map<String, String> getSpaceAvailability(String date) {
        // Fetch room availability for the specified date
        return roomConfigSystem.getRoomAvailability(date);
    }

    // --- 3. Seating Plans ---
    @Override
    public String getSeatingPlan(int activityId) {
        // Fetch seating plan for the specified activity
        return "Seating Plan for activity " + activityId;
    }

    @Override
    public String getConfigurationDetails(int bookingId) {
        // Step 1: Fetch the booking details
        Booking booking = calendarModule.getBookingById(bookingId); // Assuming CalendarModule has this method
        if (booking == null) {
            return "Booking not found for ID: " + bookingId;
        }

        // Step 2: Fetch the configuration details for the booking
        String configuration = roomConfigSystem.getConfiguration(booking);

        // Step 3: Return the configuration details
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
    public String getUsageReports(String startDate, String endDate) {
        // Fetch usage reports for the specified date range
        return "Usage report from " + startDate + " to " + endDate;
    }

    // --- 7. Communication on Held Spaces ---
    @Override
    public String getHeldSpaces() {
        // Fetch held spaces and their expiry details
        return "List of held spaces with expiry details";
    }

    // --- 8. Film Showings ---
    @Override
    public boolean scheduleFilm(int filmId, String proposedDate) {
        // Schedule a film for the proposed date
        System.out.println("Scheduling film " + filmId + " on " + proposedDate);
        return true; // Assume scheduling is always successful
    }

    // --- 9. Daily Sheets ---
    @Override
    public String getDailySheet(String date) {
        // Fetch daily usage sheet for the specified date
        return "Daily sheet for " + date;
    }
}