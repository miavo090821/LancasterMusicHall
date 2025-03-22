package marketing;

import java.time.LocalDate;
import java.util.Map;

public interface MarketingInterface {
    // --- 1. Calendar Access ---
    /**
     * Get all bookings scheduled within a specific date range
     * @param startDate The start date.
     * @param endDate The end date.
     * @return A list of bookings happening within this period
     */
    String viewCalendar(LocalDate startDate, LocalDate endDate);

    /**
     *  Notify the system about changes to a booking (e.g., cancellations)
     * @param bookingId The ID of the booking.
     * @param changeType The type of change (e.g., Cancelled).
     */
    void notifyCalendarChange(int bookingId, String changeType);

    // --- 2. Space Availability ---
    /**
     * Check which spaces are available for a date,
     * @param date The date to check.
     * @return a map showing available spaces and details
     */
    Map<String, String> getSpaceAvailability(LocalDate date);

    // --- 3. Seating Plans ---
    /**
     * get the seating plan for a specific activity or event.
     * @param activityId The ID of the activity.
     * @return a string showing the specified activity
     */
    String getSeatingPlan(int activityId);

    // --- 4. Configuration Requirements ---
    /**
     * Get setup requirements for an event .
     * @param bookingId The ID of the booking.
     * @return The configuration details.
     */
    String getConfigurationDetails(int bookingId);

    // --- 5. Revenue Information ---
    /**
     * Get revenue details for an activity.
     * @param activityId The ID of the activity.
     * @return A summary of the revenue generated.
     */
    String getRevenueInfo(int activityId);

    // --- 6. Usage Reports ---
    /**
     * Get reports on space and resource usage over a specific time.
     * @param startDate The start date.
     * @param endDate The end date.
     * @return A usage report.
     */
    String getUsageReports(LocalDate startDate, LocalDate endDate);

    // --- 7. Communication on Held Spaces ---
    /**
     * Get a list of spaces that are being held (reserved but not confirmed).
     * @return A list of held spaces.
     */
    String getHeldSpaces();

    // --- 8. Film Showings ---
    /**
     * Schedule a film screening on a specific time
     * @param filmId The ID of the film.
     * @param proposedDate The proposed screening date.
     * @return True if the scheduling was successful.
     */
    boolean scheduleFilm(int filmId, LocalDate proposedDate);

    // --- 9. Daily Sheets ---
    /**
     * Get a summary of all scheduled events for a given time.
     * @param date The date to check.
     * @return A daily schedule report.
     */
    String getDailySheet(LocalDate date);
}
