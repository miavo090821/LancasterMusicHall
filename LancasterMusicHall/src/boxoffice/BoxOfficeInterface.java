package boxoffice;

import operations.entities.Booking;
import operations.entities.FinancialRecord;
import operations.entities.Seat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BoxOfficeInterface {

    // --- Calendar Access ---

    /**
     * Get all bookings (shows, films, meetings) for a specific date range.
     *
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A list of bookings within the date range, including:
     * - Title
     * - Date
     * - Time
     * - Location (room/venue)
     * - Price
     * - Capacity (expected attendees)
     */
    List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Notify the Box Office of any changes to bookings (e.g., cancellations, rescheduling).
     *
     * @param bookingId The ID of the booking.
     * @param updatedDetails The updated booking details.
     * @return True if the update was successfully communicated.
     */
    boolean notifyBookingChanges(int bookingId, Booking updatedDetails);

    // --- Sales Monitoring ---

    /**
     * Provides real-time or near real-time sales status.
     * @return A summary (could be JSON or a custom object) of ticket sales, seats sold, revenue, etc.
     */
    FinancialRecord getSalesDashboardData();

    // --- Revenue Info ---

    /**
     * Get a breakdown of revenue for a specific booking.
     *
     * @param bookingId The ID of the booking.
     * @return A breakdown of revenue, costs, and profit.
     */
    FinancialRecord getRevenueBreakdownForBooking(int bookingId);

    // --- Seating Plans ---
    /**
     * Get the seating plan for a specific event.
     * @param bookingId The ID of the booking.
     * @return A list of seats in the seating plan, including:
     *         - Seat numbers
     *         - Seat status (available, sold, held, restricted view, wheelchair accessible)
     */
    List<Seat> getSeatingPlanForBooking(int bookingId);

    /**
     * Update seating plan (mark specific seats as wheelchair accessible or restricted view).
     * @param bookingId The ID of the booking.
     * @param updatedSeats A list of updated seats.
     * @return True if the update was successful.
     */
    boolean updateSeatingPlan(int bookingId, List<Seat> updatedSeats);
}