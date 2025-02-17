package com.lancaster.marketing;

public interface MarketingInterface {

    // --- 1. Calendar Access ---
    /**
     * Retrieves the central calendar view for marketing.
     * @param startDate Start date (String or LocalDate).
     * @param endDate End date (String or LocalDate).
     * @return A list or map of events/bookings within that date range.
     */
    String viewCalendar(String startDate, String endDate);

    /**
     * Notifies Marketing of changes to bookings (Operations triggers this).
     * @param bookingId The ID of the changed/canceled booking.
     * @param changeType e.g., "UPDATED", "CANCELED", "NEW_HOLD".
     */
    void notifyCalendarChange(int bookingId, String changeType);

    // --- 2. Space Availability ---
    /**
     * Gets availability status for each venue/room in a given date range.
     */
    Map<String, String> getSpaceAvailability(String date);

    // --- 3. Seating Plans ---
    /**
     * Retrieves current seating plan details for an event (show/film).
     * @param activityId ID of the Show or Film.
     * @return A seating layout representation (could be JSON or a custom class).
     */
    String getSeatingPlan(int activityId);

    // --- 4. Configuration Requirements ---
    /**
     * Reads how the room is configured for an event (e.g., "theatre style").
     * @param bookingId The booking for which the config is needed.
     * @return Configuration details.
     */
    String getConfigurationDetails(int bookingId);

    // --- 5. Revenue Information ---
    /**
     * Retrieves ticket sales and cost info for a film/event.
     * @param activityId ID of the Film or Show.
     * @return Financial summary (could be JSON, custom object, etc.).
     */
    String getRevenueInfo(int activityId);

    // --- 6. Usage Reports ---
    /**
     * Provides usage stats over a given period.
     * @param startDate Start date (String or LocalDate).
     * @param endDate End date (String or LocalDate).
     * @return Usage or occupancy report data.
     */
    String getUsageReports(String startDate, String endDate);

    // --- 7. Communication on Held Spaces ---
    /**
     * Shows all current holds, their expiry dates, and any notes.
     * @return A list of hold records.
     */
    String getHeldSpaces();

    // --- 8. Film Showings ---
    /**
     * Requests scheduling of a new film.
     * Ensures Main Hall is set to stalls-only seating,
     * and notifies Operations for final confirmation.
     * @param filmId ID of the film to schedule.
     * @param proposedDate Proposed date/time.
     * @return True if scheduling is successful.
     */
    boolean scheduleFilm(int filmId, String proposedDate);

    // --- 9. Daily Sheets ---
    /**
     * Retrieves daily usage sheets from Operations.
     * @param date The date for which the sheet is requested.
     * @return A structured summary of that day’s events/bookings.
     */
    String getDailySheet(String date);
}
