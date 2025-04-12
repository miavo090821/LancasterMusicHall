package boxoffice;

import operations.entities.Event;
import operations.entities.Seat;

import java.time.LocalDate;
import java.util.List;

/**
 * The BoxOfficeInterface defines the contract for box office operations in the venue management system.
 * <p>
 * This interface provides methods for:
 * <ul>
 *   <li>Event calendar access and management</li>
 *   <li>Seating plan configuration and queries</li>
 *   <li>Accessibility seat management</li>
 * </ul>
 *
 * <p><b>Key Responsibilities:</b>
 * <ul>
 *   <li>Maintaining up-to-date event information</li>
 *   <li>Managing seat availability and configurations</li>
 *   <li>Handling accessibility requirements</li>
 *   <li>Providing real-time event data to clients</li>
 * </ul>
 *
 * @see Event
 * @see Seat
 */

public interface BoxOfficeInterface {

    // --- Calendar Access ---

    /**
     * Retrieves all events scheduled within a specified date range.
     * <p>
     * The returned events include comprehensive details about each scheduled activity.
     * </p>
     *
     * @param startDate the inclusive start date of the query range (must not be null)
     * @param endDate the inclusive end date of the query range (must not be null)
     * @return an unmodifiable list of {@link Event} objects containing:
     *         <ul>
     *           <li>Event title and description</li>
     *           <li>Date and time information</li>
     *           <li>Venue/room location details</li>
     *           <li>Applicable ticket pricing</li>
     *           <li>Maximum capacity limits</li>
     *           <li>Current seating configuration</li>
     *         </ul>
     * @throws IllegalArgumentException if either date parameter is null,
     *         or if endDate is before startDate
     */
    List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Notify the Box Office when a Event changes (e.g., if a show is rescheduled or canceled).
     * @param EventId The ID of the Event being updated.
     * @param updatedDetails The new Event details.
     * @return True if the update was successful.
     */
    boolean notifyEventChanges(int EventId, Event updatedDetails);

    // --- Seating Plans ---

    /**
     * Get the seating chart for a specific event.
     * @param EventId The ID of the event.
     * @return A list of seats showing:
     * - Seat row, and number
     * - Whether they are available, sold, held, or have restricted views
     * - If they are wheelchair-accessible or their companions
     */
    List<Seat> getSeatingPlanForEvent(int EventId);

    /**
     * Update the seating chart for an event (e.g., mark seats as wheelchair-accessible or restricted view).
     * @param EventId The ID of the event.
     * @param updatedSeats The new seating details.
     * @return True if the update was successful.
     */
    boolean updateSeatingPlan(int EventId, List<Seat> updatedSeats);

    /**
     * Get a list of seats reserved for wheelchair users and their companions.
     * @param EventId The ID of the event.
     * @return A list of seats specifically held for accessibility.
     */
    List<Seat> getHeldAccessibleSeats(int EventId);
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
//  * @param EventId The ID of the event.
//  * @return A detailed financial breakdown.
//  */
// FinancialRecord getRevenueBreakdownForEvent(int EventId);
