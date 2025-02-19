package marketing;

import java.util.Map;

public interface MarketingInterface {
    // --- 1. Calendar Access ---
    String viewCalendar(String startDate, String endDate);
    void notifyCalendarChange(int bookingId, String changeType);

    // --- 2. Space Availability ---
    Map<String, String> getSpaceAvailability(String date);

    // --- 3. Seating Plans ---
    String getSeatingPlan(int activityId);

    // --- 4. Configuration Requirements ---
    String getConfigurationDetails(int bookingId);

    // --- 5. Revenue Information ---
    String getRevenueInfo(int activityId);

    // --- 6. Usage Reports ---
    String getUsageReports(String startDate, String endDate);

    // --- 7. Communication on Held Spaces ---
    String getHeldSpaces();

    // --- 8. Film Showings ---
    boolean scheduleFilm(int filmId, String proposedDate);

    // --- 9. Daily Sheets ---
    String getDailySheet(String date);
}
