package marketing;

import java.time.LocalDate;
import java.util.Map;

/**
 * The MarketingInterface defines the contract for communication between
 * the marketing system and the core venue management system.
 *
 * <p>This interface provides access to:
 * <ul>
 *   <li>Calendar and booking information</li>
 *   <li>Space availability data</li>
 *   <li>Event configuration details</li>
 *   <li>Financial reporting</li>
 *   <li>Daily operations data</li>
 * </ul>
 *
 * <p>All methods are designed to be stateless and thread-safe.
 */
public interface MarketingInterface {
    // --- 1. Calendar Access ---
    /**
     * Retrieves all bookings scheduled within a specific date range.
     *
     * @param startDate The inclusive start date of the range
     * @param endDate The inclusive end date of the range
     * @return Formatted string containing all bookings in the period,
     *         or empty string if no bookings found
     * @throws IllegalArgumentException if endDate is before startDate
     */
    String viewCalendar(LocalDate startDate, LocalDate endDate);

    /**
     * Notifies the system about changes to an existing booking.
     *
     * @param bookingId The unique ID of the affected booking
     * @param changeType The type of change (e.g., "Cancelled", "Rescheduled")
     * @throws IllegalArgumentException if bookingId doesn't exist or
     *         if changeType is not recognized
     */
    void notifyCalendarChange(int bookingId, String changeType);

    // --- 2. Space Availability ---
    /**
     * Checks venue space availability for a specific date.
     *
     * @param date The date to check availability for
     * @return Map where keys are space names and values are availability details
     *         (e.g., "Available", "Booked: Event Name")
     */
    Map<String, String> getSpaceAvailability(LocalDate date);

    // --- 3. Seating Plans ---
    /**
     * Retrieves the seating plan configuration for a specific activity.
     *
     * @param activityId The ID of the activity/event
     * @return String representation of the seating plan layout,
     *         or empty string if no plan exists
     */
    String getSeatingPlan(int activityId);

    // --- 4. Configuration Requirements ---
    /**
     * Gets the technical setup requirements for an event.
     *
     * @param bookingId The ID of the booking to check
     * @return Formatted string detailing equipment, layout, and other needs,
     *         or empty string if no requirements specified
     */
    String getConfigurationDetails(int bookingId);

    // --- 5. Revenue Information ---
    /**
     * Retrieves revenue details for a specific activity.
     *
     * @param activityId The ID of the activity to report on
     * @return Formatted revenue summary including ticket sales,
     *         concessions, and other income
     */
    String getRevenueInfo(int activityId);

    // --- 6. Usage Reports ---
    /**
     * Generates reports on space and resource utilization.
     *
     * @param startDate The inclusive start date of the reporting period
     * @param endDate The inclusive end date of the reporting period
     * @return Formatted report string with utilization statistics
     *         and analysis
     */
    String getUsageReports(LocalDate startDate, LocalDate endDate);

    // --- 7. Communication on Held Spaces ---
    /**
     * Gets information about spaces that are temporarily reserved.
     *
     * @return Formatted string listing all currently held spaces
     *         with expiration times
     */
    String getHeldSpaces();

    // --- 8. Film Showings ---
    /**
     * Attempts to schedule a film screening at the venue.
     *
     * @param filmId The ID of the film to schedule
     * @param proposedDate The desired screening date
     * @return true if successfully scheduled, false if unavailable
     * @throws IllegalArgumentException if filmId is invalid
     */
    boolean scheduleFilm(int filmId, LocalDate proposedDate);

    // --- 9. Daily Sheets ---
    /**
     * Generates a summary of all scheduled events for a given day.
     *
     * @param date The date to generate the sheet for
     * @return Formatted daily schedule including times,
     *         locations, and event contacts
     */
    String getDailySheet(LocalDate date);
}