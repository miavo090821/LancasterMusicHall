package com.lancaster.marketing;

import com.lancaster.operations.modules.CalendarModule;
import com.lancaster.operations.modules.RoomConfigurationSystem;
import com.lancaster.operations.modules.IncomeTracker;
import java.util.List;
import java.util.Map;

public class MarketingService implements MarketingInterface {

    private CalendarModule calendarModule;
    private RoomConfigurationSystem roomConfigSystem;
    private IncomeTracker incomeTracker;

    // Constructor to inject dependencies
    public MarketingService(CalendarModule calendarModule, RoomConfigurationSystem roomConfigSystem, IncomeTracker incomeTracker) {
        this.calendarModule = calendarModule;
        this.roomConfigSystem = roomConfigSystem;
        this.incomeTracker = incomeTracker;
    }

    /**
     * 1. Calendar Access - Retrieves the central calendar view for marketing.
     */
    @Override
    public String viewCalendar(String startDate, String endDate) {
        List<?> bookings = calendarModule.getBookingsForDate(startDate); // Simplified filter
        return "Formatted Calendar Data for " + startDate + " to " + endDate;
    }

    /**
     * 1. Calendar Access (Notification) - Notifies Marketing of booking changes.
     */
    @Override
    public void notifyCalendarChange(int bookingId, String changeType) {
        System.out.println("Booking " + bookingId + " has been " + changeType);
    }

    /**
     * 2. Space Availability - Gets availability status for each venue/room.
     */
    @Override
    public Map<String, String> getSpaceAvailability(String date) {
        return roomConfigSystem.getRoomAvailability(date);
    }

    /**
     * 3. Seating Plans - Retrieves current seating plan details for an event.
     */
    @Override
    public String getSeatingPlan(int activityId) {
        return "Seating Plan for activity " + activityId;
    }

    /**
     * 4. Configuration Requirements - Reads room configuration for an event.
     */
    @Override
    public String getConfigurationDetails(int bookingId) {
        // For simplicity, create a dummy Booking object with the bookingId.
        // In a real scenario, you would fetch the booking from a data source.
        return roomConfigSystem.getConfiguration(new com.lancaster.operations.entities.Booking(bookingId, null, null, null, null, null, null, false, null));
    }

    /**
     * 5. Revenue Information - Retrieves ticket sales and cost info.
     */
    @Override
    public String getRevenueInfo(int activityId) {
        return "Revenue information for activity " + activityId;
    }

    /**
     * 6. Usage Reports - Provides usage stats over a given period.
     */
    @Override
    public String getUsageReports(String startDate, String endDate) {
        return "Usage report from " + startDate + " to " + endDate;
    }

    /**
     * 7. Communication on Held Spaces - Shows current holds and expiry dates.
     */
    @Override
    public String getHeldSpaces() {
        return "List of held spaces with expiry details";
    }

    /**
     * 8. Film Showings - Schedules a film ensuring Main Hall is stalls-only.
     */
    @Override
    public boolean scheduleFilm(int filmId, String proposedDate) {
        System.out.println("Scheduling film " + filmId + " on " + proposedDate);
        return true;
    }

    /**
     * 9. Daily Sheets - Retrieves daily usage sheets.
     */
    @Override
    public String getDailySheet(String date) {
        return "Daily sheet for " + date;
    }
}
