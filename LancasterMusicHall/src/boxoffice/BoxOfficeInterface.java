package boxoffice;

import operations.entities.Booking;
import operations.entities.FinancialRecord;
import operations.entities.Seat;

import java.time.LocalDate;
import java.util.List;

public interface BoxOfficeInterface {

    // --- Calendar Access ---

    /**
     * Get all bookings (shows, films, meetings, etc.) scheduled within a specific date range.
     * @param startDate The start of the date range.
     * @param endDate   The end of the date range.
     * @return A list of bookings happening within this period, including:
     * - Title
     * - Date & Time
     * - Location (room/venue)
     * - Ticket price
     * - Expected attendance
     */
    List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Notify the Box Office when a booking changes (e.g., a show is rescheduled or canceled).
     * @param bookingId The ID of the booking being updated.
     * @param updatedDetails The new booking details.
     * @return True if the update was successful.
     */
    boolean notifyBookingChanges(int bookingId, Booking updatedDetails);

    // --- Seating Plans ---

    /**
     * Get the seating chart for a specific event.
     * @param bookingId The ID of the event.
     * @return A list of seats showing:
     * - Seat numbers
     * - Whether they are available, sold, held, or have restricted views
     * - If they are wheelchair-accessible
     */
    List<Seat> getSeatingPlanForBooking(int bookingId);

    /**
     * Update the seating chart for an event (e.g., mark seats as wheelchair-accessible or restricted view).
     * @param bookingId The ID of the event.
     * @param updatedSeats The new seating details.
     * @return True if the update was successful.
     */
    boolean updateSeatingPlan(int bookingId, List<Seat> updatedSeats);

    /**
     * Get a list of seats reserved for wheelchair users and their companions.
     * @param bookingId The ID of the event.
     * @return A list of seats specifically held for accessibility.
     */
    List<Seat> getHeldAccessibleSeats(int bookingId);
}

// Not sure if they need these features, since they weren’t mentioned in the requirements.
// Leaving them here just in case.

// --- Sales Monitoring ---
//
// /**
//  * Get live ticket sales data, including the number of tickets sold and total revenue.
//  * @return A report on current sales numbers.
//  */
// FinancialRecord getSalesDashboardData();
//
// --- Revenue Info ---
//
// /**
//  * Get a financial report for a specific event, including revenue, costs, and profit.
//  * @param bookingId The ID of the event.
//  * @return A detailed financial breakdown.
//  */
// FinancialRecord getRevenueBreakdownForBooking(int bookingId);
