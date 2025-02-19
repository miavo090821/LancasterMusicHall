package boxoffice;

import operations.entities.Booking;
import operations.entities.FinancialRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BoxOfficeInterface {

    // --- Calendar Access ---
    /**
     * Get all bookings (shows, films, meetings) for a specific date range.
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @return A list of bookings within the date range, including:
     *         - Title
     *         - Date
     *         - Time
     *         - Location (room/venue)
     *         - Price
     *         - Capacity (expected attendees)
     */
    List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Notify the Box Office of any changes to bookings (e.g., cancellations, rescheduling).
     * @param bookingId The ID of the booking.
     * @param updatedDetails The updated booking details.
     * @return True if the update was successfully communicated.
     */
    boolean notifyBookingChanges(int bookingId, Booking updatedDetails);

    // --- Ticket Management ---
    /**
     * Sells a ticket (or multiple tickets) for a given show/film.
     * @param bookingId The ID of the booking.
     * @param activityId The ID of the Activity (Show or Film).
     * @param seatNumbers List of seat identifiers (rows, seat numbers).
     * @param discountCode Optional discount code or type (NHS, Military, etc.).
     * @return A confirmation or ticket object/ID.
     */
    String sellTickets(int bookingId, int activityId, List<String> seatNumbers, String discountCode);

    /**
     * Checks seat availability for a given activity.
     * @param activityId The ID of the Activity (Show or Film).
     * @return A structure (list/map) of available vs. occupied seats.
     */
    Map<String, Boolean> checkSeatAvailability(int activityId);

    // --- Group Bookings ---
    /**
     * Manages group bookings (up to 12 seats). Larger groups go to Marketing.
     * @param groupName The name of the group.
     * @param activityId The ID of the Activity.
     * @param numberOfSeats Number of seats requested (<= 12).
     * @param discountCode Any special group discount code.
     */
    void holdGroupSeats(String groupName, int activityId, int numberOfSeats, String discountCode);

    // --- Refunds & Special Permissions ---
    /**
     * Process a refund (partial or full).
     * Only accessible to Manager or Deputy (handled in your actual security logic).
     * @param ticketId ID of the ticket or transaction.
     * @param reason The reason for refund.
     * @param amount Amount to refund.
     * @return True if refund is successful.
     */
    boolean processRefund(int ticketId, String reason, double amount);

    // --- Online Ticket Sales Integration ---
    /**
     * Syncs seat sales with an external ticketing website.
     * @return True if sync is successful.
     */
    boolean syncOnlineTicketSales();

    // --- Sales Monitoring ---
    /**
     * Provides real-time or near real-time sales status.
     * @return A summary (could be JSON or a custom object) of ticket sales, seats sold, revenue, etc.
     */
    FinancialRecord getSalesDashboardData();

    // --- Held & Accessible Seats ---
    /**
     * Release held seats (after a certain time or when they are no longer needed).
     * @param bookingId The ID of the booking.
     * @param seatNumbers List of seat numbers to release.
     * @return True if the seats were successfully released.
     */
    boolean releaseHeldSeats(int bookingId, List<String> seatNumbers);

    // --- Revenue Info ---
    /**
     * Get a breakdown of revenue for a specific booking.
     * @param bookingId The ID of the booking.
     * @return A breakdown of revenue, costs, and profit.
     */
    FinancialRecord getRevenueBreakdownForBooking(int bookingId);

    // --- Refund Log ---
    /**
     * Log a refund for a specific booking.
     * @param bookingId The ID of the booking.
     * @param ticketId The ID of the ticket being refunded.
     * @param amount The amount to refund.
     * @param reason The reason for the refund.
     * @return True if the refund was successfully logged.
     */
    boolean logRefund(int bookingId, int ticketId, double amount, String reason);

    // --- Guest Estimates ---
    /**
     * Get guest estimates for a specific booking.
     * @param bookingId The ID of the booking.
     * @return The estimated number of guests.
     */
    int getGuestEstimatesForBooking(int bookingId);
}
/*

// --- Seating Plans ---
 * Get the seating plan for a specific event.
 * @param bookingId The ID of the booking.
 * @return The seating plan for the event, including:
 *         - Seat numbers
 *         - Seat status (available, sold, held, restricted view, wheelchair accessible)

SeatingPlan getSeatingPlanForBooking(int bookingId);

 * Update seating plan (mark specific seats as wheelchair accessible or restricted view).
 * @param bookingId The ID of the booking.
 * @param seatingPlan The updated seating plan.
 * @return True if the update was successful.

boolean updateSeatingPlan(int bookingId, SeatingPlan seatingPlan);

*/